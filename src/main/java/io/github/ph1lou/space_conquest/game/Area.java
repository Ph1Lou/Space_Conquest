package io.github.ph1lou.space_conquest.game;

import io.github.ph1lou.space_conquest.enums.TowerMode;
import net.minecraft.server.v1_16_R3.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Area {

    private final GameManager game;

    private Material generatorType;

    private final Location middle;

    @Nullable
    private Team ownerTeam;

    @Nullable
    private Area target;

    private int controlSize;

    private TowerMode mode =TowerMode.MINE;

    @Nullable
    private Team isCapture;

    private final boolean isBase;

    private final boolean isMiddle;

    private final List<Location> blocks = new ArrayList<>();
    private int timer=0;

    public Area(GameManager game, boolean isBase, boolean isMiddle , Location middle, Material generatorType){
        this.game=game;
        this.isBase=isBase;
        this.middle=middle;
        this.isMiddle=isMiddle;
        this.generatorType = generatorType;
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

    public void progressCapture(Team team){

        int temp = this.controlSize;

        if(team.equals(this.isCapture)){

            this.progressControl();
            if(temp==this.controlSize){
                if(this.isBase && !team.equals(this.getOwnerTeam())){
                    //mettre ici le vol des crying obsidian de l'autre équipe
                }
                else {
                    this.setOwnerTeam(team);
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
            if(temp==this.controlSize){ //tant que la base n'est pas toute blanche
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

    public Material getGeneratorType() {
        return this.generatorType;
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
        mode = TowerMode.MINE;
    }

    public int getControlSize() {
        return this.controlSize;
    }

    public float getRatioPlayerOn(Team team) {
        List<Player> players = this.getPlayerOn();
        int total = players.size();
        if(this.mode == TowerMode.DEFEND_AND_CONQUEST || this.mode == TowerMode.DEFEND_AND_MINE || this.mode == TowerMode.DEFEND){
            total++;
        }
        return players.stream()
                .filter(player -> team.getMembers().contains(player.getUniqueId()))
                .count()/(float)Math.max(1,total);
    }

    public void setGeneratorType(Material generatorType) {
        this.generatorType=generatorType;
    }

    public boolean isMiddle() {
        return isMiddle;
    }

    public TowerMode getMode() {
        return mode;
    }

    public void setMode(TowerMode mode) {
        this.mode = mode;
    }


    /**
     * Génère les particules du beacon vers la base de la team et les ajoutes dans leur ressources de team
     * @param team la team a qui appartient à la zone
     */
    public void mineRessources(Team team) {

        if(this.mode == TowerMode.MINE || this.mode == TowerMode.CONQUEST_AND_MINE || this.mode == TowerMode.DEFEND_AND_MINE){
            team.getResource().put(this.getGeneratorType(), team.getResource().getOrDefault(this.getGeneratorType(),0)+this.getBlocks().size()/18);
            Location location = this.getMiddle().clone();
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

        if(this.getOwnerTeam()==null){
            return;
        }


        if(this.mode == TowerMode.CONQUEST || this.mode == TowerMode.CONQUEST_AND_MINE || this.mode == TowerMode.DEFEND_AND_CONQUEST){

            this.timer++;
            this.timer%=4;

            if(this.timer!=0){
                return;
            }


            if(this.target!=null){
                if(!this.getOwnerTeam().equals(this.target.getOwnerTeam())){
                    Location location = this.getMiddle().clone();
                    location.setY(location.getBlockY()+3.5);
                    location.setX(location.getBlockX()+0.5);
                    location.setZ(location.getBlockZ()+0.5);
                    Location base = team.getBase().getMiddle().clone();
                    base.setY(base.getBlockY()+3.5);
                    base.setX(base.getBlockX()+0.5);
                    base.setZ(base.getBlockZ()+0.5);
                    Vector vector = base.toVector().subtract(location.toVector()).normalize().multiply(50);
                    game.getWorld().spawnParticle(Particle.FLAME,location,0,vector.getX(),vector.getY(),vector.getZ(),0.5);
                    this.target.progressCapture(this.getOwnerTeam());
                    return;
                }

            }

            this.target = game.getAreas().stream()
                    .filter(area -> game.getWorld().equals(this.getMiddle().getWorld()))
                    .filter(area -> !this.getOwnerTeam().equals(area.getOwnerTeam()))
                    .filter(area -> !area.isBase())
                    .filter(area -> !area.isMiddle())
                    .map(area -> new Tuple<>(area,this.getMiddle().distance(area.getMiddle())))
                    .sorted(Comparator.comparingDouble(Tuple::b))
                    .filter(areaDoubleTuple -> areaDoubleTuple.b()<100)
                    .map(Tuple::a)
                    .findFirst().orElse(null);
        }
        else if(this.mode == TowerMode.ATTACK){
            Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getWorld().equals(this.getMiddle().getWorld()))
                    .filter(player -> player.getLocation().distance(this.getMiddle())<30)
                    .findFirst()
                    .ifPresent(player -> {
                        Location location = this.getMiddle().clone();
                        location.setY(location.getBlockY()+3.5);
                        location.setX(location.getBlockX()+0.5);
                        location.setZ(location.getBlockZ()+0.5);
                        Vector vector = player.getEyeLocation().toVector().subtract(location.toVector()).normalize().multiply(20);
                        game.getWorld().spawnParticle(Particle.FLAME,location,0,vector.getX(),vector.getY(),vector.getZ(),0.5);
                        player.damage(1);
                    });
        }

    }

}
