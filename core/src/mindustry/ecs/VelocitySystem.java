package mindustry.ecs;

import arc.ecs.*;
import arc.ecs.annotations.*;
import arc.ecs.systems.*;
import arc.util.*;
import mindustry.annotations.Annotations.*;
import mindustry.ecs.Components.*;

@AutoSystem
@All({Velocityc.class, Posc.class})
public class VelocitySystem extends IteratingSystem{
    Mapper<Velocityc> vel;
    Mapper<Posc> pos;

    @Override
    protected void initialize(){
        //TODO remove
        int unit = base.create(Archetypes.baseUnit);
        Posc p = pos.get(unit);
        p.x = 100f;
        p.y = 100f;
    }

    @Override
    protected void process(int entity){
        Velocityc v = vel.get(entity);
        Posc p = pos.get(entity);

        p.x += v.vec.x;
        p.y += v.vec.y;
        v.vec.scl(1f - v.drag * Time.delta());
    }
}
