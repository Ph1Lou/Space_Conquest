package fr.ph1lou.space_conquest.events;

import fr.ph1lou.space_conquest.game.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class WinEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public Team getTeam() {
        return team;
    }

    private final Team team;

    public WinEvent(Team team){
        this.team=team;
    }

    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
