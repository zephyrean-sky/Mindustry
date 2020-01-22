package mindustry.ecs;

import arc.ecs.*;
import arc.ecs.annotations.*;
import arc.ecs.utils.*;
import mindustry.ecs.Components.*;

@All({Pos.class, Hitbox.class})
public class QuadTreeSystem extends BaseEntitySystem{
    private final QuadTree quad = new QuadTree(0f, 0f, 100f, 200f);
    private Mapper<Pos> posm;
    private Mapper<Hitbox> hitm;

    @Override
    protected void inserted(int e){
        Pos position = posm.get(e);
        Hitbox size = hitm.get(e);
        quad.insert(e, position.x, position.y, size.width, size.height);
    }

    @Override
    protected void processSystem(){
        IntBag actives = subscription.getEntities();
        int[] ids = actives.getData();
        for(int i = 0, s = actives.size(); s > i; i++){
            int e = ids[i];

            Pos position = posm.get(e);
            Hitbox size = hitm.get(e);
            quad.update(e, position.x, position.y, size.width, size.height);
        }
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