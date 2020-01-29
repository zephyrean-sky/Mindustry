package mindustry.systems;

import arc.*;
import arc.ecs.*;
import arc.util.*;
import mindustry.core.*;
import mindustry.core.GameState.*;
import mindustry.entities.type.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.game.Teams.*;
import mindustry.gen.*;
import mindustry.type.*;

import static mindustry.Vars.*;

public class GameLogicSystem extends BaseSystem{

    public void play(){
        state.set(State.playing);
        state.wavetime = state.rules.waveSpacing * 2; //grace period of 2x wave time before game starts
        Events.fire(new PlayEvent());

        //add starting items
        if(!world.isZone()){
            for(TeamData team : state.teams.getActive()){
                if(team.hasCore()){
                    TileEntity entity = team.core();
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
        Events.fire(new ResetEvent());
    }

    public void runWave(){
        spawner.spawnEnemies();
        state.wave++;
        state.wavetime = world.isZone() && world.getZone().isLaunchWave(state.wave) ? state.rules.waveSpacing * state.rules.launchWaveMultiplier : state.rules.waveSpacing;

        Events.fire(new WaveEvent());
    }

    private void checkGameOver(){
        if(!state.rules.attackMode && state.teams.playerCores().size == 0 && !state.gameOver){
            state.gameOver = true;
            Events.fire(new GameOverEvent(state.rules.waveTeam));
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
                    Events.fire(new GameOverEvent(alive));
                }
                state.gameOver = true;
            }
        }
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
