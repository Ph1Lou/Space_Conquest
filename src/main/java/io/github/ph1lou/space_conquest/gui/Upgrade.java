package io.github.ph1lou.space_conquest.gui;

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
            .title(JavaPlugin.getPlugin(Main.class).getLangManager().getTranslation("space-conquest.gui.upgrade.name"))
            .closeable(true)
            .parent(Ressources.INVENTORY)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();

        contents.set(0,0,ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON).setDisplayName(game.translate("space-conquest.gui.ressources.title")).build()),e -> Ressources.INVENTORY.open(player)));
        contents.set(0,2,ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON).setDisplayName(game.translate("space-conquest.gui.boutique.title")).build()),e -> Boutique.INVENTORY.open(player)));
        contents.set(0,4,ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON).setDisplayName(game.translate("space-conquest.gui.upgrade.name")).build()),e -> Upgrade.INVENTORY.open(player)));
        contents.set(0,6,ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON).setDisplayName(game.translate("space-conquest.gui.special-artefact.title")).build()),e -> SpecialArtefact.INVENTORY.open(player)));
        contents.set(0,8,
                ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON).setDisplayName(game.translate("space-conquest.gui.beacon.name"))
                        .build()),e -> Beacon.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();
        Team team = game.getTeam(player);

        if(team==null) return;

        if(team.getUpgrade().getChestPlate()==0){

            ItemBuilder chestPlate = new ItemBuilder(Material.IRON_CHESTPLATE).setDisplayName(game.translate("space-conquest.gui.upgrade.iron-chest-plate.name",team.getResource().getOrDefault(Material.IRON_BLOCK,0)));
            
            contents.set(1,1, ClickableItem.of((chestPlate.build()), e -> {
                if(team.getResource().containsKey(Material.IRON_BLOCK)){
                    if(team.getResource().get(Material.IRON_BLOCK)>=5000){
                        team.getUpgrade().setChestPlate(1);
                        player.sendMessage(game.translate("space-conquest.gui.upgrade.iron-chest-plate.message"));
                        team.getResource().put(Material.IRON_BLOCK,team.getResource().get(Material.IRON_BLOCK)-5000);
                    }
                }
            }));
        }
        else if(team.getUpgrade().getChestPlate()==1){
            
            ItemBuilder chestPlate = new ItemBuilder(Material.DIAMOND_CHESTPLATE).setDisplayName(game.translate("space-conquest.gui.upgrade.diamond-chest-plate.name",team.getResource().getOrDefault(Material.DIAMOND_BLOCK,0)));

            contents.set(1,1, ClickableItem.of((chestPlate.build()), e -> {
                if(team.getResource().containsKey(Material.DIAMOND_BLOCK)){
                    if(team.getResource().get(Material.DIAMOND_BLOCK)>=5000){
                        team.getUpgrade().setChestPlate(2);
                        player.sendMessage(game.translate("space-conquest.gui.upgrade.diamond-chest-plate.message"));
                        team.getResource().put(Material.DIAMOND_BLOCK,team.getResource().get(Material.DIAMOND_BLOCK)-5000);
                    }
                }
            }));
        }
        else contents.set(1,1, null);

        if(team.getUpgrade().isProtection()==0){

            ItemBuilder anvil = new ItemBuilder(Material.ANVIL);
            anvil.setDisplayName(game.translate("space-conquest.gui.upgrade.protection-1.name",team.getResource().getOrDefault(Material.GOLD_BLOCK,0)));

            
            contents.set(1,3, ClickableItem.of((anvil.build()), e -> {
                if(team.getResource().containsKey(Material.GOLD_BLOCK)){
                    if(team.getResource().get(Material.GOLD_BLOCK)>=5000){
                        team.getUpgrade().setProtection(1);
                        player.sendMessage(game.translate("space-conquest.gui.upgrade.protection-1.message"));
                        team.getResource().put(Material.GOLD_BLOCK,team.getResource().get(Material.GOLD_BLOCK)-5000);
                    }
                }
            }));
        }
        else if(team.getUpgrade().isProtection()==1){

            ItemBuilder anvil = new ItemBuilder(Material.ANVIL);
            anvil.setDisplayName(game.translate("space-conquest.gui.upgrade.protection-2.name",team.getResource().getOrDefault(Material.GOLD_BLOCK,0)));


            contents.set(1,3, ClickableItem.of((anvil.build()), e -> {
                if(team.getResource().containsKey(Material.GOLD_BLOCK)){
                    if(team.getResource().get(Material.GOLD_BLOCK)>=10000){
                        team.getUpgrade().setProtection(2);
                        player.sendMessage(game.translate("space-conquest.gui.upgrade.protection-2.message"));
                        team.getResource().put(Material.GOLD_BLOCK,team.getResource().get(Material.GOLD_BLOCK)-10000);
                    }
                }
            }));
        }
        else contents.set(1,3, null);

        if(team.getUpgrade().isSharpness()==0){

            ItemBuilder sword = new ItemBuilder(Material.GOLDEN_SWORD);
            sword.setDisplayName(game.translate("space-conquest.gui.upgrade.sharpness-1.name",team.getResource().getOrDefault(Material.GOLD_BLOCK,0)));

            
            contents.set(1,5, ClickableItem.of((sword.build()), e -> {
                if(team.getResource().containsKey(Material.GOLD_BLOCK)){
                    if(team.getResource().get(Material.GOLD_BLOCK)>=5000){
                        team.getUpgrade().setSharpness(1);
                        player.sendMessage(game.translate("space-conquest.gui.upgrade.sharpness-1.message"));
                        team.getResource().put(Material.GOLD_BLOCK,team.getResource().get(Material.GOLD_BLOCK)-5000);
                    }
                }
            }));
        }
        else if(team.getUpgrade().isSharpness()==1){

            ItemBuilder sword = new ItemBuilder(Material.GOLDEN_SWORD);
            sword.setDisplayName(game.translate("space-conquest.gui.upgrade.sharpness-2.name",team.getResource().getOrDefault(Material.GOLD_BLOCK,0)));


            contents.set(1,5, ClickableItem.of((sword.build()), e -> {
                if(team.getResource().containsKey(Material.GOLD_BLOCK)){
                    if(team.getResource().get(Material.GOLD_BLOCK)>=10000){
                        team.getUpgrade().setSharpness(2);
                        player.sendMessage(game.translate("space-conquest.gui.upgrade.sharpness-2.message"));
                        team.getResource().put(Material.GOLD_BLOCK,team.getResource().get(Material.GOLD_BLOCK)-10000);
                    }
                }
            }));
        }
        else contents.set(1,5, null);

        if(team.getUpgrade().isPower()==0){

            ItemBuilder bow = new ItemBuilder(Material.BOW);
            bow.setDisplayName(game.translate("space-conquest.gui.upgrade.power-1.name",team.getResource().getOrDefault(Material.GOLD_BLOCK,0)));
            
            contents.set(1,7, ClickableItem.of((bow.build()), e -> {
                if(team.getResource().containsKey(Material.GOLD_BLOCK)){
                    if(team.getResource().get(Material.GOLD_BLOCK)>=5000){
                        team.getUpgrade().setPower(1);
                        player.sendMessage(game.translate("space-conquest.gui.upgrade.power-1.message"));
                        team.getResource().put(Material.GOLD_BLOCK,team.getResource().get(Material.GOLD_BLOCK)-5000);
                    }
                }
            }));
        }
        else if(team.getUpgrade().isPower()==1){

            ItemBuilder bow = new ItemBuilder(Material.BOW);
            bow.setDisplayName(game.translate("space-conquest.gui.upgrade.power-2.name",team.getResource().getOrDefault(Material.GOLD_BLOCK,0)));

            contents.set(1,7, ClickableItem.of((bow.build()), e -> {
                if(team.getResource().containsKey(Material.GOLD_BLOCK)){
                    if(team.getResource().get(Material.GOLD_BLOCK)>=10000){
                        team.getUpgrade().setPower(2);
                        player.sendMessage(game.translate("space-conquest.gui.upgrade.power-2.message"));
                        team.getResource().put(Material.GOLD_BLOCK,team.getResource().get(Material.GOLD_BLOCK)-10000);
                    }
                }
            }));
        }
        else contents.set(1,7, null);


    }
}
