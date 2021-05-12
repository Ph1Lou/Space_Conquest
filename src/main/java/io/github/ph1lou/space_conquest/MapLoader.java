package io.github.ph1lou.space_conquest;

import io.github.ph1lou.space_conquest.game.Area;
import io.github.ph1lou.space_conquest.game.GameManager;
import io.github.ph1lou.space_conquest.game.Team;
import io.github.ph1lou.space_conquest.utils.ItemBuilder;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.*;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;

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
        int radius = 100+game.getZoneNumber()*3;
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

            NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, game.translate("space-conquest.team.leader",team.getColorTeam().getChatColor()+team.getName()));

            npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, new ItemStack(team.getColorTeam().getMaterial()));
            npc.getOrAddTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, new ItemBuilder(Material.LEATHER_LEGGINGS).setColor(team.getColorTeam().getColor()).build());
            npc.getOrAddTrait(SkinTrait.class).setSkinPersistent("e","CavlQkPktJfQShsQA0QJz1ADzwvG5tyE6XiPGbSNwbQI7TkUDRGiqZNvZGsEhc2B2Wo4eGPSaoJDfsHahkz9ggbZGFGBnLFIriFHmbGBucR/RqXEegU+eanqHzkLXfFe1pcx836nv1Z7XknwkDdCxj2pZXnNCtBdx7orKgrzQeaJrcOwAxr8bhfG83Cu7nC0kIstS00yKZPkiLl/cEvWD1PRUeHZU6hmoXrdP1spPcTV8/ADsT8Q03WLcMPoSzebIcUv4MVG/m1qV/ukPw0dLSCaZ/rCOdvzTsOssLrd/i3woVO60HOCkqpuGNK3kojFzfFbgKmu+Yya0G6MlGV0jSY/5YjE45jUQxD+Yq7TFulJap81peQggmZrg53rA+scWfdOPTR4Xm0gzskI/2ywpqvSLllg/5SOYHMNJYh5PajZtUOeS1/64Dh/72AMh+E1AsMS0Bn4IyPTQAY2t8T9uHm3GXmB+CVGAnri9TkTs5cXIDOgX0umw8OGR2rB/CCYHy07LiZcFdx8PivlPFzYPrrB8C76kO24+Lkzib0awIl02yjEn0FPrW1tMJOIKz97/DP5CVaiQQsk0UfSv9I8nsfCd3E6pmGKyW2P8Q4X65VShX5/C+qkH11J16xTdgFSK0VhhvurIh/irGhpF7nzDIbbdBh0095WiLtbXlI3pnY=","ewogICJ0aW1lc3RhbXAiIDogMTYwNjEzNzA4MDM4NywKICAicHJvZmlsZUlkIiA6ICIyYzEwNjRmY2Q5MTc0MjgyODRlM2JmN2ZhYTdlM2UxYSIsCiAgInByb2ZpbGVOYW1lIiA6ICJOYWVtZSIsCiAgInNpZ25hdHVyZVJlcXVpcmVkIiA6IHRydWUsCiAgInRleHR1cmVzIiA6IHsKICAgICJTS0lOIiA6IHsKICAgICAgInVybCIgOiAiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS8zNjc2NTBiMGQ1YzBiZTc4NTk2OTJjYzZmODU5NTNjMDc4ZjBjYzM1OTlhOGI4OGZjMmM1NDUyNDk4MGE0ZDExIgogICAgfQogIH0KfQ==");

            npc.spawn(location.clone());
            team.setNpc(npc);


            i++;
        }

        int k=11;
        i=game.getZoneNumber();
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

        this.generateCircle(world.getSpawnLocation(),game.getCenterSize(),Material.GLOWSTONE,false,true);
    }

    private void generateCircle(Location location, int size, Material material){
        this.generateCircle(location, size, material, false);
    }

    private Area generateCircle(Location location, int size, Material material, boolean isBase){
        return generateCircle( location, size, material,isBase, false);
    }



    public Area generateCircle(Location location, int size, Material material, boolean isBase, boolean isMiddle){

        World world = game.getWorld();
        Area area = new Area(isBase,location.clone(),Material.CRYING_OBSIDIAN);

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
