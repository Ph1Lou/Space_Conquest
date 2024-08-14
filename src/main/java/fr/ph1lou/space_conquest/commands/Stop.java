package fr.ph1lou.space_conquest.commands;

import fr.ph1lou.space_conquest.game.GameManager;
import fr.ph1lou.space_conquest.Main;
import fr.ph1lou.space_conquest.enums.State;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class Stop implements TabExecutor {

    final Main main;

    public Stop(Main main){
        this.main=main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        GameManager game = main.getCurrentGame();

        if(!game.isState(State.GAME)) return true;

        if(!commandSender.hasPermission("space-conquest")){
            return true;
        }

        game.restart();

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length==0) return Collections.singletonList("stop");
        if(args.length==1){
            if("stop".contains(args[0])) return Collections.singletonList("stop");
        }
        return null;
    }
}
