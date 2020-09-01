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

public class Upgrade implements InventoryProvider {



    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("upgrade")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new Upgrade())
            .size(4, 9)
            .title("Upgrade")
            .closeable(true)
            .parent(GuiShop.INVENTORY)
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

        if(team==null) return;

        if(!team.getUpgrade().isIronChestPlate()){

            ItemBuilder chestPlate = new ItemBuilder(Material.IRON_CHESTPLATE).setDisplayName(String.format("§bUpgrade Iron ChestPlate§r Coût 5000 Fers (§b%d en banque)",team.getResource().getOrDefault(Material.IRON_BLOCK,0)));

            
            contents.set(1,1, ClickableItem.of((chestPlate.build()), e -> {
                if(team.getResource().containsKey(Material.IRON_BLOCK)){
                    if(team.getResource().get(Material.IRON_BLOCK)>=5000){
                        team.getUpgrade().setIronChestPlate(true);
                        player.sendMessage("Upgrade iron chestplate unlock");
                        team.getResource().put(Material.IRON_BLOCK,team.getResource().get(Material.IRON_BLOCK)-5000);
                    }
                }
            }));
        }
        else contents.set(1,1, null);

        if(!team.getUpgrade().isProtection()){

            ItemBuilder anvil = new ItemBuilder(Material.ANVIL);
            anvil.setDisplayName(String.format("§bUpgrade Protection§r Coût 5000 Ors (§b%d en banque)",team.getResource().getOrDefault(Material.GOLD_BLOCK,0)));

            
            contents.set(1,3, ClickableItem.of((anvil.build()), e -> {
                if(team.getResource().containsKey(Material.GOLD_BLOCK)){
                    if(team.getResource().get(Material.GOLD_BLOCK)>=5000){
                        team.getUpgrade().setProtection(true);
                        player.sendMessage("Upgrade protection unlock");
                        team.getResource().put(Material.GOLD_BLOCK,team.getResource().get(Material.GOLD_BLOCK)-5000);
                    }
                }
            }));
        }
        else contents.set(1,3, null);

        if(!team.getUpgrade().isSharpness()){

            ItemBuilder sword = new ItemBuilder(Material.GOLDEN_SWORD);
            sword.setDisplayName(String.format("§bUpgrade Sharpness§r Coût 5000 Ors (§b%d en banque)",team.getResource().getOrDefault(Material.GOLD_BLOCK,0)));

            
            contents.set(1,5, ClickableItem.of((sword.build()), e -> {
                if(team.getResource().containsKey(Material.GOLD_BLOCK)){
                    if(team.getResource().get(Material.GOLD_BLOCK)>=5000){
                        team.getUpgrade().setSharpness(true);
                        player.sendMessage("Upgrade sharpness unlock");
                        team.getResource().put(Material.GOLD_BLOCK,team.getResource().get(Material.GOLD_BLOCK)-5000);
                    }
                }
            }));
        }
        else contents.set(1,5, null);

        if(!team.getUpgrade().isPunch()){

            ItemBuilder bow = new ItemBuilder(Material.BOW);
            bow.setDisplayName(String.format("§bUpgrade Punch§r Coût 5000 Ors (§b%d en banque)",team.getResource().getOrDefault(Material.GOLD_BLOCK,0)));
            
            contents.set(1,7, ClickableItem.of((bow.build()), e -> {
                if(team.getResource().containsKey(Material.GOLD_BLOCK)){
                    if(team.getResource().get(Material.GOLD_BLOCK)>=5000){
                        team.getUpgrade().setPunch(true);
                        player.sendMessage("Upgrade bow unlock");
                        team.getResource().put(Material.GOLD_BLOCK,team.getResource().get(Material.GOLD_BLOCK)-5000);
                    }
                }
            }));
        }
        else contents.set(1,7, null);


    }
}
