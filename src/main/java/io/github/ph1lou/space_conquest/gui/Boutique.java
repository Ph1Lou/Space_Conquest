package io.github.ph1lou.space_conquest.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.space_conquest.Main;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.utils.ItemBuilder;
import io.github.ph1lou.space_conquest.utils.TexturedItem;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class Boutique implements InventoryProvider {



    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("boutique")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new Boutique())
            .size(4, 9)
            .title(JavaPlugin.getPlugin(Main.class).getLangManager().getTranslation("space-conquest.gui.boutique.title"))
            .closeable(true)
            .parent(Ressources.INVENTORY)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();

        contents.set(0,0,
                ClickableItem.of((TexturedItem.BLUE_BUTTON.getItemBuilder().setDisplayName(game.translate("space-conquest.gui.ressources.title"))
                        .build()),e -> Ressources.INVENTORY.open(player)));
        contents.set(0,2,
                ClickableItem.of((TexturedItem.RED_BUTTON.getItemBuilder().setDisplayName(game.translate("space-conquest.gui.boutique.title"))
                        .build()),e -> Boutique.INVENTORY.open(player)));
        contents.set(0,4,
                ClickableItem.of((TexturedItem.YELLOW_BUTTON.getItemBuilder().setDisplayName(game.translate("space-conquest.gui.upgrade.name"))
                        .build()),e -> Upgrade.INVENTORY.open(player)));
        contents.set(0,6,
                ClickableItem.of((TexturedItem.GREEN_BUTTON.getItemBuilder().setDisplayName(game.translate("space-conquest.gui.special-artefact.title"))
                        .build()),e -> SpecialArtefact.INVENTORY.open(player)));

        contents.set(0,8,
                ClickableItem.of((TexturedItem.PURPLE_BUTTON.getItemBuilder().setDisplayName(game.translate("space-conquest.gui.beacon.name"))
                        .build()),e -> Beacon.INVENTORY.open(player)));



    }

    @Override
    public void update(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();

        game.getTeam(player).ifPresent(team -> {
            ItemBuilder woodenSword = TexturedItem.SWORD_LEVEL_1.getItemBuilder();

            woodenSword.addEnchant(Enchantment.DAMAGE_ALL,team.getUpgrade().isSharpness());


            ItemStack tempWoodenSword = woodenSword.build().clone();

            contents.set(1,1, ClickableItem.of((woodenSword.setDisplayName(game.translate("space-conquest.gui.boutique.wooden-sword",
                    team.getResource().getOrDefault(TexturedItem.IRON_RESSOURCE,0))).build()), e -> team.spend(500,TexturedItem.IRON_RESSOURCE,
                    () -> player.getInventory().addItem(tempWoodenSword))));

            ItemBuilder ironSword = TexturedItem.SWORD_LEVEL_2.getItemBuilder();

            ironSword.addEnchant(Enchantment.DAMAGE_ALL,team.getUpgrade().isSharpness());

            ItemStack temp = ironSword.build().clone();

            contents.set(1,3, ClickableItem.of((ironSword.setDisplayName(game.translate("space-conquest.gui.boutique.iron-sword",
                    team.getResource().getOrDefault(TexturedItem.IRON_RESSOURCE,0))).build()), e -> team.spend(1500,TexturedItem.IRON_RESSOURCE,
                            () ->  player.getInventory().addItem(temp))));

            ItemBuilder diamondSword = TexturedItem.SWORD_LEVEL_3.getItemBuilder();

            diamondSword.addEnchant(Enchantment.DAMAGE_ALL,team.getUpgrade().isSharpness());

            ItemStack tempSword = diamondSword.build().clone();

            contents.set(1,5, ClickableItem.of((diamondSword.setDisplayName(game.translate("space-conquest.gui.boutique.diamond-sword",
                    team.getResource().getOrDefault(TexturedItem.DIAMOND_RESSOURCE,0))).build()), e -> team.spend(3000,TexturedItem.DIAMOND_RESSOURCE,
                            () ->  player.getInventory().addItem(tempSword))));

            ItemBuilder bow = new ItemBuilder(Material.BOW);


            bow.addEnchant(Enchantment.ARROW_DAMAGE,team.getUpgrade().isPower());

            ItemStack tempBow = bow.build().clone();

            contents.set(1,7, ClickableItem.of((bow.setDisplayName(game.translate("space-conquest.gui.boutique.bow",
                    team.getResource().getOrDefault(TexturedItem.GOLD_RESSOURCE,0))).build()), e -> team.spend(2000,TexturedItem.GOLD_RESSOURCE,
                            () ->   player.getInventory().addItem(tempBow))));

            ItemBuilder arrow = TexturedItem.ARROW.getItemBuilder();
            arrow.setAmount(16);
            arrow.setDisplayName(game.translate("space-conquest.gui.boutique.arrow",
                    team.getResource().getOrDefault(TexturedItem.DIAMOND_RESSOURCE,0)));
            contents.set(3,1, ClickableItem.of((arrow.build()), e -> team.spend(500,TexturedItem.DIAMOND_RESSOURCE,
                    () ->   player.getInventory().addItem(TexturedItem.ARROW.getItemBuilder().setAmount(16).build()))));

            ItemBuilder wool = new ItemBuilder(team.getColorTeam().getConstructionMaterial());
            wool.setAmount(32);
            wool.setDisplayName(game.translate("space-conquest.gui.boutique.wool",
                    team.getResource().getOrDefault(TexturedItem.IRON_RESSOURCE,0)));


            contents.set(3,3, ClickableItem.of((wool.build()), e -> team.spend(64,TexturedItem.IRON_RESSOURCE,
                    () ->   player.getInventory().addItem(new ItemStack(team.getColorTeam().getConstructionMaterial(),32)))));

            ItemBuilder shear = TexturedItem.SHEARS.getItemBuilder();
            shear.setDisplayName(game.translate("space-conquest.gui.boutique.shear",
                    team.getResource().getOrDefault(TexturedItem.IRON_RESSOURCE,0)));


            contents.set(3,5, ClickableItem.of((shear.build()), e -> team.spend(200,TexturedItem.IRON_RESSOURCE,
                    () ->   player.getInventory().addItem(TexturedItem.SHEARS.getItemBuilder().build()))));

        });


    }
}
