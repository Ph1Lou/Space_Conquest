package io.github.ph1lou.space_conquest.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.space_conquest.Main;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.game.Team;
import io.github.ph1lou.space_conquest.utils.ItemBuilder;
import io.github.ph1lou.space_conquest.utils.Ressource;
import io.github.ph1lou.space_conquest.utils.TexturedItem;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.atomic.AtomicInteger;

public class Ressources implements InventoryProvider {

    private final Team team;

    public Ressources(Team ownerTeam) {
        this.team = ownerTeam;
    }

    public static SmartInventory getInventory() {
        return Ressources.getInventory(null);
    }

    public static SmartInventory getInventory(Team ownerTeam) {
        
        return SmartInventory.builder()
                .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
                .id("guishop")
                .provider(new Ressources(ownerTeam))
                .size(3, 9)
                .title(JavaPlugin.getPlugin(Main.class).getLangManager().getTranslation("space-conquest.gui.ressources.title"))
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

        if(team == null){
            return;
        }

        AtomicInteger i= new AtomicInteger(1);
        for(TexturedItem item:team.getResource().keySet()){

            Ressource.getRessource(item).ifPresent(ressource -> {
                ItemBuilder itemStack = item.getItemBuilder()
                        .setDisplayName(game.translate(ressource.getName(),
                                String.valueOf(team.getResource().get(item))));
                contents.set(1, i.get(),ClickableItem.empty(itemStack.build()));
                i.getAndIncrement();
            });

        }

    }
}
