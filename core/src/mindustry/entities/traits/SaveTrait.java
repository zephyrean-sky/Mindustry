package mindustry.entities.traits;

/**
 * Marks an entity as serializable.
 */
public interface SaveTrait extends Entity_, TypeTrait, Saveable{
    byte version();
}
