package mindustry.ecs;

import arc.ecs.*;
import mindustry.*;
import mindustry.ecs.Components.*;

public class Archetypes{
    //the player archetype doesn't have any additional components, as those are added by specific units
    public static final Archetype player = ArchetypeBuilder.with(Vars.base, "player", Playerc.class, Syncc.class);

    public static final Archetype baseUnit = ArchetypeBuilder.with(Vars.base, "baseUnit",
        Posc.class, Healthc.class, Hitboxc.class, Teamc.class, Rotc.class,
        Velocityc.class, Syncc.class, Spawningc.class, Statusesc.class, Shellc.class,
        Rotc.class);
}
