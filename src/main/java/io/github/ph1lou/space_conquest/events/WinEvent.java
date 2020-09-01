package io.github.ph1lou.space_conquest.events;

import io.github.ph1lou.space_conquest.game.Team;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class WinEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public Team getTeam() {
        return team;
    }

    private final Team team;

    public WinEvent(Team team){
        this.team=team;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
