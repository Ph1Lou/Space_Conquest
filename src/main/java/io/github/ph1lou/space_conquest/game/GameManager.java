package io.github.ph1lou.space_conquest.game;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.space_conquest.Main;
import io.github.ph1lou.space_conquest.MapLoader;
import io.github.ph1lou.space_conquest.enums.State;
import io.github.ph1lou.space_conquest.listeners.Capture;
import io.github.ph1lou.space_conquest.listeners.LobbyListener;
import io.github.ph1lou.space_conquest.listeners.PlayerListener;
import io.github.ph1lou.space_conquest.tasks.Lobby;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GameManager {


    public void setObjective(int objective) {
        this.objective = objective;
    }

    private int objective=1000;

    public int getCenterSize() {
        return centerSize;
    }

    public void setCenterSize(int centerSize) {
        this.centerSize = centerSize;
    }

    private int centerSize=7;

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    private String gameName="@Ph1Lou_";

    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    private int teamNumber=12;

    public int getPlayerMax() {
        return playerMax;
    }

    public void setPlayerMax(int playerMax) {
        this.playerMax = playerMax;
    }

    private int playerMax=40;

    public boolean isSingleColor() {
        return singleColor;
    }

    public void setSingleColor(boolean singleColor) {
        this.singleColor = singleColor;
    }

    private boolean singleColor = true;

    public int getZoneNumber() {
        return zoneNumber;
    }

    public void setZoneNumber(int zoneNumber) {
        this.zoneNumber = zoneNumber;
    }

    private int zoneNumber =10;

    private final Main main;

    private int teamSize=4;

    private Capture capture;

    private final LobbyListener lobbyListener;

    private PlayerListener playerListener;

    private State state = State.LOBBY;

    private final List<Team> teams = new ArrayList<>();

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    private final Scoreboard scoreboard;

    private World world;

    public List<Area> getAreas() {
        return areas;
    }

    private final List<Area> areas = new ArrayList<>();


    private final Map<UUID, FastBoard> fastBoard = new HashMap<>();

    private final Map<UUID, Integer> kills = new HashMap<>();

    private int playerSize=0;

    private final MapLoader mapLoader;

    public ScoreBoard getScoreBoard() {
        return scoreBoard;
    }

    private final ScoreBoard scoreBoard;

    private int timer;

    public int getTimer() {
        return timer;
    }

    public void setTimer(int timer) {
        this.timer = timer;
    }

    public boolean isState(State state) {
        return state==this.state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Main getMain() {
        return main;
    }

    public MapLoader getMapLoader() {
        return mapLoader;
    }

    public int getTeamSize() {
        return teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public GameManager(Main main){
        this.main = main;
        mapLoader = new MapLoader(this);
        scoreBoard = new ScoreBoard(this);
        this.lobbyListener=new LobbyListener(this);
        Bukkit.getPluginManager().registerEvents(this.lobbyListener,main);
        scoreboard=Bukkit.getScoreboardManager().getMainScoreboard();
        for(org.bukkit.scoreboard.Team team:scoreboard.getTeams()){
            team.unregister();
        }
        Lobby start = new Lobby(this);
        start.runTaskTimer(main, 0, 5);
    }

    public List<Team> getTeams() {
        return teams;
    }

    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    @Nullable
    public Team getTeam(Player player){
        for(Team team:getTeams()){
            if(team.getMembers().contains(player.getUniqueId())){
                return team;
            }
        }
        return null;
    }


    public void repartition() {

        world=mapLoader.generateMap();
        world.setGameRule(GameRule.DO_INSOMNIA,false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN,true);
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setTime(19000);
        mapLoader.generateTeamCamp();
        this.capture=new Capture(this);
        this.playerListener=new PlayerListener(this);
        Bukkit.getPluginManager().registerEvents(capture,main);
        Bukkit.getPluginManager().registerEvents(this.playerListener,main);

    }

    public int getPlayerSize() {
        return playerSize;
    }

    public void setPlayerSize(int playerSize) {
        this.playerSize = playerSize;
    }

    public Map<UUID, FastBoard> getFastBoard() {
        return fastBoard;
    }

    public Map<UUID, Integer> getKills() {
        return kills;
    }

    public int getTeamNumber() {
        return this.teamNumber;
    }

    public void restart() {

        setState(State.END);
        HandlerList.unregisterAll(this.lobbyListener);
        HandlerList.unregisterAll(this.playerListener);
        HandlerList.unregisterAll(this.capture);
        main.setCurrentGame(new GameManager(main));

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            ItemStack itemStack = new ItemStack(Material.WHITE_BANNER);
            GameManager newGame = main.getCurrentGame();
            player.getInventory().clear();
            player.setGameMode(GameMode.ADVENTURE);
            FastBoard fastBoard = new FastBoard(player);
            fastBoard.updateTitle("Space §bConquest");
            newGame.getFastBoard().put(player.getUniqueId(),fastBoard);

            player.getInventory().addItem(itemStack);
        }
        getMapLoader().deleteMap();
    }

    public Integer getObjective() {
        return this.objective;
    }
}
