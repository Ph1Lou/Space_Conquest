package io.github.ph1lou.space_conquest.game.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.space_conquest.Main;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.game.Team;
import io.github.ph1lou.space_conquest.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class GuiShop implements InventoryProvider {


    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .id("guishop")
            .provider(new GuiShop())
            .size(3, 9)
            .title("Vos Ressources")
            .closeable(true)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        contents.set(0,0,ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON).setDisplayName("Vos Ressources").build()),e -> GuiShop.INVENTORY.open(player)));
        contents.set(0,2,ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON).setDisplayName("Boutique").build()),e -> Boutique.INVENTORY.open(player)));
        contents.set(0,4,ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON).setDisplayName("Améliorations").build()),e -> Upgrade.INVENTORY.open(player)));
        contents.set(0,6,ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON).setDisplayName("Objets Spéciaux").build()),e -> SpecialArtefact.INVENTORY.open(player)));
    }


    @Override
    public void update(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();
        Team team = game.getTeam(player);

        if(team!=null){
            int i=1;
            for(Material mat:team.getResource().keySet()){
                ItemBuilder itemStack = new ItemBuilder(mat).setDisplayName(String.valueOf(team.getResource().get(mat)));
                contents.set(1,i,ClickableItem.of((itemStack.build()),e -> e.setCancelled(true)));
                i++;
            }
        }

    }
}
