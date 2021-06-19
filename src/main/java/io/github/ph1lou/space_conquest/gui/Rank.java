package io.github.ph1lou.space_conquest.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.space_conquest.Main;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.game.Team;
import io.github.ph1lou.space_conquest.utils.ItemBuilder;
import io.github.ph1lou.space_conquest.utils.TexturedItem;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Rank implements InventoryProvider {



    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("rank")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new Rank())
            .size(3, 9)
            .title(JavaPlugin.getPlugin(Main.class).getLangManager().getTranslation("space-conquest.gui.rank.rank"))
            .closeable(true)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {
    }

    @Override
    public void update(Player player, InventoryContents contents) {

        GameManager game = JavaPlugin.getPlugin(Main.class).getCurrentGame();

        int j=0;

        for(Team team:game.getTeams()){

            ItemBuilder banner = new ItemBuilder(team.getColorTeam().getBanner());
            banner.setDisplayName(team.getColorTeam().getChatColor()+team.getName());
            banner.setLore(game.translate("space-conquest.gui.rank.message",team.getResource().getOrDefault(TexturedItem.CRYING_OBSIDIAN_RESSOURCE,0),game.getObjective()));

            contents.set(j/9,j%9,ClickableItem.empty(banner.build()));
            j++;
        }

        for(int k=j;k<27;k++){
            contents.set(j/9,j%9,null);
            j++;
        }

    }
}
