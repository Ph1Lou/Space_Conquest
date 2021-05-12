package io.github.ph1lou.space_conquest.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.space_conquest.Main;
import io.github.ph1lou.space_conquest.enums.ColorTeam;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.game.Team;
import io.github.ph1lou.space_conquest.utils.ItemBuilder;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ColorChoice implements InventoryProvider {



    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("ColorChoice")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new ColorChoice())
            .size(3, 9)
            .title(JavaPlugin.getPlugin(Main.class).getLangManager().getTranslation("space-conquest.gui.team-color"))
            .closeable(true)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
        }

    @Override
    public void update(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();

        Team team = game.getTeam(player);

        if(team==null) return;

        int j=0;

        List<ColorTeam> colorTeams = new ArrayList<>(Arrays.asList(ColorTeam.values()));
        if(game.isSingleColor()){
            for(Team team1:game.getTeams()){
                colorTeams.remove(team1.getColorTeam());
            }
        }

        for(ColorTeam colorTeam:colorTeams){

            ItemBuilder banner = new ItemBuilder(colorTeam.getConstructionMaterial());

            banner.setDisplayName(game.translate(colorTeam.getName()));

            contents.set(j/9,j%9,ClickableItem.of((banner.build()),e -> {
                        team.setColorTeam(colorTeam);
                        ConfigMenu.INVENTORY.open(player);
            } ));
            j++;
        }

        for(int k=j;k<18;k++){
            contents.set(j/9,j%9,null);
        }
    }
}
