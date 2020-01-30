package mindustry.entities;

import arc.*;
import arc.graphics.*;
import arc.math.*;
import arc.math.geom.*;
import arc.util.*;
import arc.util.pooling.*;
import mindustry.content.*;
import mindustry.entities.effect.*;
import mindustry.entities.effect.GroundEffectEntity.*;
import mindustry.entities.type.*;

import static arc.Core.camera;
import static mindustry.Vars.*;

public class Effects{
    private static final float shakeFalloff = 10000f;

    private static void shake(float intensity, float duration){
        //TODO call methods of renderer system
        //renderer.shake(intensity, duration);
    }

    public static void shake(float intensity, float duration, float x, float y){
        if(Core.camera == null) return;

        float distance = Core.camera.position.dst(x, y);
        if(distance < 1) distance = 1;

        shake(Mathf.clamp(1f / (distance * distance / shakeFalloff)) * intensity, duration);
    }

    public static void shake(float intensity, float duration, Position loc){
        shake(intensity, duration, loc.getX(), loc.getY());
    }

    public static void createEffect(Effect effect, float x, float y, float rotation, Color color, Object data){
        if(headless || effect == Fx.none) return;
        if(Core.settings.getBool("effects")){
            Rect view = camera.bounds(Tmp.r1);
            Rect pos = Tmp.r2.setSize(effect.size).setCenter(x, y);

            if(view.overlaps(pos)){

                if(!(effect instanceof GroundEffect)){
                    EffectEntity entity = Pools.obtain(EffectEntity.class, EffectEntity::new);
                    entity.effect = effect;
                    entity.color.set(color);
                    entity.rotation = rotation;
                    entity.data = data;
                    entity.id++;
                    entity.set(x, y);
                    if(data instanceof Entity_){
                        entity.setParent((Entity_)data);
                    }
                    effectGroup.add(entity);
                }else{
                    GroundEffectEntity entity = Pools.obtain(GroundEffectEntity.class, GroundEffectEntity::new);
                    entity.effect = effect;
                    entity.color.set(color);
                    entity.rotation = rotation;
                    entity.id++;
                    entity.data = data;
                    entity.set(x, y);
                    if(data instanceof Entity_){
                        entity.setParent((Entity_)data);
                    }
                    groundEffectGroup.add(entity);
                }
            }
        }
    }
}
