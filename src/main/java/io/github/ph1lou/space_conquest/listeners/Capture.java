package io.github.ph1lou.space_conquest.listeners;

import io.github.ph1lou.space_conquest.game.Area;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.game.gui.GuiShop;
import net.jitse.npclib.api.events.NPCInteractEvent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Capture implements Listener {

    final GameManager game;


    public Capture(GameManager game){
        this.game=game;

    }
    @EventHandler
    public void onBlockExplode(EntityExplodeEvent event){

        int i=0;
        while(i<event.blockList().size()){
            Block block = event.blockList().get(i);
            Material material = block.getType();
            if(material.toString().contains("STAINED_GLASS")){
                event.blockList().remove(block);
            }
            else if (material.equals(Material.BEACON) || material.equals(Material.GLOWSTONE)){
                event.blockList().remove(block);
            }
            else if(block.getRelative(BlockFace.UP).getType().equals(Material.BEACON)){
                event.blockList().remove(block);
            }
            else i++;
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){

        Material material = event.getBlock().getType();
        if(material.toString().contains("STAINED_GLASS")){
            event.setCancelled(true);
        }
        else if (material.equals(Material.BEACON) || material.equals(Material.GLOWSTONE)){
            event.setCancelled(true);
        }
        else if(event.getBlock().getRelative(BlockFace.UP).getType().equals(Material.BEACON)){
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onEntityInteract(NPCInteractEvent event) {
        Player player = event.getWhoClicked();
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 10);
        GuiShop.INVENTORY.open(player);
    }

    @EventHandler
    public void onPlayerKillPlayer(PlayerDeathEvent event){
        
        if(event.getEntity().getKiller() == null) return;
        Player killer = event.getEntity().getKiller();
        UUID uuid = killer.getUniqueId();
        game.getKills().put(uuid,game.getKills().getOrDefault(uuid,0)+1);
        
        killer.setVelocity(new Vector(0,3,0));
        killer.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,300,0,false,false));
        killer.playSound(killer.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT,10,10);
        
    }



    @EventHandler
    public void onVillagerDamage(EntityDamageEvent event){

        if(event.getEntity() instanceof Villager) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){

        ItemStack itemStack = event.getItem();
        if(itemStack==null) return;
        ItemMeta itemMeta = itemStack.getItemMeta();
        if(itemMeta==null) return;
        if(itemMeta.getDisplayName().equals("Propulsion")){
            Player player1 = event.getPlayer();
            player1.setVelocity(new Vector(0,8,0));
            player1.getInventory().removeItem(itemStack);
            player1.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,300,0,false,false));
            event.setCancelled(true);
        }
        else if(itemMeta.getDisplayName().equals("Levitation")){
            Player player1 = event.getPlayer();
            if(itemStack.getAmount()==1){
                player1.getInventory().removeItem(itemStack);
            }
            else itemStack.setAmount(itemStack.getAmount()-1);


            for(Player player:Bukkit.getOnlinePlayers()){
                if(player.getLocation().distanceSquared(player1.getLocation())<=625){
                    if(!player.equals(player1)){
                        player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,200,0,false,false));
                        player.sendMessage("[Space §bConquest§r] Votre assistance gravitationnelle a été sabotée");
                        player.playSound(player.getLocation(),Sound.BLOCK_GLASS_BREAK,10,10);

                    }
                }
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteractWithBeacon(PlayerInteractEvent event){

        Block block = event.getClickedBlock();
        Player player1 = event.getPlayer();

        if(block==null) return;
        if(!block.getType().equals(Material.BEACON)){
            return;
        }

        event.setCancelled(true);
        Location location = block.getLocation();
        location.setY(location.getBlockY()-2);

        for(Area area:game.getAreas()){
            if(area.getMiddle().equals(location)){
                if(area.getOwnerTeam()!=null && area.getOwnerTeam().equals(game.getTeam(player1))){
                    GuiShop.INVENTORY.open(player1);
                }
                else player1.sendMessage("[Space §bConquest§r] Vous n'avez pas le contrôle de cette Balise");
            }
        }
    }

    @EventHandler
    public void WeatherChangeEvent(WeatherChangeEvent event) {

        event.setCancelled(true);
        event.getWorld().setWeatherDuration(0);
        event.getWorld().setThundering(false);
    }


    @SuppressWarnings("unchecked")
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if (event.getBlock().getType() == Material.TNT) {
            event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
            TNTPrimed tnt = (TNTPrimed)event.getBlock().getWorld().spawn(event.getBlock().getLocation(), (Class)TNTPrimed.class);
            tnt.setFuseTicks(40);
        }
    }

}
