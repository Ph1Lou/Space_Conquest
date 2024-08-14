package fr.ph1lou.space_conquest.commands;

import fr.ph1lou.space_conquest.Main;
import fr.ph1lou.space_conquest.game.GameManager;
import fr.ph1lou.space_conquest.game.Team;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class TeamChat implements CommandExecutor {

    private final Main main;

    public TeamChat(Main main) {
        this.main=main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        GameManager game = main.getCurrentGame();

        if(!(sender instanceof Player player)) return true;

        Team team = game.getTeam(player).orElse(null);

        if(team==null) {
            player.sendMessage(game.translate("space-conquest.team.no-team"));
            return true;
        }

        StringBuilder sb = new StringBuilder();
        sb.append(team.getColorTeam().getChatColor()).append(game.translate("space-conquest.team.prefix-chat"));
        if(team.getFounder().equals(player.getUniqueId())){
            sb.append("⭐ ");
        }
        sb.append(player.getName()).append(" : ");
        for(String string:args){
            sb.append(string).append(" ");
        }

        for(UUID uuid:team.getMembers()){
            Player player1 = Bukkit.getPlayer(uuid);
            if(player1!=null){
                player1.sendMessage(sb.toString());
            }
        }

        return true;
    }
}
