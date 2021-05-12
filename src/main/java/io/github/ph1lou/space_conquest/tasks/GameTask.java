package io.github.ph1lou.space_conquest.tasks;

import io.github.ph1lou.space_conquest.enums.State;
import io.github.ph1lou.space_conquest.events.WinEvent;
import io.github.ph1lou.space_conquest.game.Area;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.game.Team;
import net.minecraft.server.v1_16_R3.Tuple;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class GameTask extends BukkitRunnable {

    private final GameManager game;
    private int timer=0;

    public GameTask(GameManager game){
        this.game=game;
    }


    private void progressMiddle(Player player, Area area, Team team) {

        player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK, 10, 10);
        this.mineRessources(area, team);
        team.getBossBar().setVisible(true);
        team.getBossBar().setProgress(team.getResource().getOrDefault(Material.CRYING_OBSIDIAN, 0) / (float) game.getObjective());
        Bukkit.getOnlinePlayers().forEach(player1 -> team.getBossBar().addPlayer(player1));
        Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(), () -> {

            if (area.getRatioPlayerOn(team) < 1/2f) {
                team.getBossBar().setVisible(false);
            }
        }, 6);
        if (team.getResource().getOrDefault(Material.CRYING_OBSIDIAN, 0) >= game.getObjective()) {

            game.getScoreBoard().updateScoreBoard();

            Bukkit.getOnlinePlayers().forEach(player1 -> {
                player1.playSound(player1.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 10, 10);
                player1.sendTitle("Victoire", String.format("Équipe %s", team.getName()), 20, 20, 20);
            });
            Bukkit.broadcastMessage("[Space §bConquest] Victoire " + String.format("Équipe §b%s", team.getName()));
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
        .forEach(areaTeamTuple -> this.mineRessources(areaTeamTuple.a(),areaTeamTuple.b()));


        playerMoved((p) -> {},(player, area, team) -> {
            if (area.getRatioPlayerOn(team) > 1/2f) {

                if (Material.CRYING_OBSIDIAN.equals(area.getGeneratorType())) {
                    this.progressMiddle(player,area,team);
                }
                else {
                    area.progressCapture(team, team1 -> game.getAreas().stream()
                            .filter(area1 -> !area1.isBase())
                            .filter(area1 -> team1.equals(area1.getIsCapture()))
                            .forEach(area1 -> {
                                int areaSize = 1;
                                int temp1 = 0;
                                while (temp1 != areaSize) {
                                    areaSize = area1.getControlSize();
                                    area1.removeControl();
                                    temp1 = area1.getControlSize();
                                }
                                area1.setOwnerTeam(null);
                                area1.setIsCapture(null);
                            }));
                }
            }
        });

    }

    /**
     * Génère les particules du beacon vers la base de la team et les ajoutes dans leur ressources de team
     * @param area l'aire de départ
     * @param team la team a qui appartient à la zone
     */
    private void mineRessources(Area area, Team team) {
        team.getResource().put(area.getGeneratorType(), team.getResource().getOrDefault(area.getGeneratorType(),0)+area.getBlocks().size()/18);
        Location location = area.getMiddle().clone();
        location.setY(location.getBlockY()+3.5);
        location.setX(location.getBlockX()+0.5);
        location.setZ(location.getBlockZ()+0.5);
        Location base = team.getBase().getMiddle().clone();
        base.setY(base.getBlockY()-0.5);
        base.setX(base.getBlockX()+0.5);
        base.setZ(base.getBlockZ()+0.5);
        Vector vector = base.toVector().subtract(location.toVector()).normalize();
        game.getWorld().spawnParticle(Particle.FLAME,location,0,vector.getX(),vector.getY(),vector.getZ(),0.5);
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
