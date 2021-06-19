package io.github.ph1lou.space_conquest.utils;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;


public enum TexturedItem {

    GOLD_RESSOURCE(Material.HONEYCOMB,new Object[][]{{"CustomModelData", 2}}),
    IRON_RESSOURCE(Material.MUSIC_DISC_MELLOHI,new Object[][]{{"CustomModelData", 2}}),
    EMERALD_RESSOURCE(Material.MUSIC_DISC_STAL,new Object[][]{{"CustomModelData", 2}}),
    CRYING_OBSIDIAN_RESSOURCE(Material.NAUTILUS_SHELL,new Object[][]{{"CustomModelData", 2}}),
    DIAMOND_RESSOURCE(Material.HEART_OF_THE_SEA,new Object[][]{{"CustomModelData", 2}}),
    GREEN_BUTTON(Material.MUSIC_DISC_PIGSTEP,new Object[][]{{"CustomModelData", 2}}),
    PURPLE_BUTTON(Material.MUSIC_DISC_11,new Object[][]{{"CustomModelData", 2}}),
    BLUE_BUTTON(Material.MUSIC_DISC_WARD,new Object[][]{{"CustomModelData", 2}}),
    RED_BUTTON(Material.MUSIC_DISC_STRAD,new Object[][]{{"CustomModelData", 2}}),
    YELLOW_BUTTON(Material.MUSIC_DISC_WAIT,new Object[][]{{"CustomModelData", 2}}),
    SWORD_LEVEL_1(Material.WOODEN_SWORD,new Object[][]{{"CustomModelData", 2}}),
    SWORD_LEVEL_2(Material.IRON_SWORD,new Object[][]{{"CustomModelData", 2}}),
    SWORD_LEVEL_3(Material.DIAMOND_SWORD,new Object[][]{{"CustomModelData", 2}}),
    ARROW(Material.ARROW,new Object[][]{{"CustomModelData", 2}}),
    SHARPNESS_UPGRADE(Material.GOLDEN_SWORD,new Object[][]{{"CustomModelData", 2}}),
    SHEARS(Material.SHEARS,new Object[][]{{"CustomModelData", 2}}),
    LEVITATOR(Material.MUSIC_DISC_MALL,new Object[][]{{"CustomModelData", 2}}),
    DETONATOR(Material.MUSIC_DISC_FAR,new Object[][]{{"CustomModelData", 2}}),
    KNOCK_BACK(Material.STICK,new Object[][]{{"CustomModelData", 2}}),
    AUTO_WALL(Material.EGG,new Object[][]{{"CustomModelData", 2}}),
    PROTECTION_UPGRADE(Material.MUSIC_DISC_CHIRP,new Object[][]{{"CustomModelData", 2}}),
    FIRE_CHARGE(Material.FIRE_CHARGE,new Object[][]{{"CustomModelData", 2}}),
    REVERSE_ARROW(Material.TIPPED_ARROW,new Object[][]{{"CustomModelData", 2}}),
    TOWER_LEVEL_1(Material.WOODEN_PICKAXE,new Object[][]{{"CustomModelData", 2}}),
    TOWER_LEVEL_2(Material.GOLDEN_PICKAXE,new Object[][]{{"CustomModelData", 2}}),
    TOWER_LEVEL_3(Material.DIAMOND_PICKAXE,new Object[][]{{"CustomModelData", 2}}),

    HELMET_WHITE(Material.MUSIC_DISC_BLOCKS,new Object[][]{{"CustomModelData", 2}}),
    HELMET_ORANGE(Material.MUSIC_DISC_BLOCKS,new Object[][]{{"CustomModelData", 3}}),
    HELMET_MAGENTA(Material.MUSIC_DISC_BLOCKS,new Object[][]{{"CustomModelData", 4}}),
    HELMET_LIGHT_BLUE(Material.MUSIC_DISC_BLOCKS,new Object[][]{{"CustomModelData", 5}}),
    HELMET_YELLOW(Material.MUSIC_DISC_BLOCKS,new Object[][]{{"CustomModelData", 6}}),
    HELMET_LIME(Material.MUSIC_DISC_BLOCKS,new Object[][]{{"CustomModelData", 7}}),
    HELMET_PINK(Material.MUSIC_DISC_BLOCKS,new Object[][]{{"CustomModelData", 8}}),
    HELMET_GRAY(Material.MUSIC_DISC_BLOCKS,new Object[][]{{"CustomModelData", 9}}),
    HELMET_LIGHT_GRAY(Material.MUSIC_DISC_BLOCKS,new Object[][]{{"CustomModelData", 10}}),
    HELMET_CYAN(Material.MUSIC_DISC_BLOCKS,new Object[][]{{"CustomModelData", 11}}),
    HELMET_PURPLE(Material.MUSIC_DISC_BLOCKS,new Object[][]{{"CustomModelData", 12}}),
    HELMET_BLUE(Material.MUSIC_DISC_BLOCKS,new Object[][]{{"CustomModelData", 13}}),
    HELMET_BROWN(Material.MUSIC_DISC_BLOCKS,new Object[][]{{"CustomModelData", 14}}),
    HELMET_GREEN(Material.MUSIC_DISC_BLOCKS,new Object[][]{{"CustomModelData", 15}}),
    HELMET_RED(Material.MUSIC_DISC_BLOCKS,new Object[][]{{"CustomModelData", 16}}),
    HELMET_BLACK(Material.MUSIC_DISC_BLOCKS,new Object[][]{{"CustomModelData", 17}}),

    PROPULSOR(Material.GOLDEN_HOE,new Object[][]{{"CustomModelData", 2}});

    private final Material material;

    private final Map<String, Integer> nbtTags;

    TexturedItem(Material material) {
        this(material,new Object[][]{{}});
    }


    TexturedItem(Material material, Object[][] nbt) {
        this.material = material;
        Map<String, Integer> nbtMap = new HashMap<>();
        for (Object[] lines : nbt) {
            nbtMap.put((String) lines[0], (Integer) lines[1]);
        }
        this.nbtTags = nbtMap;
    }

    public Material getMaterial() {
        return material;
    }

    public Map<String, Integer> getNbtTags() {
        return nbtTags;
    }

    public ItemBuilder getItemBuilder(){
        ItemBuilder itemBuilder = new ItemBuilder(this.material);

        this.nbtTags.forEach(itemBuilder::addNBTTag);
        return itemBuilder;
    }
}
