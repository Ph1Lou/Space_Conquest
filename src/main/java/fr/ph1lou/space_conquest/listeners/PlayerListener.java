package fr.ph1lou.space_conquest.listeners;

import fr.ph1lou.space_conquest.game.GameManager;
import fr.ph1lou.space_conquest.game.Team;
import fr.ph1lou.space_conquest.enums.State;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerListener implements Listener {

    private final GameManager game;

    public PlayerListener(GameManager game){
        this.game=game;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){

        if(this.game.isState(State.LOBBY)) return;

        Player player1 =event.getPlayer();

        Team team = this.game.getTeam(player1).orElse(null);

        if(team ==null) {
            event.setRespawnLocation(this.game.getWorld().getSpawnLocation());
        }
        else if(this.game.isState(State.GAME)){
            event.setRespawnLocation(team.getSpawn());
            Bukkit.getScheduler()
                    .scheduleSyncDelayedTask(
                            this.game.getMain(),
                            () -> team.start(player1), 20L);
        }
    }


    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event){

        if(event.getTo()==null){
            return;
        }

        if(event.getTo().getY()<10){
            event.getPlayer().damage(1000000000);
        }
    }

    @EventHandler
    public void onPlayerDamageWithSwapArrow(EntityDamageByEntityEvent event){

        if(!(event.getEntity() instanceof Player player)) return;

        if(!(event.getDamager() instanceof Arrow arrow)) return;

        if(!(arrow.getShooter() instanceof Player shooter)) return;

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
