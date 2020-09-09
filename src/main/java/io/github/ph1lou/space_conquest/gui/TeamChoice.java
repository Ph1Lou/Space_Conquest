package io.github.ph1lou.space_conquest.gui;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import io.github.ph1lou.space_conquest.Main;
import io.github.ph1lou.space_conquest.enums.State;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.game.Team;
import io.github.ph1lou.space_conquest.utils.ItemBuilder;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TeamChoice implements InventoryProvider {



    public static final SmartInventory INVENTORY = SmartInventory.builder()
            .id("TeamChoice")
            .manager(JavaPlugin.getPlugin(Main.class).getInvManager())
            .provider(new TeamChoice())
            .size(4, 9)
            .title("Team")
            .closeable(true)
            .build();

    @Override
    public void init(Player player, InventoryContents contents) {

        Main main = JavaPlugin.getPlugin(Main.class);
        GameManager game = main.getCurrentGame();

        ItemBuilder itemStack = new ItemBuilder(Material.CREEPER_HEAD);
        itemStack.setDisplayName("Créez une Team");

        contents.set(0,0,ClickableItem.of((itemStack.build()),e -> new AnvilGUI.Builder()
                        .onComplete((player2, text) -> {

                            if(game.isState(State.LOBBY)){
                                if(game.getTeams().size()<game.getTeamNumber()){
                                    text=text.substring(0,Math.min(text.length(),16));
                                    if(text.length()>0 && text.charAt(0)==' '){
                                        text=text.replaceFirst(" ","");
                                    }
                                    Team team = new Team(game,text);
                                    team.addPlayer(player2);
                                    team.setFounder(player2.getUniqueId());
                                    game.getTeams().add(team);
                                }
                                else player2.sendMessage("Le nombre d'équipes maximums a été crée");
                            }
                            else player2.sendMessage("La partie est déjà lancée");
                            return AnvilGUI.Response.close();
                        })
                        .title("Entrez le nom de votre équipe")
                        .item(new ItemStack(Material.GREEN_CONCRETE_POWDER))
                        .plugin(main)
                        .text(" ")
                        .onClose(player1 -> Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                            if(game.isState(State.LOBBY)){
                                TeamChoice.INVENTORY.open(player1);
                            }
                        }))
                        .open(player)
        ));
        contents.set(0,8,ClickableItem.of((new ItemBuilder(Material.BARRIER).setDisplayName("Quitter son équipe").build()),e ->{
            Team team = game.getTeam(player);
            if(team!=null){
                team.removePlayer(player);
            }
        }));


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
            banner.setDisplayName(team.getColorTeam().getChatColor()+"[Team "+team.getName()+"]");


            contents.set(j/9+1,j%9,ClickableItem.of((banner.build()),e -> team.addPlayer(player)));
            j++;
        }

        for(int k=j;k<18;k++){

            contents.set(j/9+1,j%9,null);
            j++;
        }


        if(player.isOp()){

            ItemBuilder teamSize = new ItemBuilder(Material.STONE_BUTTON);

            teamSize.setDisplayName(String.format("Taille des équipes §b%d",game.getTeamSize()));
            teamSize.setLore(Arrays.asList("§fClique-§bGauche§f >> +",
                    "§fClique-§bDroit§f >> -"));

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

            teamSize.setDisplayName(String.format("Taille Base Centrale §b%d",game.getCenterSize()));
            teamSize.setLore(Arrays.asList("§fClique-§bGauche§f >> +2",
                    "§fClique-§bDroit§f >> -2"));

            contents.set(3,8,ClickableItem.of((teamSize.build()),e -> {
                if(e.isRightClick()){
                    if(game.getCenterSize()>2){
                        game.setCenterSize(game.getCenterSize()-2);
                    }
                }
                else if(e.isLeftClick()){
                    game.setCenterSize(game.getCenterSize()+2);
                }

            }));

            ItemBuilder baseNumber = new ItemBuilder(Material.STONE_BUTTON);

            baseNumber.setDisplayName(String.format("Nombre de bases de fers §b%d",game.getZoneNumber()));
            baseNumber.setLore(Arrays.asList("§fClique-§bGauche§f >> +",
                    "§fClique-§bDroit§f >> -"));

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


            ItemBuilder playerMaxSize = new ItemBuilder(Material.STONE_BUTTON);

            playerMaxSize.setDisplayName(String.format("Joueurs Max §b%d",game.getPlayerMax()));
            playerMaxSize.setLore(Arrays.asList("§fClique-§bGauche§f >> +",
                    "§fClique-§bDroit§f >> -"));

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

            ItemBuilder teamNumber = new ItemBuilder(Material.STONE_BUTTON);

            teamNumber.setDisplayName(String.format("Nombre d'équipe Max §b%d",game.getTeamNumber()));
            teamNumber.setLore(Arrays.asList("§fClique-§bGauche§f >> +",
                    "§fClique-§bDroit§f >> -"));

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

            isSingle.setDisplayName("Couleur unique");
            isSingle.setLore(game.isSingleColor()?"§2Activé":"§4Désactivé");

            contents.set(0, 6, ClickableItem.of((isSingle.build()),e -> game.setSingleColor(!game.isSingleColor())));

            ItemBuilder objective = new ItemBuilder(Material.STONE_BUTTON);

            objective.setDisplayName(String.format("Crying Obsidian a récolté §b%d",game.getObjective()));
            objective.setLore(Arrays.asList("§fClique-§bGauche§f >> +",
                    "§fClique-§bDroit§f >> -"));

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

            gameName.setDisplayName("Changez le nom de la Partie");

            contents.set(0, 4, ClickableItem.of((gameName.build()), e -> new AnvilGUI.Builder()
                    .onComplete((player2, text) -> {
                        game.setGameName(text);
                        return AnvilGUI.Response.close();
                    })
                    .title("Changez le nom de la Partie")
                    .item(new ItemStack(Material.GREEN_CONCRETE_POWDER))
                    .plugin(main)
                    .text(game.getGameName())
                    .onClose(player1 -> Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                        if(game.isState(State.LOBBY)){
                            TeamChoice.INVENTORY.open(player1);
                        }
                    }))
                    .open(player)));

            contents.set(3, 4, ClickableItem.of((new ItemBuilder(Material.GREEN_STAINED_GLASS).setDisplayName("Lancez la Partie").build()), e -> Start.INVENTORY.open(player)));
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


        Team team = game.getTeam(player);

        if(team!=null && team.getFounder().equals(player.getUniqueId())){
            contents.set(3,6,ClickableItem.of((new ItemBuilder(Material.ACACIA_SIGN).
                    setDisplayName("Renommez votre équipe").
                    build()),e -> new AnvilGUI.Builder()
                            .onComplete((player2, text) -> {
                                if(text.length()>0 && text.charAt(0)==' '){
                                    text=text.replaceFirst(" ","");
                                }
                                team.setName(text.substring(0,Math.min(text.length(),16)));
                                return AnvilGUI.Response.close();
                            })
                            .title("Renommez votre équipe")
                            .item(new ItemStack(Material.GREEN_CONCRETE_POWDER))
                            .text(team.getName())
                            .plugin(main)
                            .onClose(player1 -> Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                                if(game.isState(State.LOBBY)){
                                    TeamChoice.INVENTORY.open(player1);
                                }
                            }))
                            .open(player)));

            contents.set(3,2,ClickableItem.of((new ItemBuilder(team.getColorTeam().getConstructionMaterial()).
                    setDisplayName("Changez votre couleur").
                    build()),e -> ColorChoice.INVENTORY.open(player)));

        }
        else {
            contents.set(3,6,null);
            contents.set(3,2,null);
        }

    }
}
