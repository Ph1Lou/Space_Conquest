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
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class SpecialArtefact implements InventoryProvider {



    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("special")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new SpecialArtefact())
            .size(4, 9)
            .title("Special Artefact")
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

        ItemBuilder tnt = new ItemBuilder(Material.TNT);
        tnt.setDisplayName(String.format("§bTNT§r Coût 150 Emeraudes (§b%d en banque)",team.getResource().getOrDefault(Material.EMERALD_BLOCK,0)));

        contents.set(1,1, ClickableItem.of((tnt.build()), e -> {
            if(team.getResource().containsKey(Material.EMERALD_BLOCK)){
                if(team.getResource().get(Material.EMERALD_BLOCK)>=150){
                    player.getInventory().addItem(new ItemStack(Material.TNT));
                    team.getResource().put(Material.EMERALD_BLOCK,team.getResource().get(Material.EMERALD_BLOCK)-150);
                }
            }
        }));

        ItemBuilder propulsion = new ItemBuilder(Material.GOLDEN_HOE);
        propulsion.setDisplayName(String.format("§bPropulseur§r Coût 300 Emeraudes (§b%d en banque)",team.getResource().getOrDefault(Material.EMERALD_BLOCK,0)));
        propulsion.setLore(Arrays.asList("Propulse la personne qui l'utilise","puis donne l'effet slow falling","pendant 15 secondes"));

        contents.set(1,3, ClickableItem.of((propulsion.build()), e -> {
            if(team.getResource().containsKey(Material.EMERALD_BLOCK)){
                if(team.getResource().get(Material.EMERALD_BLOCK)>=300){
                    player.getInventory().addItem(new ItemBuilder(Material.GOLDEN_HOE).setDisplayName("Propulsion").build());
                    team.getResource().put(Material.EMERALD_BLOCK,team.getResource().get(Material.EMERALD_BLOCK)-300);
                }
            }
        }));

        ItemBuilder levitation = new ItemBuilder(Material.LANTERN);
        levitation.setDisplayName(String.format("§bLévitation§r Coût 300 Emeraudes (§b%d en banque)",team.getResource().getOrDefault(Material.EMERALD_BLOCK,0)));
        levitation.setLore(Arrays.asList("Donne l'effet Lévitation à toutes les personnes","situées à moins de 25 blocks","utilisateur de l'objet non compris"));
        contents.set(1,5, ClickableItem.of((levitation.build()), e -> {
            if(team.getResource().containsKey(Material.EMERALD_BLOCK)){
                if(team.getResource().get(Material.EMERALD_BLOCK)>=300){
                    player.getInventory().addItem(new ItemBuilder(Material.LANTERN).setDisplayName("Levitation").build());
                    team.getResource().put(Material.EMERALD_BLOCK,team.getResource().get(Material.EMERALD_BLOCK)-300);
                }
            }
        }));

        ItemBuilder stick = new ItemBuilder(Material.STICK);
        stick.setDisplayName(String.format("§bKnock Back§r Coût 500 Emeraudes (§b%d en banque)",team.getResource().getOrDefault(Material.EMERALD_BLOCK,0)));
        contents.set(1,7, ClickableItem.of((stick.build()), e -> {
            if(team.getResource().containsKey(Material.EMERALD_BLOCK)){
                if(team.getResource().get(Material.EMERALD_BLOCK)>=500){
                    player.getInventory().addItem(new ItemBuilder(Material.STICK).addEnchant(Enchantment.KNOCKBACK,3).build());
                    team.getResource().put(Material.EMERALD_BLOCK,team.getResource().get(Material.EMERALD_BLOCK)-500);
                }
            }
        }));

    }
}
