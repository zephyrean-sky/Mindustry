package mindustry.entities.traits;

public interface AbsorbTrait extends Entity_, TeamTrait, DamageTrait{
    void absorb();

    default boolean canBeAbsorbed(){
        return true;
    }

    default float getShieldDamage(){
        return damage();
    }
}
