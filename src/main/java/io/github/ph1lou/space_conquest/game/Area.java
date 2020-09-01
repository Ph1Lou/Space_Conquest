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

public class Area {

    private int size;

    public Material getGeneratorType() {
        return generatorType;
    }

    public void setGeneratorType(Material generatorType) {
        this.generatorType = generatorType;
    }

    private Material generatorType;

    public Location getMiddle() {
        return middle;
    }


    private final Location middle;

    public @Nullable Team getOwnerTeam() {
        return ownerTeam;
    }

    public void setOwnerTeam(@Nullable Team ownerTeam) {
        this.ownerTeam = ownerTeam;
    }

    @Nullable
    private Team ownerTeam;

    public int getControlSize() {
        return controlSize;
    }

    private int controlSize;

    public @Nullable Team getIsCapture() {
        return isCapture;
    }

    public void setIsCapture(@Nullable Team isCapture) {
        this.isCapture = isCapture;
    }

    @Nullable
    private Team isCapture;

    private final boolean isBase;

    public PotionEffect getBonus() {
        return bonus;
    }

    public void setBonus(PotionEffect bonus) {
        this.bonus = bonus;
    }

    private PotionEffect bonus=new PotionEffect(PotionEffectType.SPEED,40,1,false,false);

    private final List<Location> blocks = new ArrayList<>();


    public Area(GameManager game,boolean isBase , Location middle){
        this.isBase=isBase;
        this.middle=middle;
    }

    public List<Location> getBlocks() {
        return blocks;
    }



    public boolean isOnArea(Player player){
        Location location = player.getLocation();
        for(Location block:getBlocks()){
            if(block.getX()==location.getBlockX() && block.getY()+1==location.getBlockY() && block.getZ()==location.getBlockZ()){
                return true;
            }
        }
        return false;
    }

    public List<Player> getPlayerOn(){
        List<Player> players = new ArrayList<>();
        for(Player player:Bukkit.getOnlinePlayers()){
            if(isOnArea(player)) players.add(player);
        }
        return players;
    }

    public void progressCapture(Team team){
        int temp = controlSize;
        if(team.equals(isCapture)){
            progressControl();
            if(temp==controlSize){
                ownerTeam=team;
                for(Player player:getPlayerOn()){
                    player.setVelocity(new Vector(0,3,0));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING,300,0,false,false));
                    player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE,10,10);
                }
            }
        }
        else {
            ownerTeam=null;
            resetControl();
            if(temp==controlSize){
                isCapture=team;
            }
        }
    }

    private void progressControl() {
        for(Location location:getBlocks()){
            if(location.getBlock().getType().equals(Material.WHITE_STAINED_GLASS)){
                controlSize+=1;
                assert isCapture != null;
                assert isCapture != null;
                location.getBlock().setType(isCapture.getColorTeam().getMaterial());
                break;
            }

        }
    }

    public void resetControl() {

        for(Location location:getBlocks()){
            Material material= location.getBlock().getType();
            if(material.toString().contains("STAINED_GLASS") && !material.equals(Material.WHITE_STAINED_GLASS)){
                location.getBlock().setType(Material.WHITE_STAINED_GLASS);
                controlSize-=1;
                break;
            }
        }
    }

    public boolean isBase() {
        return isBase;
    }

}
