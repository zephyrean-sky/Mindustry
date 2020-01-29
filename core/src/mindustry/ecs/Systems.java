package mindustry.ecs;

import arc.*;
import arc.math.*;
import arc.util.ArcAnnotate.*;
import arc.util.*;
import mindustry.annotations.Annotations.*;
import mindustry.content.*;
import mindustry.ecs.Components.*;
import mindustry.entities.*;
import mindustry.entities.effect.*;
import mindustry.entities.traits.*;
import mindustry.entities.traits.BuilderTrait.*;
import mindustry.entities.type.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.BuildBlock.*;

import java.util.*;

import static mindustry.Vars.*;

public class Systems{

    @AutoSystem
    public static void player(Playerc player, Healthc health){

    }

    @AutoSystem
    public static void timeScale(TimeScalec c){
        c.timeScaleDuration -= Time.delta();
        if(c.timeScaleDuration <= 0f){
            c.timeScale = 1f;
        }
    }

    @AutoSystem
    public static void tile(Tilec tc){
        if(tc.block.idleSound != Sounds.none && tc.block.shouldIdleSound(tc.tile)){
            loops.play(tc.block.idleSound, tc.tile, tc.block.idleSoundVolume);
        }

        tc.block.update(tc.tile);
    }

    @AutoSystem
    public static void health(int entity, Healthc hc){
        if(hc.health <= 0 && !hc.dead){
            hc.dead = true;
            //TODO onDeath event.
            base.delete(entity);
        }
    }

    @AutoSystem
    public static void sync(Posc pos, Syncc sync){

    }

    @AutoSystem
    public static void draw(Posc pos, Drawc draw){

    }

    @AutoSystem
    public static void bullet(Posc pos, Hitboxc hitbox, Bulletc bullet){

    }

    @AutoSystem
    public static void velocity(Posc pos, Velocityc vel){
        pos.x += vel.vec.x;
        pos.y += vel.vec.y;
        vel.vec.scl(1f - vel.drag * Time.delta());
    }

    @AutoSystem
    public static void mine(Posc pos, Minec mine, Itemc items, Teamc teamc){
        Team team = teamc.team;
        final float mineDistance = 70f;

        Tile tile = mine.tile;
        TileEntity core = team.core();

        //TODO
        Unit fakeUnitForBlockAcceptanceRemoveLater = null;

        if(core != null && tile != null && tile.drop() != null && !items.accepts(tile.drop()) && pos.within(core, mineTransferRange)){
            int accepted = core.tile.block().acceptStack(items.stack.item, items.stack.amount, core.tile, fakeUnitForBlockAcceptanceRemoveLater);
            if(accepted > 0){
                Call.transferItemTo(items.stack.item, accepted,
                tile.worldx() + Mathf.range(tilesize / 2f),
                tile.worldy() + Mathf.range(tilesize / 2f), core.tile);
                items.clear();
            }
        }

        if(tile == null || core == null || tile.block() != Blocks.air || !pos.within(tile.worldx(), tile.worldy(), mineDistance)
        || tile.drop() == null || !items.accepts(tile.drop()) || !mine.mines(tile.drop())){
            mine.tile = null;
        }else{
            Item item = tile.drop();
            //TODO rotate the unit, if applicable (? ? ???? ?? ?)
            //unit.rotation = Mathf.slerpDelta(unit.rotation, unit.angleTo(tile.worldx(), tile.worldy()), 0.4f);

            if(Mathf.chance(Time.delta() * (0.06 - item.hardness * 0.01) * mine.speed)){

                if(pos.within(core, mineTransferRange)  && core.tile.block().acceptStack(item, 1, core.tile, fakeUnitForBlockAcceptanceRemoveLater) == 1 && mine.autounload){
                    Call.transferItemTo(item, 1,
                    tile.worldx() + Mathf.range(tilesize / 2f),
                    tile.worldy() + Mathf.range(tilesize / 2f), core.tile);
                }else if(items.accepts(item)){
                    //this is clientside, since items are synced anyway
                    ItemTransfer.transferItemToUnit(item,
                    tile.worldx() + Mathf.range(tilesize / 2f),
                    tile.worldy() + Mathf.range(tilesize / 2f),
                    fakeUnitForBlockAcceptanceRemoveLater);
                }
            }

            if(Mathf.chance(0.06 * Time.delta())){
                Effects.effect(Fx.pulverizeSmall,
                tile.worldx() + Mathf.range(tilesize / 2f),
                tile.worldy() + Mathf.range(tilesize / 2f), 0f, item.color);
            }
        }
    }

