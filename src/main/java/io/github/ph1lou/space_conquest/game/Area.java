package io.github.ph1lou.space_conquest.game;

import io.github.ph1lou.space_conquest.enums.TowerMode;
import io.github.ph1lou.space_conquest.utils.Laser;
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
import java.util.concurrent.atomic.AtomicBoolean;
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

    private TowerMode mode = TowerMode.MINE;

    @Nullable
    private Team isCapture;

    private final Laser laser;

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
        this.laser = new Laser(this.middle.clone().add(new Vector(0.5,this.isBase?
                0
                :
                this.isMiddle ?
                        20
                        :
                        3,0.5)),
                this.middle.clone().add(new Vector(0.5,this.isMiddle || this.isBase?
                        -20
                        :
                        20,0.5)));
        this.laser.start();

    }

    private void progressCaptureAuto(Team team){

        if (this.getRatioPlayerOn(team,true) > 1/2f) {
            this.progressCapture(team);
        }
    }

    public List<Location> getBlocks() {
        return blocks;
    }

    public boolean isOnArea(Player player){
        return isOnArea(player.getLocation());
    }


    public boolean isOnArea(Location location1){

        return blocks.stream()
                .filter(location -> location.getX()==location1.getBlockX())
                .filter(location -> location.getY()+1==location1.getBlockY())
                .anyMatch(location -> location.getZ()==location1.getBlockZ());
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
                        player.addPotionEffect(new PotionEffect(
                                PotionEffectType.SLOW_FALLING,
                                300,
                                0,
                                false,
                                false));
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
                this.controlSize--;
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
        return this.middle.clone();
    }

    public @Nullable Team getOwnerTeam() {
        return this.ownerTeam;
    }

    public void setOwnerTeam(@Nullable Team ownerTeam) {
        this.ownerTeam = ownerTeam;
        this.setMode(TowerMode.MINE);
    }

    public int getControlSize() {
        return this.controlSize;
    }

    public float getRatioPlayerOn(Team team) {
        return this.getRatioPlayerOn(team,false);
    }

    public float getRatioPlayerOn(Team team, boolean ok){
        List<Player> players = this.getPlayerOn();
        int total = players.size();
        int boost = ok?1:0;
        if(this.mode == TowerMode.DEFEND_AND_CONQUEST || this.mode == TowerMode.DEFEND_AND_MINE || this.mode == TowerMode.DEFEND){
            total++;
        }
        return (players.stream()
                .filter(player -> team.getMembers().contains(player.getUniqueId()))
                .count()+boost)/(float)Math.max(1,total);
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
        this.laser.moveEnd(this.getMiddle().add(new Vector(0.5,10,0.5)));
        this.mode = mode;
    }


    /**
     * Génère les particules du beacon vers la base de la team et les ajoutes dans leur ressources de team
     * @param team la team a qui appartient à la zone
     */
    public void mineRessources(Team team) {

        if(this.mode == TowerMode.MINE ||
                this.mode == TowerMode.CONQUEST_AND_MINE ||
                this.mode == TowerMode.DEFEND_AND_MINE){
            team.getResource().put(this.getGeneratorType(), team.getResource().getOrDefault(this.getGeneratorType(),0)+this.getBlocks().size()/18);
            Location location = this.getMiddle();
            location.setY(location.getBlockY()+3.5);
            location.setX(location.getBlockX()+0.5);
            location.setZ(location.getBlockZ()+0.5);
            Location base = team.getBase().getMiddle();
            base.setY(base.getBlockY()-0.5);
            base.setX(base.getBlockX()+0.5);
            base.setZ(base.getBlockZ()+0.5);
            Vector vector = base.toVector().subtract(location.toVector()).normalize();
            game.getWorld().spawnParticle(Particle.FLAME,location,0,vector.getX(),vector.getY(),vector.getZ(),0.5);
        }

        if(this.getOwnerTeam()==null){
            return;
        }

        if(this.mode == TowerMode.CONQUEST ||
                this.mode == TowerMode.CONQUEST_AND_MINE ||
                this.mode == TowerMode.DEFEND_AND_CONQUEST){

            this.timer++;
            this.timer%=4;

            if(this.timer!=0){
                return;
            }

            if(this.target!=null){
                if(!this.getOwnerTeam().equals(this.target.getOwnerTeam())){
                    this.laser.moveEnd(this.target.getMiddle().add(new Vector(0.5,2,0.5)));
                    this.target.progressCaptureAuto(this.getOwnerTeam());
                    return;
                }
                else{
                    this.laser.moveEnd(this.getMiddle().add(new Vector(0.5,10,0.5)));
                    this.target=null;
                }
            }

            this.target = game.getAreas().stream()
                    .filter(area -> game.getWorld().equals(this.getMiddle().getWorld()))
                    .filter(area -> !this.getOwnerTeam().equals(area.getOwnerTeam()))
                    .filter(area -> !area.isBase())
                    .filter(area -> !area.isMiddle())
                    .filter(area -> area.getGeneratorType().equals(this.getGeneratorType()))
                    .map(area -> new Tuple<>(area,this.getMiddle().distance(area.getMiddle())))
                    .sorted(Comparator.comparingDouble(Tuple::b))
                    .filter(areaDoubleTuple -> areaDoubleTuple.b()<75)
                    .map(Tuple::a)
                    .findFirst().orElse(null);
        }
        else if(this.mode == TowerMode.ATTACK){
            AtomicBoolean present = new AtomicBoolean();
            Bukkit.getOnlinePlayers().stream()
                    .filter(player -> player.getWorld().equals(this.getMiddle().getWorld()))
                    .filter(player -> player.getLocation().distance(this.getMiddle())<30)
                    .findFirst()
                    .ifPresent(player -> {
                        this.laser.moveEnd((player.getEyeLocation().add(new Vector(0,-1,0))));
                        player.damage(1);
                        present.set(true);
                    });

            if(!present.get()){
                this.laser.moveEnd(this.getMiddle().add(new Vector(0.5,10,0.5)));
            }
        }
    }

}
