package fr.ph1lou.space_conquest.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.space_conquest.Main;
import fr.ph1lou.space_conquest.game.GameManager;
import fr.ph1lou.space_conquest.utils.ItemBuilder;
import fr.ph1lou.space_conquest.enums.State;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Start implements InventoryProvider {

    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("start_game")
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

        contents.set(1,3,ClickableItem.of((new ItemBuilder(Material.GREEN_STAINED_GLASS).setDisplayName("Lancer la Partie").build()), e -> {

            if(game.isState(State.LOBBY)) {

                if(game.getTeams().size()>0) {
                    game.setState(State.GAME);
                }
                else {
                    player.sendMessage(game.translate("space-conquest.game.message.no-team"));
                }
            }
        }));
        contents.set(1,5,
                ClickableItem.of((new ItemBuilder(Material.RED_STAINED_GLASS)
                        .setDisplayName(game.translate("space-conquest.gui.start.back"))
                        .build()),e -> ConfigMenu.INVENTORY.open(player)));

        contents.fillBorders(ClickableItem.empty(new ItemStack(Material.ORANGE_STAINED_GLASS)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {
    }
}
