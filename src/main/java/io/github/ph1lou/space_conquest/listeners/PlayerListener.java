package io.github.ph1lou.space_conquest.listeners;

import io.github.ph1lou.space_conquest.enums.State;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerRespawnEvent;

public class PlayerListener implements Listener {

    final GameManager game;

    public PlayerListener(GameManager game){
        this.game=game;
    }


    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){
        if(game.isState(State.LOBBY)) return;
        Player player1 =event.getPlayer();
        Team team = game.getTeam(player1);
        if(team ==null) {
            event.setRespawnLocation(game.getWorld().getSpawnLocation());
        }
        else if(game.isState(State.GAME)){
            event.setRespawnLocation(team.getSpawn());
            Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(), () -> team.start(player1), 20L);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        event.getDrops().clear();
    }

    @EventHandler
    private void onClickEvent(InventoryClickEvent event) {
        if(event.getSlotType().equals(InventoryType.SlotType.ARMOR)){
            event.setCancelled(true);
        }
    }

}
