package mindustry.ecs;

import arc.ecs.*;
import arc.ecs.annotations.*;
import arc.ecs.systems.*;
import arc.math.*;
import arc.util.*;
import mindustry.annotations.Annotations.*;
import mindustry.content.*;
import mindustry.ecs.Components.*;
import mindustry.entities.*;
import mindustry.entities.effect.*;
import mindustry.entities.type.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.type.*;
import mindustry.world.*;

import static mindustry.Vars.*;

@All({Posc.class, Minec.class, Itemc.class, Teamc.class})
@AutoSystem
public class MineSystem extends IteratingSystem{
    float mineDistance = 70f;

    Mapper<Posc> posm;
    Mapper<Minec> buildm;
    Mapper<Itemc> itemm;
    Mapper<Teamc> teamm;

    @Override
    protected void process(int entity){
        Posc pos = posm.get(entity);
        Minec mine = buildm.get(entity);
        Itemc items = itemm.get(entity);
        Team team = teamm.get(entity).team;

        Tile tile = mine.tile;
        TileEntity core = team.core();

        //TODO
        Unit fakeUnitForBlockAcceptanceRemoveLater = null;

        if(core != null && tile != null && tile.drop() != null && !items.accepts(tile.drop()) && pos.within(core, mineTransferRange)){
            int accepted = core.tile.block().acceptStack(items.stack.item, items.stack.amount, core.tile, fakeUnitForBlockAcceptanceRemoveLater);
            if(accepted > 0){
                Call.transferItemTo(items.stack.item, accepted,
                tile.worldx() + Mathf.range(tilesize / 2f),
                tile.worldy() + Mathf.range(tilesize / 2f), core.tile);
                items.clear();
            }
        }

        if(tile == null || core == null || tile.block() != Blocks.air || !pos.within(tile.worldx(), tile.worldy(), mineDistance)
        || tile.drop() == null || !items.accepts(tile.drop()) || !mine.mines(tile.drop())){
            mine.tile = null;
        }else{
            Item item = tile.drop();
            //TODO rotate the unit, if applicable (? ? ???? ?? ?)
            //unit.rotation = Mathf.slerpDelta(unit.rotation, unit.angleTo(tile.worldx(), tile.worldy()), 0.4f);

            if(Mathf.chance(Time.delta() * (0.06 - item.hardness * 0.01) * mine.speed)){

                if(pos.within(core, mineTransferRange)  && core.tile.block().acceptStack(item, 1, core.tile, fakeUnitForBlockAcceptanceRemoveLater) == 1 && mine.autounload){
                    Call.transferItemTo(item, 1,
                    tile.worldx() + Mathf.range(tilesize / 2f),
                    tile.worldy() + Mathf.range(tilesize / 2f), core.tile);
                }else if(items.accepts(item)){
                    //this is clientside, since items are synced anyway
                    ItemTransfer.transferItemToUnit(item,
                    tile.worldx() + Mathf.range(tilesize / 2f),
                    tile.worldy() + Mathf.range(tilesize / 2f),
                    fakeUnitForBlockAcceptanceRemoveLater);
                }
            }

            if(Mathf.chance(0.06 * Time.delta())){
                Effects.effect(Fx.pulverizeSmall,
                tile.worldx() + Mathf.range(tilesize / 2f),
                tile.worldy() + Mathf.range(tilesize / 2f), 0f, item.color);
            }
        }
    }
}
