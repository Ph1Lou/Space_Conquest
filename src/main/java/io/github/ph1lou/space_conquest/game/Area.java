package io.github.ph1lou.space_conquest.game;

import io.github.ph1lou.space_conquest.enums.TowerMode;
import io.github.ph1lou.space_conquest.utils.Laser;
import io.github.ph1lou.space_conquest.utils.TexturedItem;
import net.minecraft.util.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.Color;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Area {

    private final GameManager game;
    private final int size;

    private TexturedItem generatorType;

    private final Location middle;

    private final List<Area> neighbours = new ArrayList<>();

    @Nullable
    private Team ownerTeam;

    @Nullable
    private Area target;

    private int controlSize;

    private TowerMode mode = TowerMode.NOT_DEFINED;

    @Nullable
    private Team isCapture;

    private final Laser laser;

    private final boolean isBase;

    private final boolean isMiddle;

    private final List<Location> blocks = new ArrayList<>();
    private int timer=0;

    public Area(GameManager game, boolean isBase, boolean isMiddle , Location middle, TexturedItem generatorType, int size){
        this.game=game;
        this.isBase=isBase;
        this.middle=middle;
        this.size=size;
        this.isMiddle=isMiddle;
        this.generatorType = generatorType;
        this.laser = new Laser.GuardianLaser(this.middle.clone().add(new Vector(0.5,this.isBase?
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


    public void initNeighbours(){
        this.neighbours.addAll(game.getAreas().stream()
                .filter(area -> game.getWorld().equals(this.getMiddle().getWorld()))
                .filter(area -> !area.equals(this))
                .filter(area -> !area.isBase())
                .filter(area -> !area.isMiddle())
                .filter(area -> area.getGeneratorType().equals(this.generatorType))
                .map(area -> new Tuple<>(area,this.getMiddle().distance(area.getMiddle())))
                .sorted(Comparator.comparingDouble(Tuple::b))
                .map(Tuple::a)
                .limit(2)
                .collect(Collectors.toList()));
    }

    private void progressCaptureAuto(Team team){

        if (this.isInSuperiority(team)) {
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
                .filter(player -> game.getTeam(player).isPresent())
                .filter(this::isOnArea)
                .collect(Collectors.toList());
    }

    public void progressCapture(Team team){

        if(this.isMiddle() || this.isBase()){
            return;
        }

        int temp = this.controlSize;

        if(team.equals(this.isCapture)){

            this.progressControl();
            if(temp==this.controlSize){

                if(!this.isBase && !team.equals(this.getOwnerTeam())){
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
            this.setOwnerTeam(null);
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

    public TexturedItem getGeneratorType() {
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
        if(!this.isBase && !this.isMiddle){
            this.setMode(TowerMode.MINE);
        }
        this.ownerTeam = ownerTeam;
    }

    public int getControlSize() {
        return this.controlSize;
    }


    public boolean isInSuperiority(Team team){
        List<Player> players = this.getPlayerOn();

        AtomicReference<Float> teamTotal= new AtomicReference<>((float) players.stream()
                .filter(player -> team.getMembers().contains(player.getUniqueId()))
                .count());
        float othersTeamTotal = players.size() - teamTotal.get();

        this.neighbours.stream()
                .filter(area -> team.equals(area.getOwnerTeam()))
                .filter(area -> area.getTarget().isPresent() && area.getTarget().get().equals(this))
                .forEach(area -> teamTotal.updateAndGet(v -> (float) (v + 0.6)));

        if(this.mode == TowerMode.DEFEND_AND_CONQUEST ||
                this.mode == TowerMode.DEFEND_AND_MINE ||
                this.mode == TowerMode.DEFEND){
            othersTeamTotal+=1.05;
        }

        return Math.max(othersTeamTotal,0.5) < teamTotal.get();

    }

    public void setGeneratorType(TexturedItem generatorType) {
        this.generatorType=generatorType;
    }

    public boolean isMiddle() {
        return isMiddle;
    }

    public TowerMode getMode() {
        return mode;
    }

    public void setMode(TowerMode mode) {
        this.laser.moveEnd(this.getMiddle().add(new Vector(0.5,20,0.5)));
        this.target = null;
        Collections.shuffle(this.neighbours);
        this.mode = mode;
    }

    /**
     * Génère les particules du beacon vers la base de la team et les ajoutes dans leur ressources de team
     * @param team la team a qui appartient à la zone
     */
    public void mineRessources(Team team) {

        if(this.isMiddle || this.mode == TowerMode.MINE ||
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

        if(this.getOwnerTeam()==null || this.isMiddle()){
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
                    this.laser.moveEnd(this.getMiddle().add(new Vector(0.5,20,0.5)));
                    this.target=null;
                }
            }

            this.target = this.neighbours.stream()
                    .filter(area -> area.getOwnerTeam() == null ||
                            !area.getOwnerTeam().equals(this.getOwnerTeam()))
                    .findFirst()
                    .orElse(null);

        }

        if (this.mode == TowerMode.DEFEND_AND_CONQUEST || this.mode== TowerMode.DEFEND_AND_MINE || this.mode == TowerMode.DEFEND){
            Location loc = this.getMiddle().add(new Vector(0,3,0));
            double tot = Math.PI*this.size/2f;

            for(int i=0;i<tot;i++){

                int x = (int) (Math.round(this.size * Math.cos(2*Math.PI*i/tot) + loc.getX()));
                int z = (int) (Math.round(this.size * Math.sin(2*Math.PI*i/tot) + loc.getZ()));
                int red = 199;
                int green = 21;
                int blue = 133;
                Location circle = new Location(loc.getWorld(),x,loc.getY(),z);
                this.getMiddle()
                        .getWorld()
                        .spawnParticle(Particle.REDSTONE,
                                circle,
                                2,
                                new Particle.DustOptions(Color.fromBGR(red, green, blue), 1));
            }
        }

        if(this.mode == TowerMode.ATTACK){
            AtomicBoolean present = new AtomicBoolean();
            Bukkit.getOnlinePlayers().stream()
                    .filter(player -> game.getTeam(player)
                            .filter(value -> !value.equals(this.getOwnerTeam()))
                            .isPresent())
                    .filter(player -> player.getWorld().equals(this.getMiddle().getWorld()))
                    .filter(player -> player.getLocation().distance(this.getMiddle())<30)
                    .findFirst()
                    .ifPresent(player -> {
                        this.laser.moveEnd((player.getEyeLocation().add(new Vector(0,-1,0))));
                        player.damage(1);
                        present.set(true);
                    });

            if(!present.get()){
                this.laser.moveEnd(this.getMiddle().add(new Vector(0.5,20,0.5)));
            }
        }
    }

    public Optional<Area> getTarget() {
        return Optional.ofNullable(target);
    }

}
