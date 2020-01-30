package mindustry.game;

import mindustry.annotations.Annotations.*;
import mindustry.core.GameState.*;
import mindustry.ctype.*;
import mindustry.entities.units.*;
import mindustry.type.*;
import mindustry.world.*;

@EventClasses
public class EventType{

    /**events that occur very often
    public enum Trigger{
        shock,
        phaseDeflectHit,
        impactPower,
        thoriumReactorOverheat,
        itemLaunch,
        fireExtinguish,
        newGame,
        tutorialComplete,
        flameAmmo,
        turretCool,
        enablePixelation,
        drown,
        exclusionDeath,
        suicideBomb,
        openWiki,
        teamCoreDamage,
        socketConfigChanged,
        update
    }*/

    public static class WinEvent{}
    public static class LoseEvent{}
    public static class LaunchEvent{}
    public static class MapMakeEvent{}
    public static class MapPublishEvent{}
    public static class PauseEvent{}
    public static class ResumeEvent{}

    public static class LaunchItemEvent{
        public ItemStack stack;
    }

    public static class CommandIssueEvent{
        public Tile tile;
        public UnitCommand command;
    }

    public static class PlayerChatEvent{
        public int player;
        public String message;
    }

    /** Called when a zone's requirements are met. */
    public static class ZoneRequireCompleteEvent{
        public Zone zoneMet, zoneForMet;
        public Objective objective;
    }

    /** Called when a zone's requirements are met. */
    public static class ZoneConfigureCompleteEvent{
        public Zone zone;
    }

    /** Called when the client game is first loaded. */
    public static class ClientLoadEvent{

    }

    public static class ServerLoadEvent{

    }

    public static class ContentReloadEvent{

    }

    public static class DisposeEvent{

    }

    public static class PlayEvent{

    }

    public static class ResetEvent{

    }

    public static class WaveEvent{

    }

    /** Called when the player places a line, mobile or desktop.*/
    public static class LineConfirmEvent{

    }

    /** Called when a turret recieves ammo, but only when the tutorial is active! */
    public static class TurretAmmoDeliverEvent{

    }

    /** Called when a core recieves ammo, but only when the tutorial is active! */
    public static class CoreItemDeliverEvent{

    }

    /** Called when the player opens info for a specific block.*/
    public static class BlockInfoEvent{

    }

    /** Called when the player withdraws items from a block. */
    public static class WithdrawEvent{
        public Tile tile;
        public int player;
        public Item item;
        public int amount;
    }

    /** Called when a player deposits items to a block.*/
    public static class DepositEvent{
        public Tile tile;
        public int player;
        public Item item;
        public int amount;
    }

    /** Called when the player taps a block. */
    public static class TapEvent{
        public Tile tile;
        public int player;
    }

    /** Called when the player sets a specific block. */
    public static class TapConfigEvent{
        public Tile tile;
        public int player;
        public int value;
    }

    public static class GameOverEvent{
        public Team winner;
    }

    /** Called when a game begins and the world is loaded. */
    public static class WorldLoadEvent{

    }

    /** Called from the logic thread. Do not access graphics here! */
    public static class TileChangeEvent{
        public Tile tile;
    }

    public static class StateChangeEvent{
        public State from, to;
    }

    public static class UnlockEvent{
        public UnlockableContent content;
    }

    public static class ResearchEvent{
        public UnlockableContent content;
    }

    /**
     * Called when block building begins by placing down the BuildBlock.
     * The tile's block will nearly always be a BuildBlock.
     */
    public static class BlockBuildBeginEvent{
        public Tile tile;
        public Team team;
        public boolean breaking;
    }

    public static class BlockBuildEndEvent{
        public Tile tile;
        public Team team;
        public int player;
        public boolean breaking;
    }

    /**
     * Called when a player or drone begins building something.
     * This does not necessarily happen when a new BuildBlock is created.
     */
    public static class BuildSelectEvent{
        public Tile tile;
        public Team team;
        public int builder;
        public boolean breaking;
    }

    /** Called right before a block is destroyed.
     * The tile entity of the tile in this event cannot be null when this happens.*/
    public static class BlockDestroyEvent{
        public Tile tile;
    }

    /*
    public static class UnitDestroyEvent{
        public Unit unit;
    }

    public static class UnitCreateEvent{
        public BaseUnit unit;
    }*/

    public static class ResizeEvent{

    }

    //TODO rename
    public static class MechChangeEvent{
        public int player;
        public UnitDef mech;
    }

    /** Called after connecting; when a player recieves world data and is ready to play.*/
    public static class PlayerJoinEvent{
        public int player;
    }

    /** Called when a player connects, but has not joined the game yet.*/
    public static class PlayerConnectEvent{
        public int player;
    }

    public static class PlayerLeave{
        public int player;
    }
    
    public static class PlayerBanEvent{
        public int player;
    }
    
    public static class PlayerUnbanEvent{
        public int player;
    }
    
    public static class PlayerIpBanEvent{
        public String ip;
    }
    
    public static class PlayerIpUnbanEvent{
        public String ip;
    }
    
}

