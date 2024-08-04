package io.github.ph1lou.space_conquest.tasks;

import io.github.ph1lou.space_conquest.enums.State;
import io.github.ph1lou.space_conquest.events.WinEvent;
import io.github.ph1lou.space_conquest.game.Area;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.game.Team;
import io.github.ph1lou.space_conquest.utils.TexturedItem;
import net.minecraft.util.Tuple;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Optional;

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
        team.getBossBar().setProgress(team.getResource().getOrDefault(TexturedItem.CRYING_OBSIDIAN_RESSOURCE,
                0) / (float) game.getObjective());
        Bukkit.getOnlinePlayers().forEach(player1 -> team.getBossBar().addPlayer(player1));
        Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(), () -> {
            if (!team.equals(area.getOwnerTeam())) {
                team.getBossBar().setVisible(false);
            }
            else if (!area.isInSuperiority(team)) {
                team.getBossBar().setVisible(false);
                area.setOwnerTeam(null);
            }
        }, 6);

        if (team.getResource().getOrDefault(TexturedItem.CRYING_OBSIDIAN_RESSOURCE, 0) >= game.getObjective()) {

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

            game.sendScore(team);

            game.restart();
            Bukkit.getPluginManager().callEvent(new WinEvent(team));
        }
    }



    @Override
    public void run() {

        this.timer++;
        if(this.game.isState(State.END)){
            cancel();
            return;
        }

        this.game.getScoreBoard().updateScoreBoard();

        if(this.timer%4==0){
            this.game.setTimer(this.game.getTimer()+1);
        }

        this.game.getAreas()
                .stream()
                .filter(area -> !area.isBase() && !area.isMiddle())
                .map(area -> new Tuple<>(area,area.getOwnerTeam()))
                .filter(areaTeamTuple -> areaTeamTuple.b()!=null)
        .forEach(areaTeamTuple -> areaTeamTuple.a().mineRessources(areaTeamTuple.b()));


        this.playerMoved((player, area, team) -> {
            if (area.isMiddle()) {
                this.progressMiddle(player,area,team);
            }
        },(player, area, team) -> {
            if (area.isInSuperiority(team)) {

                if (area.isMiddle()) {
                    area.setOwnerTeam(team);
                }
                else if(area.isBase() && area.getOwnerTeam()!=null && !area.getOwnerTeam().equals(team)){
                    Bukkit.getOnlinePlayers()
                            .stream()
                            .filter(player1 -> {
                                Optional<Team> team1 = this.game.getTeam(player1);
                                return team1.isPresent() && team1.get().equals(area.getOwnerTeam());
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

    public void playerMoved(TriConsumer<Player,Area, Team> onSelfArea, TriConsumer<Player,Area, Team> onForeignArea) {

        Bukkit.getOnlinePlayers().stream()
                .filter(player -> {
                    Material face = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
                    return face.toString().contains("STAINED_GLASS") || face.equals(Material.GLOWSTONE);
                })
                .map(player -> new Tuple<>(game.getTeam(player),player))
                .filter(tuple -> tuple.a().isPresent())
                .map(optionalTuple -> new Tuple<>(optionalTuple.a().get(),optionalTuple.b()))
                .forEach(tuple -> game.getAreas()
                        .stream()
                        .filter(area -> area.isOnArea(tuple.b()))
                        .findFirst().ifPresent(area -> {
                            if(tuple.a().equals(area.getOwnerTeam())){
                                onSelfArea.accept(tuple.b(),area,tuple.a());
                            }
                            else {
                                onForeignArea.accept(tuple.b(),area,tuple.a());
                            }
                        }));
    }

}
