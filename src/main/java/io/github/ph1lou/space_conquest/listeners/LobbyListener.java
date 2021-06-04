package io.github.ph1lou.space_conquest.listeners;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.space_conquest.enums.State;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.game.Team;
import io.github.ph1lou.space_conquest.gui.ConfigMenu;
import io.github.ph1lou.space_conquest.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;

public class LobbyListener implements Listener {

    final GameManager game;

    public LobbyListener(GameManager game){
        this.game=game;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){

        if(!this.game.isState(State.LOBBY)) return;

        event.setRespawnLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){

        Player player = event.getPlayer();
        player.setPlayerListHeaderFooter(
                game.translate("space-conquest.title"),
                game.translate("space-conquest.credit")+"Ph1Lou");

        if(game.getTeam(player).isPresent() || game.isState(State.LOBBY)){
            FastBoard fastboard = new FastBoard(player);
            fastboard.updateTitle(game.translate("space-conquest.title"));
            game.getFastBoard().put(player.getUniqueId(), fastboard);
        }

        if(!game.isState(State.LOBBY)){
            if(!game.getTeam(player).isPresent()){
                player.teleport(game.getWorld().getSpawnLocation());
                player.setGameMode(GameMode.SPECTATOR);
            }
            return;
        }

        ItemBuilder itemStack = new ItemBuilder(Material.WHITE_BANNER)
                .setDisplayName(game.translate("space-conquest.team.choice"));

        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
        player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
        player.getInventory().addItem(itemStack.build());

    }

    @EventHandler
    public void onItemMove(InventoryClickEvent event){
        if(!game.isState(State.LOBBY)) return;

        if(event.getCurrentItem()==null) return;
        if(event.getCurrentItem().getType().equals(Material.WHITE_BANNER)){
            Player player = (Player) event.getView().getPlayer();
            ConfigMenu.INVENTORY.open(player);
            event.setCancelled(true);
            player.getInventory().setHeldItemSlot((1+player.getInventory().getHeldItemSlot())%9);
        }
    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event){

        Player player = event.getPlayer();

        FastBoard board = game.getFastBoard().remove(player.getUniqueId());

        if(board!=null){
            board.delete();
        }

        if(!game.isState(State.LOBBY)) return;

        game.getTeam(player).ifPresent(team -> team.removePlayer(player));

    }


    @EventHandler
    private void onClick(PlayerInteractEvent event) {

        if(!game.isState(State.LOBBY)) return;

        if(event.getItem()==null) return;
        if(event.getItem().getType().equals(Material.WHITE_BANNER)){
            ConfigMenu.INVENTORY.open(event.getPlayer());
            event.setCancelled(true);
            event.getPlayer().getInventory().setHeldItemSlot((1
                    +event.getPlayer().getInventory().getHeldItemSlot()) % 9);
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        if(event.getItemDrop().getItemStack().getType().equals(Material.WHITE_BANNER)){
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event){

        Player player =event.getPlayer();

        Team team = game.getTeam(player).orElse(null);

        if(team==null) {
            event.setFormat(game.translate("space-conquest.game.chat.global",
                    "%s",
                    "%s"));
        }
        else event.setFormat(game.translate("space-conquest.game.chat.format",
                team.getColorTeam().getChatColor(),
                team.getName(),
                "%s",
                "%s"));
    }

    @EventHandler
    public void onFight(EntityDamageByEntityEvent event){

        if (!game.isState(State.LOBBY)) return;

        if(!(event.getEntity() instanceof Player)) return;

        if(!(event.getDamager() instanceof Player)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void WeatherChangeEvent(WeatherChangeEvent event) {

        event.setCancelled(true);
        event.getWorld().setWeatherDuration(0);
        event.getWorld().setThundering(false);
    }

    @EventHandler
    private void onClickEvent(InventoryClickEvent event) {
        if(event.getSlotType().equals(InventoryType.SlotType.ARMOR)){
            event.setCancelled(true);
        }
    }
}
