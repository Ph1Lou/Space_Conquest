package io.github.ph1lou.space_conquest.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.Pagination;
import fr.minuskube.inv.content.SlotIterator;
import io.github.ph1lou.space_conquest.Main;
import io.github.ph1lou.space_conquest.enums.TowerMode;
import io.github.ph1lou.space_conquest.game.Area;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.BoundingBox;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Beacon implements InventoryProvider {



    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("beacon")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new Beacon())
            .size(6, 9)
            .title(JavaPlugin.getPlugin(Main.class).getLangManager().getTranslation("space-conquest.gui.beacon.name"))
            .closeable(true)
            .parent(Ressources.INVENTORY)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();

        contents.set(0,0,ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON)
                .setDisplayName(game.translate("space-conquest.gui.ressources.title"))
                .build()),e -> Ressources.INVENTORY.open(player)));
        contents.set(0,2,
                ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON)
                        .setDisplayName(game.translate("space-conquest.gui.boutique.title"))
                        .build()),e -> Boutique.INVENTORY.open(player)));
        contents.set(0,4,ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON)
                .setDisplayName(game.translate("space-conquest.gui.upgrade.name"))
                .build()),e -> Beacon.INVENTORY.open(player)));
        contents.set(0,6,ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON)
                .setDisplayName(game.translate("space-conquest.gui.special-artefact.title"))
                .build()),e -> SpecialArtefact.INVENTORY.open(player)));
        contents.set(0,8,
                ClickableItem.of((new ItemBuilder(Material.ACACIA_BUTTON)
                        .setDisplayName(game.translate("space-conquest.gui.beacon.name"))
                        .build()),e -> Beacon.INVENTORY.open(player)));
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();
        game.getTeam(player).ifPresent(team -> {
            Pagination pagination = contents.pagination();


            if(team.getUpgrade().getTower()==0){
                ItemBuilder mode = new ItemBuilder(Material.WOODEN_PICKAXE);
                mode.setDisplayName(game.translate("space-conquest.gui.beacon.modes.mode_1",
                        team.getUpgrade().getTower()+1,
                        team.getResource().getOrDefault(Material.IRON_BLOCK,0)));

                contents.set(1,4, ClickableItem.of((mode.build()), e -> {
                    if(team.getResource().containsKey(Material.IRON_BLOCK)){
                        int iron =team.getResource().get(Material.IRON_BLOCK);
                        if(iron>=10000){
                            team.getResource().put(Material.IRON_BLOCK,iron-10000);
                            team.getUpgrade().setTower(team.getUpgrade().getTower()+1);
                        }
                    }
                }));
            }
            else if(team.getUpgrade().getTower()==1){
                ItemBuilder mode = new ItemBuilder(Material.GOLDEN_PICKAXE);
                mode.setDisplayName(game.translate("space-conquest.gui.beacon.modes.mode_2",
                        team.getUpgrade().getTower()+1,
                        team.getResource().getOrDefault(Material.GOLD_BLOCK,0)));

                contents.set(1,4, ClickableItem.of((mode.build()), e -> {
                    if(team.getResource().containsKey(Material.GOLD_BLOCK)){
                        int iron =team.getResource().get(Material.GOLD_BLOCK);
                        if(iron>=10000){
                            team.getResource().put(Material.GOLD_BLOCK,iron-10000);
                            team.getUpgrade().setTower(team.getUpgrade().getTower()+1);
                        }
                    }
                }));
            }
            else if(team.getUpgrade().getTower()==2){
                ItemBuilder mode = new ItemBuilder(Material.DIAMOND_PICKAXE);
                mode.setDisplayName(game.translate("space-conquest.gui.beacon.modes.mode_3",
                        team.getUpgrade().getTower()+1,
                        team.getResource().getOrDefault(Material.DIAMOND_BLOCK,0)));

                contents.set(1,4, ClickableItem.of((mode.build()), e -> {
                    if(team.getResource().containsKey(Material.DIAMOND_BLOCK)){
                        int iron =team.getResource().get(Material.DIAMOND_BLOCK);
                        if(iron>=10000){
                            team.getResource().put(Material.DIAMOND_BLOCK,iron-10000);
                            team.getUpgrade().setTower(team.getUpgrade().getTower()+1);
                        }
                    }
                }));
            }

            List<TowerMode> modes = Arrays.stream(TowerMode.values())
                    .filter(towerMode -> towerMode.getLevel()<=team.getUpgrade().getTower())
                    .collect(Collectors.toList());

            Area area = game.getAreas()
                    .stream()
                    .filter(area1 -> !area1.isBase())
                    .filter(area1 -> BoundingBox.of(area1.getMiddle(),5,5,5).contains(player.getLocation().toVector()))
                    .findFirst()
                    .orElse(null);

            if(area!=null){
                contents.set(1,2, ClickableItem.of((new ItemBuilder(Material.BEACON).setDisplayName(game.translate(area.getMode().getKey()))
                        .build()), e -> {
                    int index = modes.indexOf(area.getMode());
                    if(e.isLeftClick()){
                        area.setMode(modes.get((index+1)%modes.size()));
                    }
                    else{
                        area.setMode(modes.get((index+modes.size()-1)%modes.size()));
                    }
                }));
                return;
            }

            List<ClickableItem> items = game.getAreas().stream()
                    .filter(area1 -> !area1.isBase())
                    .filter(area1 -> team.equals(area1.getOwnerTeam()))
                    .map(area1 -> ClickableItem.of((new ItemBuilder(Material.BEACON)
                            .setDisplayName(game.translate(area1.getMode().getKey()))
                            .build()),e -> {
                        int index = modes.indexOf(area1.getMode());
                        if(e.isLeftClick()){
                            area1.setMode(modes.get((index+1)%modes.size()));
                        }
                        else{
                            area1.setMode(modes.get((index+modes.size()-1)%modes.size()));
                        }
                    }))
                    .collect(Collectors.toList());

            if (items.size() > 27) {
                pagination.setItems(items.toArray(new ClickableItem[0]));
                pagination.setItemsPerPage(18);
                pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));
                int page = pagination.getPage() + 1;
                contents.set(4, 0, null);
                contents.set(4, 1, null);
                contents.set(4, 3, null);
                contents.set(4, 5, null);
                contents.set(4, 7, null);
                contents.set(4, 8, null);
                contents.set(4, 2, ClickableItem.of(new ItemBuilder(Material.ARROW)
                                .setDisplayName(game.translate("space-conquest.gui.beacon.modes.previous",
                                        page, pagination.isFirst() ? page : page - 1)).build(),
                        e -> INVENTORY.open(player, pagination.previous().getPage())));
                contents.set(4, 6, ClickableItem.of(new ItemBuilder(Material.ARROW)
                                .setDisplayName(game.translate("space-conquest.gui.beacon.modes.next",
                                        page, pagination.isLast() ? page : page + 1)).build(),
                        e -> INVENTORY.open(player, pagination.next().getPage())));
                contents.set(4, 4, ClickableItem.empty(new ItemBuilder(Material.ACACIA_SIGN)
                        .setDisplayName(game.translate("space-conquest.gui.beacon.modes.current",
                                page, items.size() / 27 + 1)).build()));
            }
            else {
                int i = 9;
                for (ClickableItem clickableItem : items) {
                    contents.set(i / 9 + 1, i % 9, clickableItem);
                    i++;
                }
                for (int k = i; k < 27; k++) {
                    contents.set(k / 9 + 1, k % 9, null);
                }
            }
        });

    }
}
