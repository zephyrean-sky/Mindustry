package mindustry.entities.traits;

public interface DrawTrait extends Entity_{

    default float drawSize(){
        return 20f;
    }

    void draw();
}
