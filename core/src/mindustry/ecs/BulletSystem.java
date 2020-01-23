package mindustry.ecs;


import arc.ecs.*;
import arc.ecs.annotations.*;
import arc.ecs.systems.*;
import mindustry.annotations.Annotations.*;
import mindustry.ecs.Components.*;

@All({Posc.class, Bulletc.class, Hitboxc.class})
@AutoSystem
public class BulletSystem extends IteratingSystem{
    private Mapper<Posc> posm;
    private Mapper<Hitboxc> hitm;
    private Mapper<Bulletc> bulletm;

    @Override
    protected void process(int entity){

    }
}

