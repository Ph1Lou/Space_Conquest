package io.github.ph1lou.space_conquest.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.space_conquest.Main;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.utils.ItemBuilder;
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
                ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON).setDisplayName(game.translate("space-conquest.gui.ressources.title"))
                        .build()),e -> Ressources.INVENTORY.open(player)));
        contents.set(0,2,
                ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON).setDisplayName(game.translate("space-conquest.gui.boutique.title"))
                        .build()),e -> Boutique.INVENTORY.open(player)));
        contents.set(0,4,
                ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON).setDisplayName(game.translate("space-conquest.gui.upgrade.name"))
                        .build()),e -> Upgrade.INVENTORY.open(player)));
        contents.set(0,6,
                ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON).setDisplayName(game.translate("space-conquest.gui.special-artefact.title"))
                        .build()),e -> SpecialArtefact.INVENTORY.open(player)));

        contents.set(0,8,
                ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON).setDisplayName(game.translate("space-conquest.gui.beacon.name"))
                        .build()),e -> Beacon.INVENTORY.open(player)));



    }

    @Override
    public void update(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();

        game.getTeam(player).ifPresent(team -> {
            ItemBuilder woodenSword = new ItemBuilder(Material.WOODEN_SWORD);

            woodenSword.addEnchant(Enchantment.DAMAGE_ALL,team.getUpgrade().isSharpness());


            ItemStack tempWoodenSword = woodenSword.build().clone();

            contents.set(1,1, ClickableItem.of((woodenSword.setDisplayName(game.translate("space-conquest.gui.boutique.wooden-sword",team.getResource().getOrDefault(Material.IRON_BLOCK,0))).build()), e -> {
                if(team.getResource().containsKey(Material.IRON_BLOCK)){
                    if(team.getResource().get(Material.IRON_BLOCK)>=500){
                        player.getInventory().addItem(tempWoodenSword);
                        team.getResource().put(Material.IRON_BLOCK,team.getResource().get(Material.IRON_BLOCK)-500);
                    }
                }
            }));

            ItemBuilder ironSword = new ItemBuilder(Material.IRON_SWORD);

            ironSword.addEnchant(Enchantment.DAMAGE_ALL,team.getUpgrade().isSharpness());

            ItemStack temp = ironSword.build().clone();

            contents.set(1,3, ClickableItem.of((ironSword.setDisplayName(game.translate("space-conquest.gui.boutique.iron-sword",team.getResource().getOrDefault(Material.IRON_BLOCK,0))).build()), e -> {
                if(team.getResource().containsKey(Material.IRON_BLOCK)){
                    if(team.getResource().get(Material.IRON_BLOCK)>=1500){
                        player.getInventory().addItem(temp);
                        team.getResource().put(Material.IRON_BLOCK,team.getResource().get(Material.IRON_BLOCK)-1500);
                    }
                }
            }));

            ItemBuilder diamondSword = new ItemBuilder(Material.DIAMOND_SWORD);

            diamondSword.addEnchant(Enchantment.DAMAGE_ALL,team.getUpgrade().isSharpness());

            ItemStack tempSword = diamondSword.build().clone();

            contents.set(1,5, ClickableItem.of((diamondSword.setDisplayName(game.translate("space-conquest.gui.boutique.diamond-sword",team.getResource().getOrDefault(Material.DIAMOND_BLOCK,0))).build()), e -> {
                if(team.getResource().containsKey(Material.DIAMOND_BLOCK)){
                    if(team.getResource().get(Material.DIAMOND_BLOCK)>=3000){
                        player.getInventory().addItem(tempSword);
                        team.getResource().put(Material.DIAMOND_BLOCK,team.getResource().get(Material.DIAMOND_BLOCK)-3000);
                    }
                }
            }));

            ItemBuilder bow = new ItemBuilder(Material.BOW);


            bow.addEnchant(Enchantment.ARROW_DAMAGE,team.getUpgrade().isPower());

            ItemStack tempBow = bow.build().clone();

            contents.set(1,7, ClickableItem.of((bow.setDisplayName(game.translate("space-conquest.gui.boutique.bow",team.getResource().getOrDefault(Material.GOLD_BLOCK,0))).build()), e -> {
                if(team.getResource().containsKey(Material.GOLD_BLOCK)){
                    if(team.getResource().get(Material.GOLD_BLOCK)>=2000){
                        player.getInventory().addItem(tempBow);
                        team.getResource().put(Material.GOLD_BLOCK,team.getResource().get(Material.GOLD_BLOCK)-2000);
                    }
                }
            }));

            ItemBuilder arrow = new ItemBuilder(Material.ARROW);
            arrow.setAmount(16);
            arrow.setDisplayName(game.translate("space-conquest.gui.boutique.arrow",team.getResource().getOrDefault(Material.DIAMOND_BLOCK,0)));
            contents.set(3,1, ClickableItem.of((arrow.build()), e -> {
                if(team.getResource().containsKey(Material.DIAMOND_BLOCK)){
                    if(team.getResource().get(Material.DIAMOND_BLOCK)>=500){
                        player.getInventory().addItem(new ItemStack(Material.ARROW,16));
                        team.getResource().put(Material.DIAMOND_BLOCK,team.getResource().get(Material.DIAMOND_BLOCK)-500);
                    }
                }
            }));

            ItemBuilder wool = new ItemBuilder(team.getColorTeam().getConstructionMaterial());
            wool.setAmount(32);
            wool.setDisplayName(game.translate("space-conquest.gui.boutique.wool",team.getResource().getOrDefault(Material.IRON_BLOCK,0)));


            contents.set(3,3, ClickableItem.of((wool.build()), e -> {
                if(team.getResource().containsKey(Material.IRON_BLOCK)){
                    if(team.getResource().get(Material.IRON_BLOCK)>=64){
                        player.getInventory().addItem(new ItemStack(team.getColorTeam().getConstructionMaterial(),32));
                        team.getResource().put(Material.IRON_BLOCK,team.getResource().get(Material.IRON_BLOCK)-64);
                    }
                }
            }));

            ItemBuilder shear = new ItemBuilder(Material.SHEARS);
            shear.setDisplayName(game.translate("space-conquest.gui.boutique.shear",team.getResource().getOrDefault(Material.IRON_BLOCK,0)));


            contents.set(3,5, ClickableItem.of((shear.build()), e -> {
                if(team.getResource().containsKey(Material.IRON_BLOCK)){
                    if(team.getResource().get(Material.IRON_BLOCK)>=200){
                        player.getInventory().addItem(new ItemStack(Material.SHEARS));
                        team.getResource().put(Material.IRON_BLOCK,team.getResource().get(Material.IRON_BLOCK)-200);
                    }
                }
            }));

        });


    }
}
