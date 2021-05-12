package io.github.ph1lou.space_conquest.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.space_conquest.Main;
import io.github.ph1lou.space_conquest.enums.State;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.game.Team;
import io.github.ph1lou.space_conquest.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class Start implements InventoryProvider {



    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("startgame")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new Start())
            .size(3, 9)
            .title(JavaPlugin.getPlugin(Main.class).getLangManager().getTranslation("space-conquest.gui.start.name"))
            .closeable(true)
            .parent(ConfigMenu.INVENTORY)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();

        contents.set(1,3,ClickableItem.of((new ItemBuilder(Material.GREEN_STAINED_GLASS).setDisplayName("Lancer la Partie").build()),e -> {

            if(game.isState(State.LOBBY)){

                if(game.getTeams().size()>0) {

                    player.closeInventory();

                    game.repartition();

                    for(Team team:game.getTeams()){
                        for(UUID uuid:team.getMembers()){
                            Player player1 = Bukkit.getPlayer(uuid);
                            if(player1!=null){
                                player1.teleport(team.getSpawn());
                                team.start(player1);
                            }
                        }
                    }
                    for(Player player1:Bukkit.getOnlinePlayers()){
                        if(!player1.getWorld().equals(game.getWorld())){
                            player1.teleport(game.getWorld().getSpawnLocation());
                            player1.setGameMode(GameMode.SPECTATOR);
                        }
                    }
                    game.setState(State.GAME);
                }
                else {
                    player.sendMessage(game.translate("space-conquest.game.message.no-team"));
                }
            }


        }));
        contents.set(1,5,ClickableItem.of((new ItemBuilder(Material.RED_STAINED_GLASS).setDisplayName(game.translate("space-conquest.gui.start.back")).build()),e -> ConfigMenu.INVENTORY.open(player)));

        contents.fillBorders(ClickableItem.empty(new ItemStack(Material.ORANGE_STAINED_GLASS)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
