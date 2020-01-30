package mindustry.world.blocks.power;

import arc.func.Boolf;
import mindustry.world.TileData;
import mindustry.world.consumers.ConsumePower;

/** A power consumer that only activates sometimes. */
public class ConditionalConsumePower extends ConsumePower{
    private final Boolf<TileData> consume;

    public ConditionalConsumePower(float usage, Boolf<TileData> consume){
        super(usage, 0, false);
        this.consume = consume;
    }

    @Override
    public float requestedPower(TileData entity){
        return consume.get(entity) ? usage : 0f;
    }
}
