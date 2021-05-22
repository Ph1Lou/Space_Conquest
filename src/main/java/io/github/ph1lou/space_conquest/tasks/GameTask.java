package io.github.ph1lou.space_conquest.tasks;

import io.github.ph1lou.space_conquest.enums.State;
import io.github.ph1lou.space_conquest.events.WinEvent;
import io.github.ph1lou.space_conquest.game.Area;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.game.Team;
import net.minecraft.server.v1_16_R3.Tuple;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

public class GameTask extends BukkitRunnable {

    private final GameManager game;
    private int timer=0;

    public GameTask(GameManager game){
        this.game=game;
    }

    private void progressMiddle(Player player, Area area, Team team) {

        player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 10, 10);
        area.mineRessources(team);
        team.getBossBar().setVisible(true);
        team.getBossBar().setProgress(team.getResource().getOrDefault(Material.CRYING_OBSIDIAN,
                0) / (float) game.getObjective());
        Bukkit.getOnlinePlayers().forEach(player1 -> team.getBossBar().addPlayer(player1));
        Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(), () -> {

            if (area.getRatioPlayerOn(team) < 1/2f) {
                team.getBossBar().setVisible(false);
            }
        }, 20);

        if (team.getResource().getOrDefault(Material.CRYING_OBSIDIAN, 0) >= game.getObjective()) {

            game.getScoreBoard().updateScoreBoard();

            Bukkit.getOnlinePlayers().forEach(player1 -> {
                player1.playSound(player1.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 10, 10);
                player1.sendTitle(game.translate("space-conquest.game.victory.title"),
                        game.translate("space-conquest.game.victory.subtitle",
                                team.getName()), 20, 20, 20);
            });
            Bukkit.broadcastMessage(game.translate("space-conquest.game.victory.message",
                    game.translate("space-conquest.game.victory.team",
                            team.getName())));
            game.restart();
            Bukkit.getPluginManager().callEvent(new WinEvent(team));
        }
    }



    @Override
    public void run() {

        timer++;
        if(game.isState(State.END)){
            cancel();
            return;
        }

        game.getScoreBoard().updateScoreBoard();

        if(timer%4==0){
            game.setTimer(game.getTimer()+1);
        }

        game.getAreas()
                .stream()
                .filter(area -> !area.isBase())
                .map(area -> new Tuple<>(area,area.getOwnerTeam()))
                .filter(areaTeamTuple -> areaTeamTuple.b()!=null)
        .forEach(areaTeamTuple -> areaTeamTuple.a().mineRessources(areaTeamTuple.b()));


        playerMoved((p) -> {},(player, area, team) -> {
            if (area.getRatioPlayerOn(team) > 1/2f) {

                if (area.isMiddle()) {
                    this.progressMiddle(player,area,team);
                }
                if(area.isBase() && area.getOwnerTeam()!=null && !area.getOwnerTeam().equals(team)){
                    Bukkit.getOnlinePlayers()
                            .stream()
                            .filter(player1 -> {
                                Team team1 = game.getTeam(player1);
                                return team1 !=null && team1.equals(area.getOwnerTeam());
                            })
                            .forEach(player1 -> player1.teleport(area.getOwnerTeam().getSpawn()));
                    player.teleport(team.getSpawn());
                }
                else {
                    area.progressCapture(team);
                }
            }
        });

    }

    public void playerMoved(Consumer<Player> onSelfArea, TriConsumer<Player,Area, Team> onForeignArea) {

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> {
                    Material face = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
                    return face.toString().contains("STAINED_GLASS") || face.equals(Material.GLOWSTONE);
                })
                .map(player -> new Tuple<>(game.getTeam(player),player))
                .filter(tuple -> tuple.a()!=null)
                .forEach(tuple -> game.getAreas()
                        .stream()
                        .filter(area -> area.isOnArea(tuple.b()))
                        .findFirst().ifPresent(area -> {
                            if(tuple.a().equals(area.getOwnerTeam())){
                                onSelfArea.accept(tuple.b());
                            }
                            else {
                                onForeignArea.accept(tuple.b(),area,tuple.a());
                            }
                        }));
    }

}
