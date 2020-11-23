package io.github.ph1lou.space_conquest.listeners;

import io.github.ph1lou.space_conquest.enums.State;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

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
    public void onPlayerDamageWithSwapArrow(EntityDamageByEntityEvent event){

        if(!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getEntity();

        if(!(event.getDamager() instanceof Arrow)) return;

        Arrow arrow = (Arrow) event.getDamager();

        if(!(arrow.getShooter() instanceof Player)) return;

        Player shooter = (Player) arrow.getShooter();

        boolean find =false;

        for(PotionEffect potionEffect:arrow.getCustomEffects()){
            if(potionEffect.getType().equals(PotionEffectType.BAD_OMEN)){
                find=true;
            }
        }
        if(!find) return;

        event.setCancelled(true);
        Location playerLocation = player.getLocation().clone();

        player.teleport(shooter.getLocation());

        shooter.teleport(playerLocation);

        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT,10,10);
        shooter.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT,10,10);

    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event){
        event.getDrops().clear();
    }



}
