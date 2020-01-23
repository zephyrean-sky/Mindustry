package mindustry.ecs;

import arc.ecs.*;
import mindustry.*;
import mindustry.annotations.Annotations.*;

@AutoSystem(priority = Integer.MAX_VALUE)
public class InitSystem extends BaseSystem{

    @Override
    protected void initialize(){
        Vars.base = base;
    }

    @Override
    protected void processSystem(){
    }
}
