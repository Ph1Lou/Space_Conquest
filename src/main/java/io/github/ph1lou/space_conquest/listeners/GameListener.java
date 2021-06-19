package io.github.ph1lou.space_conquest.listeners;

import io.github.ph1lou.space_conquest.game.Area;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.gui.Ressources;
import io.github.ph1lou.space_conquest.gui.Rank;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Lightable;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.UUID;

public class GameListener implements Listener {

    final GameManager game;


    public GameListener(GameManager game){
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
    public void onEntityInteract(NPCRightClickEvent event) {
        Player player = event.getClicker();
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1, 10);
        Ressources.INVENTORY.open(player);
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
    public void onBlockExplode(BlockExplodeEvent event){
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
    public void onPlayerInteract(PlayerInteractEvent event){

        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if(itemStack==null) return;

        net.minecraft.server.v1_16_R3.ItemStack item = CraftItemStack.asNMSCopy(itemStack);

        if(item.getTag()==null){
            return;
        }

        if(item.getTag().getBoolean("propulsion")){

            player.setVelocity(new Vector(0,8,0));
            player.getInventory().removeItem(itemStack);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,300,0,false,false));
            event.setCancelled(true);
        }
        else if(item.getTag().getBoolean("no-gravity")){

            if(event.getAction().equals(Action.RIGHT_CLICK_AIR)){
                int i=6;
                Block block;
                do{
                    i--;
                    block = player.getTargetBlock(null,i);
                }while(i>0 && !block.getType().equals(Material.AIR));

                if(i!=0){
                    block.setType(event.getMaterial());
                    Lightable data = (Lightable) block.getBlockData();
                    data.setLit(true);
                    block.setBlockData(data);
                    if(itemStack.getAmount()==1){
                        player.getInventory().removeItem(itemStack);
                    }
                    else itemStack.setAmount(itemStack.getAmount()-1);
                    event.setCancelled(true);
                }
            }

        }

        else if(item.getTag().getBoolean("levitation")){

            if(itemStack.getAmount()==1){
                player.getInventory().removeItem(itemStack);
            }
            else itemStack.setAmount(itemStack.getAmount()-1);


            for(Player player1:Bukkit.getOnlinePlayers()){
                if(player1.getLocation().distanceSquared(player.getLocation())<=625){
                    if(!player1.equals(player)){
                        player1.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION,200,0,false,false));
                        player1.sendMessage(game.translate("space-conquest.game.message.sabotage"));
                        player1.playSound(player.getLocation(),Sound.BLOCK_GLASS_BREAK,10,10);

                    }
                }
            }
            event.setCancelled(true);
            player.sendMessage(game.translate("space-conquest.game.message.use"));
        }
        else if(item.getTag().getBoolean("explosion")){

            if(itemStack.getAmount()==1){
                player.getInventory().removeItem(itemStack);
            }
            else itemStack.setAmount(itemStack.getAmount()-1);

            Location location=null;
            for(Area area:game.getAreas()){

                if(area.isMiddle()){

                    location=area.getMiddle().clone();
                    location.setY(location.getBlockY()+3);
                    break;
                }
            }

            if (location!=null){

                game.getWorld().createExplosion(location,16F);
                game.getWorld().createExplosion(location,16F);

            }
            event.setCancelled(true);
        }
        else if(item.getTag().getBoolean("fire-charge")){

            Action action = event.getAction();
            if (action == Action.RIGHT_CLICK_BLOCK || action == Action.RIGHT_CLICK_AIR) {
                if(itemStack.getAmount()==1){
                    player.getInventory().removeItem(itemStack);
                }
                else {
                    itemStack.setAmount(itemStack.getAmount()-1);
                }
                Location eye = player.getEyeLocation();
                Location loc = eye.add(eye.getDirection().multiply(1.2));
                Fireball fireball = (Fireball)player.getWorld().spawnEntity(loc, EntityType.FIREBALL);
                fireball.setVelocity(loc.getDirection().normalize().multiply(2));
                fireball.setShooter(player);
                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 10.0f, 1.0f);
                event.setCancelled(true);
            }
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

        game.getTeam(player1).ifPresent(team -> {

            event.setCancelled(true);
            Location location = block.getLocation();
            location.setY(location.getBlockY()-2);


            if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;

            for(Area area:game.getAreas()){
                if(area.getMiddle().equals(location)){
                    if(area.isMiddle()){
                        Rank.INVENTORY.open(player1);
                    }
                    else if(team.equals(area.getOwnerTeam())){
                        Ressources.INVENTORY.open(player1);
                    }
                    else {
                        player1.sendMessage(game.translate("space-conquest.game.beacon.no-control"));
                    }
                }
            }
        });
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if (event.getBlock().getType() == Material.TNT) {
            event.getBlock().getWorld().getBlockAt(event.getBlock().getLocation()).setType(Material.AIR);
            TNTPrimed tnt = event.getBlock().getWorld().spawn(event.getBlock().getLocation(), TNTPrimed.class);
            tnt.setFuseTicks(40);
        }
    }

    @EventHandler
    public void onChickenSummon(PlayerEggThrowEvent event){
        event.setHatching(false);
    }

    @EventHandler
    public void eggBridge(ProjectileLaunchEvent event) {

        if(!event.getEntityType().equals(EntityType.EGG)) return;

        Egg egg = (Egg) event.getEntity();

        ProjectileSource projectileSource = event.getEntity().getShooter();

        if(!(projectileSource instanceof Player)) return;

        Player player = (Player) event.getEntity().getShooter();

        int i = Bukkit.getScheduler().scheduleSyncRepeatingTask(game.getMain(),() -> {

            if(egg.isDead()) {
                return;
            }
            Location location = egg.getLocation();
            location.setY(location.getBlockY()-2);
            Block block = location.getBlock();

            Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(),() -> {

                if(block.getType().equals(Material.AIR)){
                    game.getTeam(player).ifPresent(team -> block.setType(team.getColorTeam().getConstructionMaterial()));
                }
            },3L);

        },2L,1L);

        Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(),() ->
                Bukkit.getScheduler().cancelTask(i),30);

    }

}
