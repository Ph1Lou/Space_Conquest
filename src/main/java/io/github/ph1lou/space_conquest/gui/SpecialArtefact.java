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
import org.bukkit.potion.PotionEffectType;

public class SpecialArtefact implements InventoryProvider {



    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("special")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new SpecialArtefact())
            .size(6, 9)
            .title(JavaPlugin.getPlugin(Main.class).getLangManager()
                    .getTranslation("space-conquest.gui.special-artefact.title"))
            .closeable(true)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();

        contents.set(0,0,
                ClickableItem.of((TexturedItem.BLUE_BUTTON.getItemBuilder()
                        .setDisplayName(game.translate("space-conquest.gui.ressources.title"))
                        .build()),e -> Ressources.getInventory().open(player)));
        contents.set(0,2,
                ClickableItem.of((TexturedItem.RED_BUTTON.getItemBuilder()
                        .setDisplayName(game.translate("space-conquest.gui.boutique.title"))
                        .build()),e -> Boutique.INVENTORY.open(player)));
        contents.set(0,4,
                ClickableItem.of((TexturedItem.YELLOW_BUTTON.getItemBuilder()
                        .setDisplayName(game.translate("space-conquest.gui.upgrade.name"))
                        .build()),e -> Upgrade.getInventory().open(player)));
        contents.set(0,6,
                ClickableItem.of((TexturedItem.GREEN_BUTTON.getItemBuilder()
                        .setDisplayName(game.translate("space-conquest.gui.special-artefact.title"))
                        .build()),e -> SpecialArtefact.INVENTORY.open(player)));

        contents.set(0,8,
                ClickableItem.of((TexturedItem.PURPLE_BUTTON.getItemBuilder()
                        .setDisplayName(game.translate("space-conquest.gui.beacon.name"))
                        .build()),e -> Beacon.INVENTORY.open(player)));

    }

    @Override
    public void update(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();
        game.getTeam(player).ifPresent(team -> {
            ItemBuilder tnt = new ItemBuilder(Material.TNT);
            tnt.setDisplayName(game.translate("space-conquest.gui.special-artefact.tnt",
                    team.getResource().getOrDefault(TexturedItem.EMERALD_RESSOURCE,0)));

            contents.set(1,1, ClickableItem.of((tnt.build()), 
                    e -> team.spend(150,TexturedItem.EMERALD_RESSOURCE,
                            () -> player.getInventory().addItem(new ItemStack(Material.TNT)))));

            ItemBuilder propulsion = TexturedItem.PROPULSOR.getItemBuilder();
            propulsion.setDisplayName(game.translate("space-conquest.gui.special-artefact.propulser.name",
                    team.getResource().getOrDefault(TexturedItem.EMERALD_RESSOURCE,0)));
            propulsion.setLore(game.translateArray("space-conquest.gui.special-artefact.propulser.description"));

            contents.set(1,3, ClickableItem.of((propulsion.build()), e -> team.spend(300,TexturedItem.EMERALD_RESSOURCE,
                    () -> player.getInventory().addItem(TexturedItem.PROPULSOR.getItemBuilder()
                            .setDisplayName(game.translate("space-conquest.gui.special-artefact.propulser.item-name"))
                            .addNBTTag("propulsion",true)
                            .build()))));

            ItemBuilder levitation = TexturedItem.LEVITATOR.getItemBuilder();
            levitation.setDisplayName(game.translate("space-conquest.gui.special-artefact.levitation.name",
                    team.getResource().getOrDefault(TexturedItem.EMERALD_RESSOURCE,0)));
            levitation.setLore(game.translateArray("space-conquest.gui.special-artefact.levitation.description"));
            contents.set(1,5, ClickableItem.of((levitation.build()), e -> {
                if(team.getResource().containsKey(TexturedItem.EMERALD_RESSOURCE)){
                    if(team.getResource().get(TexturedItem.EMERALD_RESSOURCE)>=300){
                        player.getInventory().addItem(TexturedItem.LEVITATOR.getItemBuilder()
                                .setDisplayName(game.translate("space-conquest.gui.special-artefact.levitation.item-name"))
                                .addNBTTag("levitation",true)
                                .build());
                        team.getResource().put(TexturedItem.EMERALD_RESSOURCE,team.getResource().get(TexturedItem.EMERALD_RESSOURCE)-300);
                    }
                }
            }));

            ItemBuilder stick = TexturedItem.KNOCK_BACK.getItemBuilder();
            stick.setDisplayName(game.translate("space-conquest.gui.special-artefact.knockback-stick.name",
                    team.getResource().getOrDefault(TexturedItem.EMERALD_RESSOURCE,0)));
            contents.set(1,7, ClickableItem.of((stick.build()), e -> {
                if(team.getResource().containsKey(TexturedItem.EMERALD_RESSOURCE)){
                    if(team.getResource().get(TexturedItem.EMERALD_RESSOURCE)>=3000){
                        player.getInventory().addItem(TexturedItem.KNOCK_BACK.getItemBuilder()
                                .addEnchant(Enchantment.KNOCKBACK,1)
                                .setDisplayName(game.translate("space-conquest.gui.special-artefact.knockback-stick.item-name"))
                                .build());
                        team.getResource().put(TexturedItem.EMERALD_RESSOURCE,team.getResource().get(TexturedItem.EMERALD_RESSOURCE)-3000);
                    }
                }
            }));

            ItemBuilder snowBall = TexturedItem.AUTO_WALL.getItemBuilder();
            snowBall.setDisplayName(game.translate("space-conquest.gui.special-artefact.bridge.name",
                    team.getResource().getOrDefault(TexturedItem.IRON_RESSOURCE,0)));
            contents.set(3,1, ClickableItem.of((snowBall.build()), e -> team.spend(1000,TexturedItem.IRON_RESSOURCE,
                    () ->  player.getInventory().addItem(TexturedItem.AUTO_WALL.getItemBuilder()
                            .setDisplayName(game.translate("space-conquest.gui.special-artefact.bridge.item-name"))
                            .build()))));

            ItemBuilder bigTnt = TexturedItem.DETONATOR.getItemBuilder();
            bigTnt.setDisplayName(game.translate("space-conquest.gui.special-artefact.explosion.name",
                    team.getResource().getOrDefault(TexturedItem.EMERALD_RESSOURCE,0)));
            contents.set(3,3, ClickableItem.of((bigTnt.build()), e -> team.spend(500,TexturedItem.EMERALD_RESSOURCE,
                    () ->    player.getInventory().addItem(TexturedItem.DETONATOR.getItemBuilder()
                            .setDisplayName(game.translate("space-conquest.gui.special-artefact.explosion.item-name"))
                            .addNBTTag("explosion",true)
                            .build()))));

            ItemBuilder fireCharge = TexturedItem.FIRE_CHARGE.getItemBuilder();
            fireCharge.setDisplayName(game.translate("space-conquest.gui.special-artefact.fire-charge.name",
                    team.getResource().getOrDefault(TexturedItem.EMERALD_RESSOURCE,0)));
            contents.set(3,5, ClickableItem.of((fireCharge.build()), e -> team.spend(500,TexturedItem.EMERALD_RESSOURCE,
                    () ->   player.getInventory()
                            .addItem(TexturedItem.FIRE_CHARGE.getItemBuilder()
                                    .setDisplayName(game.translate("space-conquest.gui.special-artefact.fire-charge.item-name"))
                                    .addNBTTag("fire-charge",true).build()))));

            ItemBuilder gravityBlock = new ItemBuilder(Material.REDSTONE_LAMP);
            gravityBlock.setDisplayName(game.translate("space-conquest.gui.special-artefact.no-gravity.name",
                    team.getResource().getOrDefault(TexturedItem.IRON_RESSOURCE,0)))
                    .setLore(game.translate("space-conquest.gui.special-artefact.no-gravity.lore"));


            contents.set(3,7, ClickableItem.of((gravityBlock.build()), e -> team.spend(1000,TexturedItem.IRON_RESSOURCE,
                    () ->    player.getInventory().addItem(new ItemBuilder(Material.REDSTONE_LAMP)
                            .setDisplayName(game.translate("space-conquest.gui.special-artefact.no-gravity.item-name"))
                            .addNBTTag("no-gravity",true)
                            .build()))));

            ItemBuilder swapArrow = TexturedItem.REVERSE_ARROW.getItemBuilder();
            swapArrow.setDisplayName(game.translate("space-conquest.gui.special-artefact.permutation.name",
                    team.getResource().getOrDefault(TexturedItem.EMERALD_RESSOURCE,0)));


            contents.set(5,1, ClickableItem.of((swapArrow.build()), e -> team.spend(500,TexturedItem.EMERALD_RESSOURCE,
                    () ->  player.getInventory().addItem(TexturedItem.REVERSE_ARROW.getItemBuilder()
                            .setDisplayName(game.translate("space-conquest.gui.special-artefact.permutation.item-name"))
                            .addPotionEffect(PotionEffectType.BAD_OMEN,0).build()))));

        });



    }
}
