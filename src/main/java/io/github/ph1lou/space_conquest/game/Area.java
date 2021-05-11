package io.github.ph1lou.space_conquest.game;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class Area {

    private Material generatorType;

    private final Location middle;

    @Nullable
    private Team ownerTeam;

    private int controlSize;

    @Nullable
    private Team isCapture;

    private final boolean isBase;

    private PotionEffect bonus=new PotionEffect(PotionEffectType.SPEED,40,1,false,false);

    private final List<Location> blocks = new ArrayList<>();

    public Area(boolean isBase , Location middle){
        this.isBase=isBase;
        this.middle=middle;
    }

    public List<Location> getBlocks() {
        return blocks;
    }

    public boolean isOnArea(Player player){
        Location location = player.getLocation();
        return blocks.stream()
                .filter(location1 -> location1.getX()==location.getBlockX())
                .filter(location1 -> location1.getY()+1==location.getBlockY())
                .anyMatch(location1 -> location1.getZ()==location.getBlockZ());
    }

    public List<Player> getPlayerOn(){
        return Bukkit.getOnlinePlayers().stream()
                .filter(this::isOnArea)
                .collect(Collectors.toList());
    }

    public void progressCapture(Team team, Consumer<Team> consumer){

        int temp = this.controlSize;

        if(team.equals(this.isCapture)){

            this.progressControl();
            if(temp==this.controlSize){
                if(this.isBase && !team.equals(this.getOwnerTeam())){
                    consumer.accept(this.getOwnerTeam());
                    this.removeControl();
                }
                else {
                    this.ownerTeam=team;
                    for(Player player: getPlayerOn()){
                        player.setVelocity(new Vector(0,3,0));
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,300,0,false,false));
                        player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE,10,10);
                    }
                }
            }
        }
        else {
            this.ownerTeam=null;
            this.removeControl();
            if(temp==this.controlSize){
                this.isCapture=team;
            }
        }
    }

    private void progressControl() {
        for(Location location:getBlocks()){
            if(location.getBlock().getType().equals(Material.WHITE_STAINED_GLASS)){
                controlSize+=1;
                assert isCapture != null;
                location.getBlock().setType(isCapture.getColorTeam().getMaterial());
                break;
            }
        }
    }

    public void removeControl() {

        for(Location location : this.getBlocks()){
            Material material = location.getBlock().getType();
            if(material.toString().contains("STAINED_GLASS") && !material.equals(Material.WHITE_STAINED_GLASS)){
                location.getBlock().setType(Material.WHITE_STAINED_GLASS);
                this.controlSize-=1;
                break;
            }
        }
    }

    public @Nullable Team getIsCapture() {
        return isCapture;
    }

    public void setIsCapture(@Nullable Team isCapture) {
        this.isCapture = isCapture;
    }

    public PotionEffect getBonus() {
        return bonus;
    }

    public void setBonus(PotionEffect bonus) {
        this.bonus = bonus;
    }

    @Nullable
    public Material getGeneratorType() {
        return this.generatorType;
    }

    public void setGeneratorType(Material generatorType) {
        this.generatorType = generatorType;
    }

    public boolean isBase() {
        return this.isBase;
    }

    public Location getMiddle() {
        return this.middle;
    }

    public @Nullable Team getOwnerTeam() {
        return this.ownerTeam;
    }

    public void setOwnerTeam(@Nullable Team ownerTeam) {
        this.ownerTeam = ownerTeam;
    }

    public int getControlSize() {
        return this.controlSize;
    }

    public float getRatioPlayerOn(Team team) {
        List<Player> players = this.getPlayerOn();
        return players.stream()
                .filter(player -> team.getMembers().contains(player.getUniqueId()))
                .count()/(float)Math.max(1,players.size());
    }
}
