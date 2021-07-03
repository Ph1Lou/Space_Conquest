package io.github.ph1lou.space_conquest.commands;

import io.github.ph1lou.space_conquest.Main;
import io.github.ph1lou.space_conquest.enums.State;
import io.github.ph1lou.space_conquest.game.GameManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class LoadData implements TabExecutor {

    final Main main;

    public LoadData(Main main){
        this.main=main;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        GameManager game = main.getCurrentGame();

        if(!game.isState(State.LOBBY)) return true;

        if(!commandSender.hasPermission("space-conquest")){
            return true;
        }

        main.loadDatas();

        commandSender.sendMessage("[§bSpace Conquest§f] Config Reload");

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length==0) return Collections.singletonList("load");
        if(args.length==1){
            if("load".contains(args[0])) return Collections.singletonList("load");
        }
        return null;
    }
}
