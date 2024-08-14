package fr.ph1lou.space_conquest.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.space_conquest.Main;
import fr.ph1lou.space_conquest.game.GameManager;
import fr.ph1lou.space_conquest.utils.ItemBuilder;
import fr.ph1lou.space_conquest.utils.TexturedItem;
import fr.ph1lou.space_conquest.enums.TowerMode;
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
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();

        contents.set(0,0,
                ClickableItem.of((TexturedItem.BLUE_BUTTON.getItemBuilder().setDisplayName(game.translate("space-conquest.gui.ressources.title"))
                        .build()),e -> Ressources.getInventory().open(player)));
        contents.set(0,2,
                ClickableItem.of((TexturedItem.RED_BUTTON.getItemBuilder().setDisplayName(game.translate("space-conquest.gui.boutique.title"))
                        .build()),e -> Boutique.INVENTORY.open(player)));
        contents.set(0,4,
                ClickableItem.of((TexturedItem.YELLOW_BUTTON.getItemBuilder().setDisplayName(game.translate("space-conquest.gui.upgrade.name"))
                        .build()),e -> Upgrade.getInventory().open(player)));
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

            if(team.getUpgrade().getTower()==0){
                ItemBuilder mode = TexturedItem.TOWER_LEVEL_1.getItemBuilder();
                mode.setDisplayName(game.translate("space-conquest.gui.beacon.modes.mode_1",
                        team.getUpgrade().getTower()+1,
                        team.getResource().getOrDefault(TexturedItem.IRON_RESSOURCE,0)))
                        .setLore(game.translate("space-conquest.gui.beacon.modes.unlock_1"));

                contents.set(1,4, ClickableItem.of((mode.build()), e -> team.spend(10000,TexturedItem.IRON_RESSOURCE,
                        () ->  team.getUpgrade().setTower(team.getUpgrade().getTower()+1))));
            }
            else if(team.getUpgrade().getTower()==1){
                ItemBuilder mode = TexturedItem.TOWER_LEVEL_2.getItemBuilder();
                mode.setDisplayName(game.translate("space-conquest.gui.beacon.modes.mode_2",
                        team.getUpgrade().getTower()+1,
                        team.getResource().getOrDefault(TexturedItem.GOLD_RESSOURCE,0)))
                        .setLore(game.translate("space-conquest.gui.beacon.modes.unlock_2"));

                contents.set(1,4, ClickableItem.of((mode.build()), e -> team.spend(10000,TexturedItem.GOLD_RESSOURCE,
                        () ->  team.getUpgrade().setTower(team.getUpgrade().getTower()+1))));
            }
            else if(team.getUpgrade().getTower()==2){
                ItemBuilder mode = TexturedItem.TOWER_LEVEL_3.getItemBuilder();
                mode.setDisplayName(game.translate("space-conquest.gui.beacon.modes.mode_3",
                        team.getUpgrade().getTower()+1,
                        team.getResource().getOrDefault(TexturedItem.DIAMOND_RESSOURCE,0)))
                        .setLore(game.translate("space-conquest.gui.beacon.modes.unlock_3"));

                contents.set(1,4, ClickableItem.of((mode.build()), e -> team.spend(10000,TexturedItem.DIAMOND_RESSOURCE,
                        () ->  team.getUpgrade().setTower(team.getUpgrade().getTower()+1))));
            }

            List<TowerMode> modes = Arrays.stream(TowerMode.values())
                    .filter(towerMode -> towerMode.getLevel()<=team.getUpgrade().getTower())
                    .collect(Collectors.toList());

            game.getAreas()
                    .stream()
                    .filter(area1 -> !area1.isBase())
                    .filter(area1 -> BoundingBox.of(area1.getMiddle(), 5, 5, 5).contains(player.getLocation().toVector()))
                    .findFirst()
                    .ifPresent(area -> contents.set(1, 2, ClickableItem.of((new ItemBuilder(Material.BEACON)
                    .setDisplayName(game.translate("space-conquest.gui.beacon.modes.mode", game.translate(area.getMode().getKey())))
                    .setLore(game.translate("space-conquest.gui.beacon.modes.click"))
                    .build()), e -> {
                int index = modes.indexOf(area.getMode());
                if (e.isLeftClick()) {
                    area.setMode(modes.get((index + 1) % modes.size()));
                } else {
                    area.setMode(modes.get((index + modes.size() - 1) % modes.size()));
                }
            })));

            //Pagination pagination = contents.pagination();
           //List<ClickableItem> items = game.getAreas().stream()
           //        .filter(area1 -> !area1.isBase())
           //        .filter(area1 -> team.equals(area1.getOwnerTeam()))
           //        .map(area1 -> ClickableItem.of((new ItemBuilder(Material.BEACON)
           //                .setDisplayName(game.translate("space-conquest.gui.beacon.modes.mode", game.translate(area1.getMode().getKey())))
           //                .setLore(game.translate("space-conquest.gui.beacon.modes.click"))
           //                .build()),e -> {
           //            int index = modes.indexOf(area1.getMode());
           //            if(e.isLeftClick()){
           //                area1.setMode(modes.get((index+1)%modes.size()));
           //            }
           //            else{
           //                area1.setMode(modes.get((index+modes.size()-1)%modes.size()));
           //            }
            //        }))
            //        .collect(Collectors.toList());
//
            //if (items.size() > 27) {
            //    pagination.setItems(items.toArray(new ClickableItem[0]));
            //    pagination.setItemsPerPage(18);
            //    pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 0));
            //    int page = pagination.getPage() + 1;
            //    contents.set(4, 0, null);
            //    contents.set(4, 1, null);
            //    contents.set(4, 3, null);
            //    contents.set(4, 5, null);
            //    contents.set(4, 7, null);
            //    contents.set(4, 8, null);
            //    contents.set(4, 2, ClickableItem.of(new ItemBuilder(Material.ARROW)
            //                    .setDisplayName(game.translate("space-conquest.gui.beacon.modes.previous",
            //                            page, pagination.isFirst() ? page : page - 1)).build(),
            //            e -> INVENTORY.open(player, pagination.previous().getPage())));
            //    contents.set(4, 6, ClickableItem.of(new ItemBuilder(Material.ARROW)
            //                    .setDisplayName(game.translate("space-conquest.gui.beacon.modes.next",
            //                            page, pagination.isLast() ? page : page + 1)).build(),
            //            e -> INVENTORY.open(player, pagination.next().getPage())));
            //    contents.set(4, 4, ClickableItem.empty(new ItemBuilder(Material.ACACIA_SIGN)
            //            .setDisplayName(game.translate("space-conquest.gui.beacon.modes.current",
            //                    page, items.size() / 27 + 1)).build()));
            //}
            //else {
            //    int i = 9;
            //    for (ClickableItem clickableItem : items) {
            //        contents.set(i / 9 + 1, i % 9, clickableItem);
            //        i++;
            //    }
            //    for (int k = i; k < 27; k++) {
            //        contents.set(k / 9 + 1, k % 9, null);
            //    }
            //}
        });

    }
}
