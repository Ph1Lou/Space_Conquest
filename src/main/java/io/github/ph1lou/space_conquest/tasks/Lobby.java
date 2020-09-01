package io.github.ph1lou.space_conquest.tasks;

import io.github.ph1lou.space_conquest.enums.State;
import io.github.ph1lou.space_conquest.game.GameManager;
import org.bukkit.scheduler.BukkitRunnable;

public class Lobby extends BukkitRunnable {

    final GameManager game;

    public Lobby(GameManager game){
        this.game=game;
    }

    @Override
    public void run() {

        if(game.isState(State.GAME)){
            GameTask start = new GameTask(game);
            start.runTaskTimer(game.getMain(), 0, 5);
            cancel();
            return;
        }

        game.getScoreBoard().updateScoreBoard();

    }
}
