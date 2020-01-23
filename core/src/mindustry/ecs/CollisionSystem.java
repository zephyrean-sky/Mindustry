package mindustry.ecs;

import arc.ecs.*;
import arc.ecs.annotations.*;
import mindustry.annotations.Annotations.*;
import mindustry.ecs.Components.*;

@All({Posc.class, Hitboxc.class})
@AutoSystem
public class CollisionSystem extends BaseEntitySystem{
    private QuadTreeSystem quad;
    private Mapper<Posc> posm;
    private Mapper<Hitboxc> hitm;

    @Override
    protected void processSystem(){
        /*
        things that need to happen:
        1. damagers, such as [fire / puddles / bullets] colliding with all units
        2. units colliding with all units
        */
    }
}