    @AutoSystem
    public static void build(Posc pos, Buildc build, Teamc teamc){
        Team team = teamc.team;

        float placeDistance = 220f;
        float finalPlaceDst = state.rules.infiniteResources ? Float.MAX_VALUE : placeDistance;

        Iterator<BuildRequest> it = build.requests.iterator();
        while(it.hasNext()){
            BuildRequest req = it.next();
            Tile tile = world.tile(req.x, req.y);
            if(tile == null || (req.breaking && tile.block() == Blocks.air) || (!req.breaking && (tile.rotation() == req.rotation || !req.block.rotate) && tile.block() == req.block)){
                it.remove();
            }
        }

        TileEntity core = team.core();

        //nothing to build.
        if(build.curr() == null) return;

        //find the next build request
        if(build.requests.size > 1){
            int total = 0;
            BuildRequest req;
            while((pos.dst((req = build.curr()).tile()) > finalPlaceDst || shouldSkip(req, core)) && total < build.requests.size){
                build.requests.removeFirst();
                build.requests.addLast(req);
                total++;
            }
        }

        BuildRequest current = build.curr();

        if(!pos.withinDst(current.tile(), finalPlaceDst)) return;

        Tile tile = world.tile(current.x, current.y);

        if(!(tile.block() instanceof BuildBlock)){
            if(!current.initialized && !current.breaking && Build.validPlace(team, current.x, current.y, current.block, current.rotation)){
                Call.beginPlace(team, current.x, current.y, current.block, current.rotation);
            }else if(!current.initialized && current.breaking && Build.validBreak(team, current.x, current.y)){
                Call.beginBreak(team, current.x, current.y);
            }else{
                build.requests.removeFirst();
                return;
            }
        }

        //TODO
        Unit removeThisLaterWhenECSIsImplementedProperly = null;

        if(tile.entity instanceof BuildEntity && !current.initialized){
            Core.app.post(() -> Events.fire(new BuildSelectEvent(tile, team, (BuilderTrait)removeThisLaterWhenECSIsImplementedProperly, current.breaking)));
            current.initialized = true;
        }

        //if there is no core to build with or no build entity, stop building!
        if((core == null && !state.rules.infiniteResources) || !(tile.entity instanceof BuildEntity)){
            return;
        }

        //otherwise, update it.
        BuildEntity be = tile.ent();

        if(be == null){
            return;
        }

        //TODO rotation: implement later
        //if(unit.dst(tile) <= finalPlaceDst){
        //    unit.rotation = Mathf.slerpDelta(unit.rotation, unit.angleTo(be), 0.4f);
        //}

        if(current.breaking){
            be.deconstruct(removeThisLaterWhenECSIsImplementedProperly, core, 1f / be.buildCost * Time.delta() * build.speed * state.rules.buildSpeedMultiplier);
        }else{
            if(be.construct(removeThisLaterWhenECSIsImplementedProperly, core, 1f / be.buildCost * Time.delta() * build.speed * state.rules.buildSpeedMultiplier, current.hasConfig)){
                if(current.hasConfig){
                    Call.onTileConfig(null, tile, current.config);
                }
            }
        }

        current.stuck = Mathf.equal(current.progress, be.progress);
        current.progress = be.progress;
    }

    /** @return whether this request should be skipped, in favor of the next one. */
    private static boolean shouldSkip(BuildRequest request, @Nullable TileEntity core){
        //requests that you have at least *started* are considered
        if(state.rules.infiniteResources || request.breaking || !request.initialized || core == null) return false;
        return request.stuck && !core.items.has(request.block.requirements);
    }

}
