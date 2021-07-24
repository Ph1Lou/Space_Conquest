package io.github.ph1lou.space_conquest;

import fr.minuskube.inv.InventoryManager;
import io.github.ph1lou.space_conquest.commands.Stop;
import io.github.ph1lou.space_conquest.commands.TeamChat;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.game.LanguageManager;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.GameRule;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin {

    private InventoryManager invManager;
    private GameManager currentGame;
    private LanguageManager languageManager;

    @Override
    public void onEnable() {
        this.invManager = new InventoryManager(this);
        this.saveDefaultConfig();

        this.invManager.init();
        setWorld();
        this.languageManager = new LanguageManager(this);
        this.currentGame= new GameManager(this);
        Objects.requireNonNull(getCommand("stop")).setExecutor(new Stop(this));
        Objects.requireNonNull(getCommand("t")).setExecutor(new TeamChat(this));

    }

    public void setWorld() {

        World world = Bukkit.getWorlds().get(0);
        world.setWeatherDuration(0);
        world.setThundering(false);
        world.setDifficulty(Difficulty.PEACEFUL);
        world.setGameRule(GameRule.KEEP_INVENTORY,true);
        world.setGameRule(GameRule.DO_FIRE_TICK,false);

        int x = world.getSpawnLocation().getBlockX();
        int z = world.getSpawnLocation().getBlockZ();
        try {
            world.getWorldBorder().reset();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getConfig().getBoolean("default_lobby")) {

            world.setSpawnLocation(x, 151, z);

            for (int i = -16; i <= 16; i++) {

                for (int j = -16; j <= 16; j++) {

                    new Location(world, i + x, 150, j + z).getBlock().setType(Material.BARRIER);
                    new Location(world, i + x, 154, j + z).getBlock().setType(Material.BARRIER);
                }
                for (int j = 151; j < 154; j++) {
                    new Location(world, i + x, j, z - 16).getBlock().setType(Material.BARRIER);
                    new Location(world, i + x, j, z + 16).getBlock().setType(Material.BARRIER);
                    new Location(world, x - 16, j, i + z).getBlock().setType(Material.BARRIER);
                    new Location(world, x + 16, j, i + z).getBlock().setType(Material.BARRIER);
                }
            }
        }
    }

    @Override
    public void onDisable() {
        currentGame.getMapLoader().deleteMap();
    }

    public void setCurrentGame(GameManager gameManager) {
        this.currentGame=gameManager;
    }

    public LanguageManager getLangManager() {
        return this.languageManager;
    }

    public GameManager getCurrentGame() {
        return currentGame;
    }

    public InventoryManager getInvManager() {
        return invManager;
    }

}
