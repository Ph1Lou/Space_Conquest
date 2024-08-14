package fr.ph1lou.space_conquest.listeners;

import fr.ph1lou.space_conquest.Main;
import fr.ph1lou.space_conquest.game.Area;
import fr.ph1lou.space_conquest.game.GameManager;
import fr.ph1lou.space_conquest.game.Team;
import fr.ph1lou.space_conquest.gui.Rank;
import fr.ph1lou.space_conquest.gui.Ressources;
import fr.ph1lou.space_conquest.enums.SpecialItem;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Lightable;
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
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.util.Vector;

import java.util.Optional;
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

        Team team = game.getTeam(player).orElse(null);

        if(team != null){
            Ressources.getInventory().open(player);
        }
        else {
            game.getTeams().stream()
                    .filter(team1 -> team1.getNpc().equals(event.getNPC()))
                    .findFirst()
                    .ifPresent(team1 -> Ressources.getInventory(team1).open(player));

        }


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
    public void onHitFireBall(ProjectileHitEvent event) {

        if(event.getEntity() instanceof Fireball fireball) {
            Location location = fireball.getLocation();
            fireball.getWorld().createExplosion(location, 5f);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){

        Player player = event.getPlayer();
        ItemStack itemStack = event.getItem();

        if(event.getHand() != null && event.getHand().equals(EquipmentSlot.OFF_HAND)){
            return;
        }


        if(itemStack==null) return;

        ItemMeta itemMeta = itemStack.getItemMeta();

        if(itemMeta==null){
            return;
        }
        PersistentDataContainer persistentDataContainer = itemMeta.getPersistentDataContainer();

        if(persistentDataContainer.has(new NamespacedKey(Main.KEY, SpecialItem.PROPULSION.getKey()), PersistentDataType.BOOLEAN)){

            player.setVelocity(new Vector(0,8,0));
            player.getInventory().removeItem(itemStack);
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,300,0,false,false));
            event.setCancelled(true);
        }
        else if(persistentDataContainer.has(new NamespacedKey(Main.KEY, SpecialItem.NO_GRAVITY.getKey()), PersistentDataType.BOOLEAN)){

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

        else if(persistentDataContainer.has(new NamespacedKey(Main.KEY, SpecialItem.LEVITATION.getKey()), PersistentDataType.BOOLEAN)){

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
        else if(persistentDataContainer.has(new NamespacedKey(Main.KEY, SpecialItem.EXPLOSION.getKey()), PersistentDataType.BOOLEAN)){

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
        else if(persistentDataContainer.has(new NamespacedKey(Main.KEY, SpecialItem.FIRE_CHARGE.getKey()), PersistentDataType.BOOLEAN)){

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

        event.setCancelled(true);
        Location location = block.getLocation();
        location.setY(location.getBlockY()-2);

        if(event.getAction().equals(Action.LEFT_CLICK_BLOCK)) return;

        for(Area area:game.getAreas()){
            if(area.getMiddle().equals(location)){

                if(area.isMiddle()){
                    Rank.INVENTORY.open(player1);
                }
                else{
                    Optional<Team> teamOptional = game.getTeam(player1);

                    if(teamOptional.isPresent()){
                        if(teamOptional.get().equals(area.getOwnerTeam())){
                            Ressources.getInventory().open(player1);
                        }
                        else {
                            player1.sendMessage(game.translate("space-conquest.game.beacon.no-control"));
                        }
                    }
                    else if(area.isBase()){
                        Ressources.getInventory(area.getOwnerTeam()).open(player1);
                    }
                }
            }
        }
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
