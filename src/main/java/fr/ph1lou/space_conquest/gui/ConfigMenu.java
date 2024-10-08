package fr.ph1lou.space_conquest.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.ph1lou.space_conquest.game.GameManager;
import fr.ph1lou.space_conquest.utils.ItemBuilder;
import fr.ph1lou.space_conquest.Main;
import fr.ph1lou.space_conquest.enums.State;
import fr.ph1lou.space_conquest.game.Team;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class ConfigMenu implements InventoryProvider {



    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("ConfigMenu")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new ConfigMenu())
            .size(4, 9)
            .title(JavaPlugin.getPlugin(Main.class).getLangManager().getTranslation("space-conquest.gui.config-menu.name"))
            .closeable(true)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = main.getCurrentGame();

        ItemBuilder itemStack = new ItemBuilder(Material.CREEPER_HEAD);
        itemStack.setDisplayName(game.translate("space-conquest.gui.config-menu.create"));

        contents.set(0,0,ClickableItem.of((itemStack.build()),e -> new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    if(slot == AnvilGUI.Slot.OUTPUT) {

                        if (game.isState(State.LOBBY)) {
                            if (game.getTeams().size() < game.getTeamNumber()) {

                                String text = stateSnapshot.getText();
                                text = text.substring(0, Math.min(text.length(), 16));
                                if (text.length() > 0 && text.charAt(0) == ' ') {
                                    text = text.replaceFirst(" ", "");
                                }
                                Team team = new Team(game, text);
                                team.addPlayer(stateSnapshot.getPlayer());
                                team.setFounder(stateSnapshot.getPlayer().getUniqueId());
                                game.registerTeam(team);

                                if (game.isTraining() && game.getTeams().size() >= game.getTeamAutoStart()) {
                                    game.initStart();
                                }
                            } else {
                                stateSnapshot.getPlayer().sendMessage(game.translate("space-conquest.gui.config-menu.max"));
                            }
                        } else {
                            stateSnapshot.getPlayer().sendMessage(game.translate("space-conquest.gui.config-menu.already-launch"));
                        }
                        return Arrays.asList(
                                AnvilGUI.ResponseAction.close(),
                                AnvilGUI.ResponseAction.run(() -> {
                                    if(game.isState(State.LOBBY)){
                                        ConfigMenu.INVENTORY.open(stateSnapshot.getPlayer());
                                    }
                                }));
                    }
                    return Collections.emptyList();
                })
                .title(game.translate("space-conquest.gui.config-menu.input-title"))
                .itemOutput(new ItemStack(Material.GREEN_CONCRETE_POWDER))
                .plugin(main)
                .text(" ")
                .open(player)
        ));
        contents.set(0,8,ClickableItem.of((new ItemBuilder(Material.BARRIER)
                        .setDisplayName(game.translate("space-conquest.gui.config-menu.leave"))
                        .build()),
                e -> game.getTeam(player).ifPresent(team -> team.removePlayer(player))));
        }

    @Override
    public void update(Player player, InventoryContents contents) {
        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = main.getCurrentGame();

        int j=0;

        for(Team team:game.getTeams()){

            ItemBuilder banner = new ItemBuilder(team.getColorTeam().getBanner());

            List<String> lore = new ArrayList<>();
            for(UUID uuid:team.getMembers()){
                Player player1=Bukkit.getPlayer(uuid);
                if(player1!=null){
                    if(team.getFounder().equals(uuid)){
                        lore.add(" ⭐ "+player1.getName());
                    }
                    else lore.add(player1.getName());
                }
            }
            banner.setLore(lore);
            banner.setDisplayName(team.getColorTeam().getChatColor()+team.getName());


            contents.set(j/9+1,j%9,ClickableItem.of((banner.build()),
                    e -> team.addPlayer(player)));
            j++;
        }

        for(int k=j;k<18;k++){

            contents.set(j/9+1,j%9,null);
            j++;
        }

        if(player.hasPermission("space-conquest") && !game.isTraining()){

            ItemBuilder teamSize = new ItemBuilder(Material.STONE_BUTTON);

            teamSize.setDisplayName(game.translate("space-conquest.gui.config-menu.size",game.getTeamSize()));
            teamSize.setLore(game.translateArray("space-conquest.gui.config-menu.lores-1"));

            ItemBuilder baseNumber = new ItemBuilder(Material.STONE_BUTTON);

            baseNumber.setDisplayName(game.translate("space-conquest.gui.config-menu.iron-area",game.getZoneNumber()));
            baseNumber.setLore(game.translateArray("space-conquest.gui.config-menu.lores-1"));

            contents.set(0,2,ClickableItem.of((baseNumber.build()),e -> {
                if(e.isRightClick()){
                    if(game.getZoneNumber()>1){
                        game.setZoneNumber(game.getZoneNumber()-1);
                    }
                }
                else if(e.isLeftClick()){
                    game.setZoneNumber(game.getZoneNumber()+1);
                }

            }));

            contents.set(0,1,ClickableItem.of((teamSize.build()),e -> {
                if(e.isRightClick()){
                    if(game.getTeamSize()>1){
                        game.setTeamSize(game.getTeamSize()-1);
                    }
                }
                else if(e.isLeftClick()){
                    game.setTeamSize(game.getTeamSize()+1);
                }

            }));



            ItemBuilder playerMaxSize = new ItemBuilder(Material.STONE_BUTTON);

            playerMaxSize.setDisplayName(game.translate("space-conquest.gui.config-menu.max-player",game.getPlayerMax()));
            playerMaxSize.setLore(game.translateArray("space-conquest.gui.config-menu.lores-1"));

            contents.set(0,3,ClickableItem.of((playerMaxSize.build()),e -> {
                if(e.isRightClick()){
                    if(game.getPlayerMax()>1){
                        game.setPlayerMax(game.getPlayerMax()-1);
                    }
                }
                else if(e.isLeftClick()){
                    game.setPlayerMax(game.getPlayerMax()+1);
                }

            }));

            ItemBuilder middleSize = new ItemBuilder(Material.STONE_BUTTON);

            middleSize.setDisplayName(game.translate("space-conquest.gui.config-menu.size-middle",
                    game.getCenterSize()));
            middleSize.setLore(game.translateArray("space-conquest.gui.config-menu.lores-2"));



            contents.set(3,8,ClickableItem.of((middleSize.build()),e -> {
                if(e.isRightClick()){
                    if(game.getCenterSize()>2){
                        game.setCenterSize(game.getCenterSize()-2);
                    }
                }
                else if(e.isLeftClick()){
                    game.setCenterSize(game.getCenterSize()+2);
                }

            }));

            ItemBuilder teamNumber = new ItemBuilder(Material.STONE_BUTTON);

            teamNumber.setDisplayName(game.translate("space-conquest.gui.config-menu.team-size",game.getTeamNumber()));
            teamNumber.setLore(game.translateArray("space-conquest.gui.config-menu.lores-1"));

            contents.set(0,5,ClickableItem.of((teamNumber.build()),e -> {
                if(e.isRightClick()){
                    if(game.getTeamNumber()>1){
                        game.setTeamNumber(game.getTeamNumber()-1);
                    }
                }
                else if(e.isLeftClick()){
                    game.setTeamNumber(game.getTeamNumber()+1);
                }

            }));

            ItemBuilder isSingle = new ItemBuilder(game.isSingleColor()?Material.GREEN_CARPET:Material.RED_CARPET);

            isSingle.setDisplayName(game.translate("space-conquest.gui.config-menu.single-color"));
            isSingle.setLore(game.isSingleColor()?"§2Activé":"§4Désactivé");

            contents.set(0, 6, ClickableItem.of((isSingle.build()),e -> game.setSingleColor(!game.isSingleColor())));

            ItemBuilder objective = new ItemBuilder(Material.STONE_BUTTON);

            objective.setDisplayName(game.translate("space-conquest.gui.config-menu.crying-obsidian-goal",game.getObjective()));
            objective.setLore(game.translateArray("space-conquest.gui.config-menu.lores-1"));

            contents.set(0,7,ClickableItem.of((objective.build()),e -> {
                if(e.isRightClick()){
                    if(game.getObjective()>100){
                        game.setObjective(game.getObjective()-100);
                    }
                }
                else if(e.isLeftClick()){
                    game.setObjective(game.getObjective()+100);
                }

            }));

            ItemBuilder gameName = new ItemBuilder(Material.ACACIA_SIGN);

            gameName.setDisplayName(game.translate("space-conquest.gui.config-menu.change-game-name"));

            contents.set(0, 4, ClickableItem.of((gameName.build()), e -> new AnvilGUI.Builder()
                    .onClick((slot, stateSnapshot) -> {
                        if(slot == AnvilGUI.Slot.OUTPUT){
                            game.setGameName(stateSnapshot.getText());
                            return Arrays.asList(
                                    AnvilGUI.ResponseAction.close(),
                                    AnvilGUI.ResponseAction.run(() -> {
                                        if(game.isState(State.LOBBY)){
                                            ConfigMenu.INVENTORY.open(stateSnapshot.getPlayer());
                                        }
                                    }));
                        }
                        return Collections.emptyList();
                    })
                    .title(game.translate("space-conquest.gui.config-menu.change-game-name"))
                    .itemOutput(new ItemStack(Material.GREEN_CONCRETE_POWDER))
                    .plugin(main)
                    .text(game.getGameName())
                    .open(player)));

            contents.set(3, 4, ClickableItem.of((new ItemBuilder(Material.GREEN_STAINED_GLASS).setDisplayName(game.translate("space-conquest.gui.start.name")).build()), e -> Start.INVENTORY.open(player)));
        }
        else {
            contents.set(0,1,null);
            contents.set(0,2,null);
            contents.set(0,3,null);
            contents.set(0,4, null);
            contents.set(0,5,null);
            contents.set(0,6, null);
            contents.set(0,7,null);
            contents.set(3,8,null);
            contents.set(3,4, null);
        }


        Team team = game.getTeam(player).orElse(null);

        if(team!=null && team.getFounder().equals(player.getUniqueId())){

            contents.set(3,6,ClickableItem.of((new ItemBuilder(Material.ACACIA_SIGN).
                    setDisplayName(game.translate("space-conquest.gui.config-menu.rename-team")).
                    build()),e -> new AnvilGUI.Builder()
                    .onClick((slot, stateSnapshot) -> {
                        if(slot == AnvilGUI.Slot.OUTPUT){
                            String text = stateSnapshot.getText();
                            if(text.length()>0 && text.charAt(0)==' '){
                                text=text.replaceFirst(" ","");
                            }
                            team.setName(text.substring(0,Math.min(text.length(),16)));
                            return Arrays.asList(
                                    AnvilGUI.ResponseAction.close(),
                                    AnvilGUI.ResponseAction.run(() -> {
                                        if(game.isState(State.LOBBY)){
                                            ConfigMenu.INVENTORY.open(stateSnapshot.getPlayer());
                                        }
                                    }));
                        }

                        return Collections.emptyList();
                    })
                    .title(game.translate("space-conquest.gui.config-menu.rename-team"))
                    .itemOutput(new ItemStack(Material.GREEN_CONCRETE_POWDER))
                    .text(team.getName())
                    .plugin(main)
                    .open(player)));


            contents.set(3,2,ClickableItem.of((new ItemBuilder(team.getColorTeam().getConstructionMaterial()).
                    setDisplayName(game.translate("space-conquest.gui.config-menu.change-color")).
                    build()),e -> ColorChoice.INVENTORY.open(player)));

        }
        else {
            contents.set(3,6,null);
            contents.set(3,2,null);
        }

    }
}
