package mindustry.entities.bullet;

import arc.graphics.*;
import arc.math.*;
import arc.util.*;
import mindustry.content.*;
import mindustry.entities.*;
import mindustry.entities.type.*;
import mindustry.gen.*;
import mindustry.graphics.*;

public class MissileBulletType extends BasicBulletType{
    protected Color trailColor = Pal.missileYellowBack;

    protected float weaveScale = 0f;
    protected float weaveMag = -1f;

    public MissileBulletType(float speed, float damage, String bulletSprite){
        super(speed, damage, bulletSprite);
        backColor = Pal.missileYellowBack;
        frontColor = Pal.missileYellow;
        homingPower = 7f;
        hitSound = Sounds.explosion;
    }

    public MissileBulletType(){
        this(1f, 1f, "missile");
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        if(Mathf.chance(Time.delta() * 0.2)){
            Fx.missileTrail.at(trailColor, b.x, b.y, 2f);
        }

        if(weaveMag > 0){
            b.velocity().rotate(Mathf.sin(Time.time() + b.id * 4422, weaveScale, weaveMag) * Time.delta());
        }
    }
}
