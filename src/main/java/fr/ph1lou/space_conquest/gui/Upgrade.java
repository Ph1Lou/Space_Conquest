package fr.ph1lou.space_conquest.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.space_conquest.Main;
import fr.ph1lou.space_conquest.game.GameManager;
import fr.ph1lou.space_conquest.game.Team;
import fr.ph1lou.space_conquest.utils.ItemBuilder;
import fr.ph1lou.space_conquest.utils.TexturedItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Upgrade implements InventoryProvider {

    private final Team team;
    public Upgrade(Team team){
        this.team = team;
    }

    public static SmartInventory getInventory() {
        return Upgrade.getInventory(null);
    }

    public static SmartInventory getInventory(Team team) {
        return SmartInventory.builder()
                .id("upgrade")
                .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
                .provider(new Upgrade(team))
                .size(4, 9)
                .title(JavaPlugin.getPlugin(Main.class).getLangManager().getTranslation("space-conquest.gui.upgrade.name"))
                .closeable(true)
                .build();
    }

    @Override
    public void init(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();

        contents.set(0,0,
                ClickableItem.of((TexturedItem.BLUE_BUTTON.getItemBuilder().setDisplayName(game.translate("space-conquest.gui.ressources.title"))
                        .build()),e -> Ressources.getInventory(this.team).open(player)));

        contents.set(0,4,
                ClickableItem.of((TexturedItem.YELLOW_BUTTON.getItemBuilder().setDisplayName(game.translate("space-conquest.gui.upgrade.name"))
                        .build()),e -> Upgrade.getInventory(this.team).open(player)));

        if(this.team == null){
            contents.set(0,2,
                    ClickableItem.of((TexturedItem.RED_BUTTON.getItemBuilder().setDisplayName(game.translate("space-conquest.gui.boutique.title"))
                            .build()),e -> Boutique.INVENTORY.open(player)));

            contents.set(0,6,
                    ClickableItem.of((TexturedItem.GREEN_BUTTON.getItemBuilder().setDisplayName(game.translate("space-conquest.gui.special-artefact.title"))
                            .build()),e -> SpecialArtefact.INVENTORY.open(player)));

            contents.set(0,8,
                    ClickableItem.of((TexturedItem.PURPLE_BUTTON.getItemBuilder().setDisplayName(game.translate("space-conquest.gui.beacon.name"))
                            .build()),e -> Beacon.INVENTORY.open(player)));
        }
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();

        Team team = game.getTeam(player).orElse(this.team);

        if(team == null) return;

        if(team.getUpgrade().getChestPlate()==0){

            ItemBuilder chestPlate = new ItemBuilder(Material.IRON_CHESTPLATE)
                    .setDisplayName(game.translate("space-conquest.gui.upgrade.iron-chest-plate.name",
                            team.getResource().getOrDefault(TexturedItem.IRON_RESSOURCE,0)));

            contents.set(1,1, ClickableItem.of((chestPlate.build()),
                    e -> team.spend(10000,TexturedItem.IRON_RESSOURCE,
                            () -> {
                                if(this.team == null){
                                    team.getUpgrade().setChestPlate(1);
                                    team.sendMessage("space-conquest.gui.upgrade.iron-chest-plate.message");
                                }
                            })));
        }
        else if(team.getUpgrade().getChestPlate()==1){

            ItemBuilder chestPlate = new ItemBuilder(Material.DIAMOND_CHESTPLATE).setDisplayName(game.translate("space-conquest.gui.upgrade.diamond-chest-plate.name",
                    team.getResource().getOrDefault(TexturedItem.DIAMOND_RESSOURCE,0)));

            contents.set(1,1, ClickableItem.of((chestPlate.build()),
                    e -> team.spend(20000,TexturedItem.DIAMOND_RESSOURCE,
                            () -> {
                                if(this.team == null){
                                    team.getUpgrade().setChestPlate(2);
                                    team.sendMessage("space-conquest.gui.upgrade.diamond-chest-plate.message");
                                }

                            })));
        }
        else contents.set(1,1, null);

        if(team.getUpgrade().isProtection()==0){

            ItemBuilder anvil = TexturedItem.PROTECTION_UPGRADE.getItemBuilder();
            anvil.setDisplayName(game.translate("space-conquest.gui.upgrade.protection-1.name",team.getResource().getOrDefault(TexturedItem.GOLD_RESSOURCE,0)));


            contents.set(1,3, ClickableItem.of((anvil.build()), e -> team.spend(10000,TexturedItem.GOLD_RESSOURCE,
                    () -> {
                        if(this.team == null){
                            team.getUpgrade().setProtection(1);
                            team.sendMessage("space-conquest.gui.upgrade.protection-1.message");
                        }

                    })));
        }
        else if(team.getUpgrade().isProtection()==1){

            ItemBuilder anvil = TexturedItem.PROTECTION_UPGRADE.getItemBuilder();
            anvil.setDisplayName(game.translate("space-conquest.gui.upgrade.protection-2.name",team.getResource().getOrDefault(TexturedItem.GOLD_RESSOURCE,0)));


            contents.set(1,3, ClickableItem.of((anvil.build()), e -> team.spend(20000,TexturedItem.GOLD_RESSOURCE,
                    () -> {
                        if(this.team == null){
                            team.getUpgrade().setProtection(2);
                            team.sendMessage("space-conquest.gui.upgrade.protection-2.message");
                        }
                    })));
        }
        else contents.set(1,3, null);

        if(team.getUpgrade().isSharpness()==0){

            ItemBuilder sword = TexturedItem.SHARPNESS_UPGRADE.getItemBuilder();
            sword.setDisplayName(game.translate("space-conquest.gui.upgrade.sharpness-1.name",team.getResource().getOrDefault(TexturedItem.GOLD_RESSOURCE,0)));


            contents.set(1,5, ClickableItem.of((sword.build()), e -> team.spend(10000,TexturedItem.GOLD_RESSOURCE,
                    () -> {
                        if(this.team == null){
                            team.getUpgrade().setSharpness(1);
                            team.sendMessage("space-conquest.gui.upgrade.sharpness-1.message");
                        }

                    })));
        }
        else if(team.getUpgrade().isSharpness()==1){

            ItemBuilder sword = TexturedItem.SHARPNESS_UPGRADE.getItemBuilder();
            sword.setDisplayName(game.translate("space-conquest.gui.upgrade.sharpness-2.name",team.getResource().getOrDefault(TexturedItem.GOLD_RESSOURCE,0)));


            contents.set(1,5, ClickableItem.of((sword.build()), e -> team.spend(20000,TexturedItem.GOLD_RESSOURCE,
                    () -> {
                        if(this.team == null){
                            team.getUpgrade().setSharpness(2);
                            team.sendMessage("space-conquest.gui.upgrade.sharpness-2.message");
                        }

                    })));
        }
        else contents.set(1,5, null);

        if(team.getUpgrade().isPower()==0){

            ItemBuilder bow = new ItemBuilder(Material.BOW);
            bow.setDisplayName(game.translate("space-conquest.gui.upgrade.power-1.name",team.getResource().getOrDefault(TexturedItem.GOLD_RESSOURCE,0)));

            contents.set(1,7, ClickableItem.of((bow.build()), e -> team.spend(10000,TexturedItem.GOLD_RESSOURCE,
                    () -> {
                        if(this.team == null){
                            team.getUpgrade().setPower(1);
                            team.sendMessage("space-conquest.gui.upgrade.power-1.message");
                        }

                    })));
        }
        else if(team.getUpgrade().isPower()==1){

            ItemBuilder bow = new ItemBuilder(Material.BOW);
            bow.setDisplayName(game.translate("space-conquest.gui.upgrade.power-2.name",team.getResource().getOrDefault(TexturedItem.GOLD_RESSOURCE,0)));

            contents.set(1,7, ClickableItem.of((bow.build()), e -> team.spend(20000,TexturedItem.GOLD_RESSOURCE,
                    () -> {
                        if(this.team == null){
                            team.getUpgrade().setPower(2);
                            team.sendMessage("space-conquest.gui.upgrade.power-2.message");
                        }

                    })));
        }
        else contents.set(1,7, null);





    }
}
