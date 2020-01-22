package mindustry.ecs;

import arc.ecs.*;
import arc.ecs.annotations.*;
import arc.ecs.systems.*;
import mindustry.annotations.Annotations.*;
import mindustry.ecs.Components.*;

@All({Posc.class, Hitboxc.class})
@AutoSystem
public class QuadTreeSystem extends IteratingSystem{
    private final QuadTree quad = new QuadTree(0f, 0f, 100f, 200f);
    private Mapper<Posc> posm;
    private Mapper<Hitboxc> hitm;

    @Override
    protected void inserted(int e){
        Posc position = posm.get(e);
        Hitboxc size = hitm.get(e);
        quad.insert(e, position.x, position.y, size.width, size.height);
    }

    @Override
    protected void process(int e){
        Posc position = posm.get(e);
        Hitboxc size = hitm.get(e);
        quad.update(e, position.x, position.y, size.width, size.height);
    }

    @Override
    protected void removed(int e){
        quad.remove(e);
    }

    @Override
    protected void dispose(){
        quad.dispose();
    }

}