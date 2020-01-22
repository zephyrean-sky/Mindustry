package mindustry.ecs;

import arc.ecs.*;
import arc.math.geom.*;

public class Components{

    public static class Health extends Component{
        public float health;
        public boolean dead;
    }

    public static class Hitbox extends Component{
        public float width, height;
    }

    public static class Pos extends Component{
        public float x, y;
    }

    public static class Velocity extends Component{
        public Vec2 vec = new Vec2();
    }


}
