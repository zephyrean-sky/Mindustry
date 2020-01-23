package mindustry.ecs;

import arc.ecs.*;
import arc.ecs.annotations.*;
import mindustry.annotations.Annotations.*;
import mindustry.ecs.Components.*;

@All({Posc.class, Hitboxc.class})
@AutoSystem
public class CollisionSystem extends EntitySystem{
    private Mapper<Posc> posm;
    private Mapper<Hitboxc> hitm;

    @Override
    protected void processSystem(){

    }
}
