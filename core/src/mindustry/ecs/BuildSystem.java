package mindustry.ecs;

import arc.*;
import arc.ecs.*;
import arc.ecs.annotations.*;
import arc.ecs.systems.*;
import arc.math.*;
import arc.util.ArcAnnotate.*;
import arc.util.*;
import mindustry.annotations.Annotations.*;
import mindustry.content.*;
import mindustry.ecs.Components.*;
import mindustry.entities.traits.*;
import mindustry.entities.traits.BuilderTrait.*;
import mindustry.entities.type.*;
import mindustry.game.EventType.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.world.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.BuildBlock.*;

import java.util.*;

import static mindustry.Vars.*;

@All({Posc.class, Buildc.class, Teamc.class})
@AutoSystem
public class BuildSystem extends IteratingSystem{
    float placeDistance = 220f;
    
    Mapper<Posc> posm;
    Mapper<Buildc> buildm;
    Mapper<Teamc> teamm;

    @Override
    protected void process(int entity){
        Posc pos = posm.get(entity);
        Buildc build = buildm.get(entity);
        Team team = teamm.get(entity).team;

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
    boolean shouldSkip(BuildRequest request, @Nullable TileEntity core){
        //requests that you have at least *started* are considered
        if(state.rules.infiniteResources || request.breaking || !request.initialized || core == null) return false;
        return request.stuck && !core.items.has(request.block.requirements);
    }
}
