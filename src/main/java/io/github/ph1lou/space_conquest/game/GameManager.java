package io.github.ph1lou.space_conquest.game;

import fr.mrmicky.fastboard.FastBoard;
import io.github.ph1lou.space_conquest.Main;
import io.github.ph1lou.space_conquest.MapLoader;
import io.github.ph1lou.space_conquest.enums.State;
import io.github.ph1lou.space_conquest.listeners.GameListener;
import io.github.ph1lou.space_conquest.listeners.LobbyListener;
import io.github.ph1lou.space_conquest.listeners.PlayerListener;
import io.github.ph1lou.space_conquest.tasks.LobbyTask;
import io.github.ph1lou.space_conquest.utils.TexturedItem;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class GameManager {

    private int objective = 8000;
    private boolean singleColor = true;
    private int playerMax = 40;
    private int teamNumber = 6;
    private String gameName = "@Ph1Lou_";
    private int centerSize = 7;
    private int zoneNumber = 12;
    private final Main main;
    private int teamSize = 3;
    private GameListener gameListener;
    private final LobbyListener lobbyListener;
    private PlayerListener playerListener;
    private State state = State.LOBBY;
    private final List<Team> teams = new ArrayList<>();
    private final ScoreBoard scoreBoard;
    private int timer;
    private final List<Area> areas = new ArrayList<>();
    private final Scoreboard scoreboard;
    private World world;
    private final Map<UUID, FastBoard> fastBoard = new HashMap<>();
    private final Map<UUID, Integer> kills = new HashMap<>();
    private int playerSize = 0;
    private final MapLoader mapLoader;
    private final boolean training;
    private int countDown=30;
    private final int teamAutoStart;

    public GameManager(Main main){
        this.main = main;
        this.mapLoader = new MapLoader(this);
        this.scoreBoard = new ScoreBoard(this);
        this.lobbyListener=new LobbyListener(this);
        Bukkit.getPluginManager().registerEvents(this.lobbyListener,main);
        this.scoreboard= Objects.requireNonNull(Objects.requireNonNull(Bukkit.getScoreboardManager())).getMainScoreboard();
        for(org.bukkit.scoreboard.Team team:this.scoreboard.getTeams()){
            team.unregister();
        }
        this.training = main.getConfig().getBoolean("training");
        this.teamAutoStart = main.getConfig().getInt("number_team_auto_start");
        LobbyTask start = new LobbyTask(this);
        start.runTaskTimer(main, 0, 5);
    }

    public void setObjective(int objective) {
        this.objective = objective;
    }

    public int getCenterSize() {
        return this.centerSize;
    }

    public String translate(String key, Object... args) {
        LanguageManager languageManager = this.main.getLangManager();
        String translation = languageManager.getTranslation(key);
        try {
            return String.format(translation, args);
        } catch (IllegalFormatException e) {
            Bukkit.getConsoleSender().sendMessage(String.format("Error while formatting translation (%s)", key.toLowerCase()));
            return translation + " (Format error)";
        }
    }

    public List<String> translateArray(String key) {
        LanguageManager languageManager = main.getLangManager();
        return languageManager.getTranslationList(key);
    }

    public void setCenterSize(int centerSize) {
        this.centerSize = centerSize;
    }

    public String getGameName() {
        return this.gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setTeamNumber(int teamNumber) {
        this.teamNumber = teamNumber;
    }

    public int getPlayerMax() {
        return this.playerMax;
    }

    public void setPlayerMax(int playerMax) {
        this.playerMax = playerMax;
    }

    public boolean isSingleColor() {
        return this.singleColor;
    }

    public void setSingleColor(boolean singleColor) {
        this.singleColor = singleColor;
    }

    public int getZoneNumber() {
        return this.zoneNumber;
    }

    public void setZoneNumber(int zoneNumber) {
        this.zoneNumber = zoneNumber;
    }

    public Scoreboard getScoreboard() {
        return this.scoreboard;
    }

    public List<Area> getAreas() {
        return this.areas;
    }

    public ScoreBoard getScoreBoard() {
        return this.scoreBoard;
    }

    public int getTimer() {
        return this.timer;
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
        return this.main;
    }

    public MapLoader getMapLoader() {
        return this.mapLoader;
    }

    public int getTeamSize() {
        return this.teamSize;
    }

    public void setTeamSize(int teamSize) {
        this.teamSize = teamSize;
    }

    public List<? extends Team> getTeams() {
        return this.teams;
    }

    public void registerTeam(Team team){
        this.teams.add(team);
    }

    public World getWorld() {
        return this.world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public Optional<Team> getTeam(Player player){
        return this.teams
                .stream()
                .filter(team -> team.getMembers().contains(player.getUniqueId()))
                .findFirst();
    }

    public void start() {

        if(this.isTraining()){
            switch (this.getTeams().size()) {
                case 5 -> this.setZoneNumber(15);
                case 6 -> this.setZoneNumber(18);
                default -> {
                }
            }
        }

        this.world = this.mapLoader.generateMap();
        this.world.setGameRule(GameRule.DO_INSOMNIA,false);
        this.world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN,true);
        this.world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE,false);
        this.world.setDifficulty(Difficulty.PEACEFUL);
        this.world.setTime(19000);
        this.mapLoader.generateTeamCamp();
        this.gameListener =new GameListener(this);
        this.playerListener=new PlayerListener(this);
        Bukkit.getPluginManager().registerEvents(this.gameListener,this.main);
        Bukkit.getPluginManager().registerEvents(this.playerListener,main);
    }

    public int getPlayerSize() {
        return this.playerSize;
    }

    public void setPlayerSize(int playerSize) {
        this.playerSize = playerSize;
    }

    public Collection<? extends FastBoard> getFastBoard() {
        return this.fastBoard.values();
    }

    public Map<UUID, Integer> getKills() {
        return this.kills;
    }

    public int getTeamNumber() {
        return this.teamNumber;
    }

    public void restart() {

        this.setState(State.END);
        HandlerList.unregisterAll(this.lobbyListener);
        HandlerList.unregisterAll(this.playerListener);
        HandlerList.unregisterAll(this.gameListener);
        this.main.setCurrentGame(new GameManager(this.main));

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
            ItemStack itemStack = new ItemStack(Material.WHITE_BANNER);
            GameManager newGame = this.main.getCurrentGame();
            player.getInventory().clear();
            player.setGameMode(GameMode.ADVENTURE);
            newGame.registerBoard(player);
            player.getInventory().addItem(itemStack);
        });

        this.getMapLoader().deleteMap();
    }

    public int getObjective() {
        return this.objective;
    }

    public boolean isTraining() {
        return training;
    }

    public void initStart() {
        if(this.teams.stream()
                .filter(team -> team.getMembers().size()==this.getTeamSize()).count()>=this.getTeamAutoStart()){
            this.state=State.PRE_START;
        }
    }

    public void removeStart() {
        if(this.state == State.PRE_START){
            this.state = State.LOBBY;
            this.countDown = 30;
        }
    }

    public int getCountDown() {
        return countDown;
    }

    public void setCountDown(int countDown) {
        this.countDown = countDown;
    }

    public int getTeamAutoStart() {
        return teamAutoStart;
    }


    public void sendScore(Team team) {

        this.teams.stream().sorted(Comparator.comparingInt(value -> -value.getResource()
                .getOrDefault(TexturedItem.CRYING_OBSIDIAN_RESSOURCE,0)))
                .forEach(team1 -> {

                    int ressources = team1.getResource()
                            .getOrDefault(TexturedItem.CRYING_OBSIDIAN_RESSOURCE,0);
                    if(team1.equals(team)){
                        ressources = ressources*5/4;
                    }

                    Bukkit.broadcastMessage(this.translate(
                            "space-conquest.team.point",team1.getName(),ressources));
                });
    }

    public void registerBoard(Player player) {
        FastBoard fastboard = new FastBoard(player);
        fastboard.updateTitle(this.translate("space-conquest.title"));
        this.fastBoard.put(player.getUniqueId(),fastboard);
    }

    public void unregistered(UUID uuid) {
        FastBoard fastboard = this.fastBoard.remove(uuid);
        if(fastboard!=null){
            fastboard.delete();
        }
    }
}
