package io.github.ph1lou.space_conquest.listeners;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.space_conquest.enums.State;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.game.Team;
import io.github.ph1lou.space_conquest.game.gui.TeamChoice;
import io.github.ph1lou.space_conquest.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

public class LobbyListener implements Listener {

    final GameManager game;

    public LobbyListener(GameManager game){
        this.game=game;
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event){

        if(!game.isState(State.LOBBY)) return;

        event.setRespawnLocation(Bukkit.getWorlds().get(0).getSpawnLocation());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){

        Player player = event.getPlayer();
        player.setPlayerListHeaderFooter("Space §bConquest","Plugin et Concept par §bPh1Lou");

        if(game.isState(State.GAME)){
            for(Team team:game.getTeams()){
                team.getNpc().show(player);
            }
        }
        if(game.getTeam(player)!=null || game.isState(State.LOBBY)){
            FastBoard fastboard = new FastBoard(player);
            fastboard.updateTitle("Space §bConquest");
            game.getFastBoard().put(player.getUniqueId(), fastboard);
        }

        if(!game.isState(State.LOBBY)){
            if(game.getTeam(player)==null){
                player.teleport(game.getWorld().getSpawnLocation());
                player.setGameMode(GameMode.SPECTATOR);
            }
            return;
        }

        ItemBuilder itemStack = new ItemBuilder(Material.WHITE_BANNER).setDisplayName("Choix des §bÉquipes");

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
            TeamChoice.INVENTORY.open(player);
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

        Team team = game.getTeam(player);
        if(team!=null){
            team.removePlayer(player);
        }
    }


    @EventHandler
    private void onClick(PlayerInteractEvent event) {

        if(!game.isState(State.LOBBY)) return;

        if(event.getItem()==null) return;
        if(event.getItem().getType().equals(Material.WHITE_BANNER)){
            TeamChoice.INVENTORY.open(event.getPlayer());
            event.setCancelled(true);
            event.getPlayer().getInventory().setHeldItemSlot((1+event.getPlayer().getInventory().getHeldItemSlot())%9);
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

        Team team = game.getTeam(player);

        if(team==null) {
            event.setFormat("[Global Chat] %s : %s");
        }
        else event.setFormat("[Global Chat] "+team.getColorTeam().getChatColor()+"[Team "+team.getName()+"]§r %s : %s");
    }

    @EventHandler
    public void onFight(EntityDamageByEntityEvent event){

        if (!game.isState(State.LOBBY)) return;

        if(!(event.getEntity() instanceof Player)) return;

        if(!(event.getDamager() instanceof Player)) return;

        event.setCancelled(true);
    }
}
