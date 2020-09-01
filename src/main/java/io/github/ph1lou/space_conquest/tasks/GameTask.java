package io.github.ph1lou.space_conquest.tasks;

import io.github.ph1lou.space_conquest.enums.State;
import io.github.ph1lou.space_conquest.events.WinEvent;
import io.github.ph1lou.space_conquest.game.Area;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.game.Team;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class GameTask extends BukkitRunnable {

    final GameManager game;
    int timer=0;
    public GameTask(GameManager game){
        this.game=game;
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

        for(Area area:game.getAreas()){
            Team team = area.getOwnerTeam();

            if(area.isBase()){
                for(Player player:area.getPlayerOn()){

                    Team teamPlayer = game.getTeam(player);

                    if(teamPlayer!=null && !teamPlayer.equals(area.getOwnerTeam())){
                        for(Area area1:game.getAreas()){
                            if(!area1.isBase()){
                                if(area1.getIsCapture()!=null && area1.getIsCapture().equals(team)){
                                    int areaSize = 1;
                                    int temp = 0;
                                    while(temp!=areaSize){
                                        areaSize = area1.getControlSize();
                                        area1.resetControl();
                                        temp=area1.getControlSize();
                                    }
                                    area1.setOwnerTeam(null);
                                    area1.setIsCapture(null);
                                }
                            }
                        }
                        player.teleport(teamPlayer.getSpawn());
                    }
                }
            }
            else if(team!=null){

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

        }
        playerMoved();

    }

    public void playerMoved() {

        for(Player player:Bukkit.getOnlinePlayers()){

            Team team=game.getTeam(player);

            if(team!=null) {

                Material face = player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType();
                if(face.toString().contains("STAINED_GLASS") || face.equals(Material.GLOWSTONE)) {

                    for(Area area:game.getAreas()){

                        if(!area.isBase()){

                            if(area.isOnArea(player)){

                                if(area.getOwnerTeam()!=null && area.getOwnerTeam().equals(team)){
                                    player.addPotionEffect(area.getBonus());
                                }
                                else {
                                    int teamMate=0;
                                    int playerTotal=0;

                                    for(Player player1:area.getPlayerOn()){
                                        if(team.getMembers().contains(player1.getUniqueId())){
                                            teamMate++;
                                        }
                                        playerTotal++;
                                    }
                                    if(teamMate>playerTotal/2){

                                        if(area.getGeneratorType().equals(Material.CRYING_OBSIDIAN)){
                                            team.getResource().put(Material.CRYING_OBSIDIAN,team.getResource().getOrDefault(Material.CRYING_OBSIDIAN,0)+1);
                                            Location location = area.getMiddle().clone();
                                            location.setY(location.getBlockY()+3.5);
                                            location.setX(location.getBlockX()+0.5);
                                            location.setZ(location.getBlockZ()+0.5);
                                            Location base = team.getBase().getMiddle().clone();
                                            base.setY(base.getBlockY()-0.5);
                                            base.setX(base.getBlockX()+0.5);
                                            base.setZ(base.getBlockZ()+0.5);
                                            player.playSound(player.getLocation(), Sound.BLOCK_GLASS_BREAK,10,10);
                                            Vector vector = base.toVector().subtract(location.toVector()).normalize();
                                            game.getWorld().spawnParticle(Particle.FLAME,location,0,vector.getX(),vector.getY(),vector.getZ(),0.5);
                                            team.getBossBar().setVisible(true);
                                            team.getBossBar().setProgress(team.getResource().getOrDefault(Material.CRYING_OBSIDIAN,0)/(float) game.getObjective());
                                            for(Player player1:Bukkit.getOnlinePlayers()){
                                                team.getBossBar().addPlayer(player1);
                                            }
                                            Bukkit.getScheduler().scheduleSyncDelayedTask(game.getMain(), () -> {

                                                int teamMate1 =0;
                                                int playerTotal1 =0;

                                                for(Player player1:area.getPlayerOn()){
                                                    if(team.getMembers().contains(player1.getUniqueId())){
                                                        teamMate1++;
                                                    }
                                                    playerTotal1++;
                                                }
                                                if(teamMate1 <= playerTotal1 /2){
                                                    team.getBossBar().setVisible(false);
                                                }
                                            },6);
                                            if(team.getResource().getOrDefault(Material.CRYING_OBSIDIAN,0)>=game.getObjective()) {

                                                game.getScoreBoard().updateScoreBoard();

                                                for(Player player1: Bukkit.getOnlinePlayers()){
                                                    player1.playSound(player1.getLocation(),Sound.ENTITY_FIREWORK_ROCKET_LAUNCH,10,10);
                                                    player1.sendTitle("Victoire",String.format("Équipe %s",team.getName()),20,20,20);
                                                }
                                                Bukkit.broadcastMessage("[Space §bConquest] Victoire "+String.format("Équipe §b%s",team.getName()));

                                                game.restart();
                                                Bukkit.getPluginManager().callEvent(new WinEvent(team));
                                                return;
                                            }
                                        }
                                        else area.progressCapture(team);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }




    }
}
