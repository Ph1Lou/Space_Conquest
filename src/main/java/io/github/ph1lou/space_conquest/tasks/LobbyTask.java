package io.github.ph1lou.space_conquest.tasks;

import io.github.ph1lou.space_conquest.Main;
import io.github.ph1lou.space_conquest.enums.State;
import io.github.ph1lou.space_conquest.game.GameManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class LobbyTask extends BukkitRunnable {

    final GameManager game;

    public LobbyTask(GameManager game){
        this.game=game;
    }

    @Override
    public void run() {

        if(this.game.isState(State.PRE_START)){
            if(this.game.getCountDown()<=0){
                this.game.setState(State.GAME);
            }
            else{
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.setLevel(this.game.getCountDown());
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP,10,10);
                });
                this.game.setCountDown(this.game.getCountDown()-1);

            }
        }


        if(this.game.isState(State.GAME)){

            game.start();
            Bukkit.broadcastMessage(game.translate("space-conquest.gui.start.start"));

            Bukkit.getScheduler().scheduleSyncDelayedTask(JavaPlugin.getPlugin(Main.class),() -> {

                game.getTeams().forEach(team -> team.getMembers().forEach(uuid -> {
                    Player player1 = Bukkit.getPlayer(uuid);
                    if(player1!=null){
                        player1.closeInventory();
                        player1.teleport(team.getSpawn());
                        team.start(player1);
                    }
                }));

                Bukkit.getOnlinePlayers().forEach(player1 -> {
                    player1.closeInventory();
                    if(!player1.getWorld().equals(game.getWorld())){
                        player1.teleport(game.getWorld().getSpawnLocation());
                        player1.setGameMode(GameMode.SPECTATOR);
                    }
                });

                GameTask start = new GameTask(this.game);
                start.runTaskTimer(this.game.getMain(), 0, 5);

            },200);
            cancel();
            return;
        }

        game.getScoreBoard().updateScoreBoard();

    }
}
