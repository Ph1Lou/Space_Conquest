package io.github.ph1lou.space_conquest;

import io.github.ph1lou.space_conquest.game.Area;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.game.Team;
import io.github.ph1lou.space_conquest.utils.ItemBuilder;
import net.jitse.npclib.NPCLib;
import net.jitse.npclib.api.NPC;
import net.jitse.npclib.api.skin.MineSkinFetcher;
import net.jitse.npclib.api.state.NPCSlot;
import org.bukkit.*;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class MapLoader {

    final GameManager game;

    public MapLoader(GameManager game){
        this.game = game;
    }

    public World generateMap(){

        WorldCreator wc = new WorldCreator("game");
        wc.environment(World.Environment.NORMAL);
        wc.type(WorldType.NORMAL);
        wc.generator(new EmptyChunkGenerator());
        return wc.createWorld();
    }

    public void generateTeamCamp(){

        World world = game.getWorld();

        int teamSize = game.getTeams().size();
        int i=0;
        int radius = 100+teamSize*3;
        int height = 50;
        int baseSize=9;

        for (Team team:game.getTeams()) {
            double x = (Math.round(radius * Math.cos(i*2*Math.PI/teamSize) + world.getSpawnLocation().getX()));
            double z = (Math.round(radius * Math.sin(i*2*Math.PI/teamSize) + world.getSpawnLocation().getZ()));
            Location location = new Location(world,x,world.getSpawnLocation().getBlockY()+height,z);
            team.setBase(generateCircle(location.clone(),baseSize,team.getColorTeam().getMaterial(),true));
            team.getBase().setOwnerTeam(team);
            location.setY(location.getY()+1);
            location.setX(location.getBlockX()+2);
            team.setSpawn(location.clone());
            location.setX(location.getBlockX()-2);

            MineSkinFetcher.fetchSkinFromIdAsync(1980939648, skin -> {
                NPCLib npcLib = game.getMain().getLibrary();
                NPC npc = npcLib.createNPC(Collections.singletonList("Commandant Équipe "+team.getColorTeam().getChatColor()+team.getName()));
                npc.setLocation(location.clone());
                npc.setSkin(skin);
                npc.setItem(NPCSlot.HELMET,new ItemStack(team.getColorTeam().getMaterial()));
                npc.setItem(NPCSlot.LEGGINGS,new ItemBuilder(Material.LEATHER_LEGGINGS).setColor(team.getColorTeam().getColor()).build());

                npc.create();

                Bukkit.getScheduler().runTask(game.getMain(), () ->{
                    for(Player p:Bukkit.getOnlinePlayers()){
                        npc.show(p);
                    }
                });
                team.setNpc(npc);
            });


            i++;
        }

        int k=11;
        i*=2;
        radius-=30-k;
        height-=30-k;

        while (k!=3){

            for(int j=0;j<i;j++){
                double x = (Math.round(radius * Math.cos(j*2*Math.PI/i) + world.getSpawnLocation().getX()));
                double z = (Math.round(radius * Math.sin(j*2*Math.PI/i) + world.getSpawnLocation().getZ()));
                generateCircle(new Location(world,x,world.getSpawnLocation().getBlockY()+height,z),k,Material.WHITE_STAINED_GLASS);
            }
            k-=2;
            i--;
            radius-=30-k;
            height-=30-k;
        }

        generateCircle(world.getSpawnLocation(),7,Material.GLOWSTONE,false,true);
    }

    public void generateCircle(Location location, int size, Material material){
        generateCircle(location, size, material, false);
    }

    public Area generateCircle(Location location, int size, Material material, boolean isBase){
        return generateCircle( location, size, material,isBase, false);
    }



    public Area generateCircle(Location location, int size, Material material, boolean isBase, boolean isMiddle){

        World world = game.getWorld();
        Area area = new Area(game,isBase,location.clone());

        for(int i=-size/2;i<Math.ceil(size/2f);i++){
            for(int j=-size/2;j<Math.ceil(size/2f);j++){
                if(Math.pow(i,2)+Math.pow(j,2)<=Math.pow(size/2f,2)){
                    Location location1 = new Location(world,i+location.getBlockX(),location.getBlockY(),j+location.getBlockZ());
                    area.getBlocks().add(location1);
                    world.getBlockAt(location1).setType(material);
                }
            }
        }

        if(isMiddle){
            Location location1 = new Location(world,location.getBlockX(),location.getBlockY()+1,location.getBlockZ());
            area.setGeneratorType(Material.CRYING_OBSIDIAN);
            world.getBlockAt(location1).setType(area.getGeneratorType());
            location1.setY(location1.getBlockY()+1);
            world.getBlockAt(location1).setType(Material.BEACON);
        }
        else if(!isBase){
            Location location1 = new Location(world,location.getBlockX(),location.getBlockY()+1,location.getBlockZ());

            Material generatorType;

            if(size>=11){
                generatorType=Material.IRON_BLOCK;
            }
            else if(size>=9){
                generatorType=Material.GOLD_BLOCK;
            }
            else if(size>=7){
                generatorType=Material.DIAMOND_BLOCK;
            }
            else {
                generatorType=Material.EMERALD_BLOCK;
            }



            area.setGeneratorType(generatorType);
            world.getBlockAt(location1).setType(area.getGeneratorType());
            location1.setY(location1.getBlockY()+1);
            world.getBlockAt(location1).setType(Material.BEACON);


        }
        else {
            location.setY(location.getBlockY()-1);
            world.getBlockAt(location).setType(Material.BEACON);
        }
        game.getAreas().add(area);

        return area;
    }




    public void deleteMap() {

        if(game.getWorld()==null) return;
        try {
            Bukkit.unloadWorld(game.getWorld(), false);
            FileUtils.deleteDirectory(new File(Bukkit.getWorldContainer() + File.separator + game.getWorld().getName()));
            game.setWorld(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
