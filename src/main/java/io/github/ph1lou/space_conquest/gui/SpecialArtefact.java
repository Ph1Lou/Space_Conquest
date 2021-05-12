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
            .title(JavaPlugin.getPlugin(Main.class).getLangManager().getTranslation("space-conquest.gui.special-artefact.title"))
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

        if(team==null){
            return;
        }

        ItemBuilder tnt = new ItemBuilder(Material.TNT);
        tnt.setDisplayName(game.translate("space-conquest.gui.special-artefact.tnt",team.getResource().getOrDefault(Material.EMERALD_BLOCK,0)));

        contents.set(1,1, ClickableItem.of((tnt.build()), e -> {
            if(team.getResource().containsKey(Material.EMERALD_BLOCK)){
                if(team.getResource().get(Material.EMERALD_BLOCK)>=150){
                    player.getInventory().addItem(new ItemStack(Material.TNT));
                    team.getResource().put(Material.EMERALD_BLOCK,team.getResource().get(Material.EMERALD_BLOCK)-150);
                }
            }
        }));

        ItemBuilder propulsion = new ItemBuilder(Material.GOLDEN_HOE);
        propulsion.setDisplayName(game.translate("space-conquest.gui.special-artefact.propulser.name",team.getResource().getOrDefault(Material.EMERALD_BLOCK,0)));
        propulsion.setLore(game.translateArray("space-conquest.gui.special-artefact.propulser.description"));

        contents.set(1,3, ClickableItem.of((propulsion.build()), e -> {
            if(team.getResource().containsKey(Material.EMERALD_BLOCK)){
                if(team.getResource().get(Material.EMERALD_BLOCK)>=300){
                    player.getInventory().addItem(new ItemBuilder(Material.GOLDEN_HOE).setDisplayName(game.translate("space-conquest.gui.special-artefact.propulser.item-name")).addNBTTag("propulsion",true).build());
                    team.getResource().put(Material.EMERALD_BLOCK,team.getResource().get(Material.EMERALD_BLOCK)-300);
                }
            }
        }));

        ItemBuilder levitation = new ItemBuilder(Material.LANTERN);
        levitation.setDisplayName(game.translate("space-conquest.gui.special-artefact.levitation.name",team.getResource().getOrDefault(Material.EMERALD_BLOCK,0)));
        levitation.setLore(game.translateArray("space-conquest.gui.special-artefact.levitation.description"));
        contents.set(1,5, ClickableItem.of((levitation.build()), e -> {
            if(team.getResource().containsKey(Material.EMERALD_BLOCK)){
                if(team.getResource().get(Material.EMERALD_BLOCK)>=300){
                    player.getInventory().addItem(new ItemBuilder(Material.LANTERN).setDisplayName(game.translate("space-conquest.gui.special-artefact.levitation.item-name")).addNBTTag("levitation",true).build());
                    team.getResource().put(Material.EMERALD_BLOCK,team.getResource().get(Material.EMERALD_BLOCK)-300);
                }
            }
        }));

        ItemBuilder stick = new ItemBuilder(Material.STICK);
        stick.setDisplayName(game.translate("space-conquest.gui.special-artefact.knockback-stick.name",team.getResource().getOrDefault(Material.EMERALD_BLOCK,0)));
        contents.set(1,7, ClickableItem.of((stick.build()), e -> {
            if(team.getResource().containsKey(Material.EMERALD_BLOCK)){
                if(team.getResource().get(Material.EMERALD_BLOCK)>=3000){
                    player.getInventory().addItem(new ItemBuilder(Material.STICK).addEnchant(Enchantment.KNOCKBACK,1).setDisplayName(game.translate("space-conquest.gui.special-artefact.knockback-stick.item-name")).build());
                    team.getResource().put(Material.EMERALD_BLOCK,team.getResource().get(Material.EMERALD_BLOCK)-3000);
                }
            }
        }));

        ItemBuilder snowBall = new ItemBuilder(Material.EGG);
        snowBall.setDisplayName(game.translate("space-conquest.gui.special-artefact.bridge.name",team.getResource().getOrDefault(Material.IRON_BLOCK,0)));
        contents.set(3,1, ClickableItem.of((snowBall.build()), e -> {
            if(team.getResource().containsKey(Material.IRON_BLOCK)){
                if(team.getResource().get(Material.IRON_BLOCK)>=1000){
                    player.getInventory().addItem(new ItemBuilder(Material.EGG).setDisplayName(game.translate("space-conquest.gui.special-artefact.bridge.item-name")).build());
                    team.getResource().put(Material.IRON_BLOCK,team.getResource().get(Material.IRON_BLOCK)-1000);
                }
            }
        }));

        ItemBuilder bigTnt = new ItemBuilder(Material.STONE_BUTTON);
        bigTnt.setDisplayName(game.translate("space-conquest.gui.special-artefact.explosion.name",team.getResource().getOrDefault(Material.EMERALD_BLOCK,0)));
        contents.set(3,3, ClickableItem.of((bigTnt.build()), e -> {
            if(team.getResource().containsKey(Material.EMERALD_BLOCK)){
                if(team.getResource().get(Material.EMERALD_BLOCK)>=500){
                    player.getInventory().addItem(new ItemBuilder(Material.STONE_BUTTON).setDisplayName(game.translate("space-conquest.gui.special-artefact.explosion.item-name")).addNBTTag("explosion",true).build());
                    team.getResource().put(Material.EMERALD_BLOCK,team.getResource().get(Material.EMERALD_BLOCK)-500);
                }
            }
        }));

        ItemBuilder fireCharge = new ItemBuilder(Material.FIRE_CHARGE);
        fireCharge.setDisplayName(game.translate("space-conquest.gui.special-artefact.fire-charge.name",team.getResource().getOrDefault(Material.EMERALD_BLOCK,0)));
        contents.set(3,5, ClickableItem.of((fireCharge.build()), e -> {
            if(team.getResource().containsKey(Material.EMERALD_BLOCK)){
                if(team.getResource().get(Material.EMERALD_BLOCK)>=500){
                    player.getInventory().addItem(new ItemBuilder(Material.FIRE_CHARGE).setDisplayName(game.translate("space-conquest.gui.special-artefact.fire-charge.item-name")).addNBTTag("fire-charge",true).build());
                    team.getResource().put(Material.EMERALD_BLOCK,team.getResource().get(Material.EMERALD_BLOCK)-500);
                }
            }
        }));

        ItemBuilder gravityBlock = new ItemBuilder(Material.REDSTONE_LAMP);
        gravityBlock.setDisplayName(game.translate("space-conquest.gui.special-artefact.no-gravity.name",team.getResource().getOrDefault(Material.IRON_BLOCK,0))).setLore("Se Pose dans le vide même si il n'y a pas de blocs autour");


        contents.set(3,7, ClickableItem.of((gravityBlock.build()), e -> {
            if(team.getResource().containsKey(Material.IRON_BLOCK)){
                if(team.getResource().get(Material.IRON_BLOCK)>=1000){
                    player.getInventory().addItem(new ItemBuilder(Material.REDSTONE_LAMP).setDisplayName(game.translate("space-conquest.gui.special-artefact.no-gravity.item-name")).addNBTTag("no-gravity",true).build());
                    team.getResource().put(Material.IRON_BLOCK,team.getResource().get(Material.IRON_BLOCK)-1000);
                }
            }
        }));

        ItemBuilder swapArrow = new ItemBuilder(Material.TIPPED_ARROW);
        swapArrow.setDisplayName(game.translate("space-conquest.gui.special-artefact.permutation.name",team.getResource().getOrDefault(Material.EMERALD_BLOCK,0)));


        contents.set(5,1, ClickableItem.of((swapArrow.build()), e -> {
            if(team.getResource().containsKey(Material.EMERALD_BLOCK)){
                if(team.getResource().get(Material.EMERALD_BLOCK)>=500){
                    player.getInventory().addItem(new ItemBuilder(Material.TIPPED_ARROW).setDisplayName(game.translate("space-conquest.gui.special-artefact.permutation.item-name")).addPotionEffect(PotionEffectType.BAD_OMEN,0).build());
                    team.getResource().put(Material.EMERALD_BLOCK,team.getResource().get(Material.EMERALD_BLOCK)-500);
                }
            }
        }));

    }
}
