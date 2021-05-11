package io.github.ph1lou.space_conquest.game;

import io.github.ph1lou.space_conquest.enums.ColorTeam;
import io.github.ph1lou.space_conquest.utils.ItemBuilder;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Team {

    private final org.bukkit.scoreboard.Team team;
    private List<String> scoreBoard = new ArrayList<>();
    private final Upgrades upgrade= new Upgrades();
    private final UUID uuid;
    private final GameManager game;
    private ColorTeam colorTeam;
    private final List<UUID> members = new ArrayList<>();
    private UUID founder;
    private Location spawn;
    private String name;
    private final Map<Material,Integer> resource = new HashMap<>();
    private BossBar bossBar;
    private NPC npc ;

    public Team(GameManager game, String name) {
        this.game=game;
        this.uuid=UUID.randomUUID();
        Scoreboard scoreboard = game.getScoreboard();
        this.team = scoreboard.registerNewTeam(this.uuid.toString().substring(0,16));
        int i=0;
        List<ColorTeam> colorTeams = new ArrayList<>();
        for(Team team:game.getTeams()){
            colorTeams.add(team.getColorTeam());
        }

        while(i<ColorTeam.values().length && colorTeams.contains(ColorTeam.values()[i])){
            i++;
        }
        if(i==ColorTeam.values().length){
            this.colorTeam = ColorTeam.values()[0];
        }
        else {
            this.colorTeam = ColorTeam.values()[i];
        }
        team.setAllowFriendlyFire(false);
        team.setOption(org.bukkit.scoreboard.Team.Option.COLLISION_RULE, org.bukkit.scoreboard.Team.OptionStatus.NEVER);
        setName(name);

    }

    public void setColorTeam(ColorTeam colorTeam){
        this.colorTeam=colorTeam;
        team.setPrefix(getColorTeam().getChatColor()+"[Team "+name+"] ");
        this.bossBar = Bukkit.createBossBar(String.format("L'équipe "+getColorTeam().getChatColor()+"%s§r mine la Crying Obsidian",name), getColorTeam().getBarColor(), BarStyle.SOLID);
        for(UUID uuid:this.getMembers()){
            Player player = Bukkit.getPlayer(uuid);
            if(player!=null){
                player.getInventory().setHelmet(new ItemStack(this.getColorTeam().getBanner()));
            }
        }
    }

    public UUID getFounder() {
        return founder;
    }

    public void setFounder(UUID founder) {
        this.founder = founder;
    }

    public Upgrades getUpgrade() {
        return upgrade;
    }

    public ColorTeam getColorTeam() {
        return colorTeam;
    }

    public List<String> getScoreBoard() {
        return scoreBoard;
    }

    public void setScoreBoard(List<String> scoreBoard) {
        this.scoreBoard = scoreBoard;
    }

    public Area getBase() {
        return base;
    }

    public void setBase(Area base) {
        this.base = base;
    }

    private Area base;

    public List<UUID> getMembers() {
        return members;
    }

    public Location getSpawn() {
        return spawn;
    }

    public void setSpawn(Location spawn) {
        this.spawn = spawn;
    }

    public String getName() {
        return this.name;
    }

    @NotNull
    public Map<Material,Integer> getResource() {
        return this.resource;
    }

    public void start(Player player){

        player.getInventory().clear();
        player.setGameMode(GameMode.SURVIVAL);

        PlayerInventory inventory = player.getInventory();
        ItemBuilder helmet = new ItemBuilder(getColorTeam().getMaterial());
        helmet.setDisplayName("Casque Spatial");
        inventory.setHelmet(helmet.build());

        ItemBuilder leatherLeggings=new ItemBuilder(Material.LEATHER_LEGGINGS);
        leatherLeggings.setColor(getColorTeam().getColor());
        leatherLeggings.setDisplayName("Combinaison Spatiale");
        leatherLeggings.setUnbreakable(true);
        leatherLeggings.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,upgrade.isProtection());

        inventory.setLeggings(leatherLeggings.build());


        if(upgrade.getChestPlate()==1){
            ItemBuilder ironChestPlate = new ItemBuilder(Material.IRON_CHESTPLATE);
            ironChestPlate.setDisplayName("Combinaison Spatiale renforcée");
            ironChestPlate.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,upgrade.isProtection());
            ironChestPlate.setUnbreakable(true);
            inventory.setChestplate(ironChestPlate.build());
        }
        else if(upgrade.getChestPlate()==2){
            ItemBuilder diamondChestPlate=new ItemBuilder(Material.DIAMOND_CHESTPLATE);

            diamondChestPlate.setDisplayName("Combinaison Spatiale Ultra Renforcée");
            diamondChestPlate.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,upgrade.isProtection());
            diamondChestPlate.setUnbreakable(true);
            inventory.setChestplate(diamondChestPlate.build());
        }
        else{
            ItemBuilder leatherChestPlate=new ItemBuilder(Material.LEATHER_CHESTPLATE);
            leatherChestPlate.setColor(getColorTeam().getColor());
            leatherChestPlate.setDisplayName("Combinaison Spatiale");
            leatherChestPlate.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,upgrade.isProtection());
            leatherChestPlate.setUnbreakable(true);
            inventory.setChestplate(leatherChestPlate.build());
        }


        ItemBuilder leatherBoots=new ItemBuilder(Material.LEATHER_BOOTS);
        leatherBoots.setColor(getColorTeam().getColor());
        leatherBoots.setDisplayName("Combinaison Spatiale");
        leatherBoots.addEnchant(Enchantment.PROTECTION_FALL,100);
        leatherBoots.addItemFlag(ItemFlag.HIDE_ENCHANTS);
        leatherBoots.setUnbreakable(true);
        leatherBoots.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL,upgrade.isProtection());
        inventory.setBoots(leatherBoots.build());

    }
    public BossBar getBossBar() {
        return bossBar;
    }

    public NPC getNpc() {
        return npc;
    }

    public void setNpc(NPC npc) {
        this.npc = npc;
    }

    public void addPlayer(Player player) {

        if(getMembers().contains(player.getUniqueId())) return;

        if(getMembers().size()<game.getTeamSize()){
            Team team1 = game.getTeam(player);
            if(team1!=null){
                team1.removePlayer(player);
            }
            if(game.getPlayerMax()>game.getPlayerSize()){
                getMembers().add(player.getUniqueId());
                game.setPlayerSize(game.getPlayerSize()+1);
                team.addEntry(player.getName());
                player.getInventory().setHelmet(new ItemStack(this.getColorTeam().getBanner()));
            }
            else player.sendMessage("Le nombre de joueurs max a été atteint");

        }
    }

    public void removePlayer(Player player) {

        if(!getMembers().contains(player.getUniqueId())) return;

        getMembers().remove(player.getUniqueId());
        game.setPlayerSize(game.getPlayerSize()-1);
        team.removeEntry(player.getName());
        player.getInventory().setHelmet(null);

        if(getFounder().equals(player.getUniqueId())){
            if(getMembers().size()>0){
                setFounder(getMembers().get(0));
            }
            else {
                game.getTeams().remove(this);
                team.unregister();
            }
        }
    }


    public void setName(String name) {
        this.name=name;
        team.setPrefix(getColorTeam().getChatColor()+"[Team "+name+"] ");
        this.bossBar = Bukkit.createBossBar(String.format("L'équipe "+getColorTeam().getChatColor()+"%s§r mine la Crying Obsidian",name), getColorTeam().getBarColor(), BarStyle.SOLID);
        bossBar.setVisible(false);
    }

    public UUID getUuid() {
        return uuid;
    }
}
