package mindustry.systems;

import arc.*;
import arc.ecs.*;
import arc.util.*;
import mindustry.annotations.Annotations.*;
import mindustry.content.*;
import mindustry.core.*;
import mindustry.core.GameState.*;
import mindustry.ctype.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.BuildBlock.*;

import java.util.*;

import static mindustry.Vars.*;
import static mindustry.gen.Sys.*;

@AutoSystem
public class LogicSystem extends BaseSystem{

    @Subscribe
    public void wave(WaveEvent e){
        if(world.isZone()){
            world.getZone().updateWave(state.wave);
        }
    }

    @Subscribe
    public void blockDestroy(BlockDestroyEvent event){
        //blocks that get broken are appended to the team's broken block queue
        Tile tile = event.tile;
        Block block = tile.block();
        //skip null entities or un-rebuildables, for obvious reasons; also skip client since they can't modify these requests
        if(tile.entity == null || !tile.block().rebuildable || net.client()) return;

        if(block instanceof BuildBlock){

            BuildEntity entity = tile.ent();

            //update block to reflect the fact that something was being constructed
            if(entity.cblock != null && entity.cblock.synthetic()){
                block = entity.cblock;
            }else{
                //otherwise this was a deconstruction that was interrupted, don't want to rebuild that
                return;
            }
        }

        TeamData data = state.teams.get(tile.getTeam());

        //remove existing blocks that have been placed here.
        //painful O(n) iteration + copy
        for(int i = 0; i < data.brokenBlocks.size; i++){
            BrokenBlock b = data.brokenBlocks.get(i);
            if(b.x == tile.x && b.y == tile.y){
                data.brokenBlocks.removeIndex(i);
                break;
            }
        }

        data.brokenBlocks.addFirst(new BrokenBlock(tile.x, tile.y, tile.rotation(), block.id, tile.entity.config()));
    }

    @Subscribe
    public void blockBuildEnd(BlockBuildEndEvent event){
        if(!event.breaking){
            TeamData data = state.teams.get(event.team);
            Iterator<BrokenBlock> it = data.brokenBlocks.iterator();
            while(it.hasNext()){
                BrokenBlock b = it.next();
                Block block = content.block(b.block);
                if(event.tile.block().bounds(event.tile.x, event.tile.y, Tmp.r1).overlaps(block.bounds(b.x, b.y, Tmp.r2))){
                    it.remove();
                }
            }
        }
    }

    /** Handles the event of content being used by either the player or some block. */
    public void handleContent(UnlockableContent content){
        if(!headless){
            data.unlockContent(content);
        }
    }

    public void play(){
        state.set(State.playing);
        state.wavetime = state.rules.waveSpacing * 2; //grace period of 2x wave time before game starts
        Event.firePlay();

        //add starting items
        if(!world.isZone()){
            for(TeamData team : state.teams.getActive()){
                if(team.hasCore()){
                    TileData entity = team.core();
                    entity.items.clear();
                    for(ItemStack stack : state.rules.loadout){
                        entity.items.add(stack.item, stack.amount);
                    }
                }
            }
        }
    }

    public void reset(){
        state = new GameState();

        base.deleteAll();
        Time.clear();
        Event.fireReset();
    }

    public void runWave(){
        spawner.spawnEnemies();
        state.wave++;
        state.wavetime = world.isZone() && world.getZone().isLaunchWave(state.wave) ? state.rules.waveSpacing * state.rules.launchWaveMultiplier : state.rules.waveSpacing;

        Event.fireWave();
    }

    private void checkGameOver(){
        if(!state.rules.attackMode && state.teams.playerCores().size == 0 && !state.gameOver){
            state.gameOver = true;
            Event.fireGameOver(state.rules.waveTeam);
        }else if(state.rules.attackMode){
            Team alive = null;

            for(TeamData team : state.teams.getActive()){
                if(team.hasCore()){
                    if(alive != null){
                        return;
                    }
                    alive = team.team;
                }
            }

            if(alive != null && !state.gameOver){
                if(world.isZone() && alive == state.rules.defaultTeam){
                    //in attack maps, a victorious game over is equivalent to a launch
                    Call.launchZone();
                }else{
                    Event.fireGameOver(alive);
                }
                state.gameOver = true;
            }
        }
    }

    @Remote(called = Loc.both)
    public static void launchZone(){
        if(!headless){
            ui.hudfrag.showLaunch();
        }

        for(TileData tile : state.teams.playerCores()){
            Fx.launch.at(tile);
        }

        if(world.getZone() != null){
            world.getZone().setLaunched();
        }

        Time.runTask(30f, () -> {
            for(TileData entity : state.teams.playerCores()){
                for(Item item : content.items()){
                    data.addItem(item, entity.items.get(item));
                    Event.fireLaunchItem(item, entity.items.get(item));
                }
                entity.tile.remove();
            }
            state.launched = true;
            state.gameOver = true;
            Event.fireLaunch();
            //manually fire game over event now
            Event.fireGameOver(state.rules.defaultTeam);
        });
    }

    @Remote(called = Loc.both)
    public static void onGameOver(Team winner){
        state.stats.wavesLasted = state.wave;
        ui.restart.show(winner);
        client.setQuiet();
    }

    @Override
    protected void processSystem(){
        if(!state.paused()){
            Time.update();

            if(state.rules.waves && state.rules.waveTimer && !state.gameOver){
                if(!state.rules.waitForWaveToEnd || state.enemies == 0){
                    state.wavetime = Math.max(state.wavetime - Time.delta(), 0);
                }
            }
        }

        if(!net.client()){
            state.enemies = unitGroup.count(b -> b.getTeam() == state.rules.waveTeam && b.countsAsEnemy());

            if(!world.isInvalidMap() && !state.isEditor() && state.rules.canGameOver){
                checkGameOver();
            }

            if(state.wavetime <= 0 && state.rules.waves){
                runWave();
            }
        }
    }

    @Override
    protected boolean checkProcessing(){
        return state.playing();
    }
}
