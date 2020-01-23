package mindustry.ecs;

import arc.ecs.annotations.*;
import arc.ecs.systems.*;
import mindustry.annotations.Annotations.*;
import mindustry.ecs.Components.*;

@All({Drawc.class})
@AutoSystem
public class DrawSystem extends IteratingSystem{
    @Override
    protected void process(int entity){

    }
}
