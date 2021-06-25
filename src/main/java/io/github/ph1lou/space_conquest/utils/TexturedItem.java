package io.github.ph1lou.space_conquest.utils;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;


public enum TexturedItem {

    GOLD_RESSOURCE(Material.HONEYCOMB,new Object[][]{{"CustomModelData", 2}}),
    IRON_RESSOURCE(Material.STICK,new Object[][]{{"CustomModelData", 26}}),
    EMERALD_RESSOURCE(Material.STICK,new Object[][]{{"CustomModelData", 27}}),
    CRYING_OBSIDIAN_RESSOURCE(Material.NAUTILUS_SHELL,new Object[][]{{"CustomModelData", 2}}),
    DIAMOND_RESSOURCE(Material.HEART_OF_THE_SEA,new Object[][]{{"CustomModelData", 2}}),
    GREEN_BUTTON(Material.STICK,new Object[][]{{"CustomModelData", 22}}),
    PURPLE_BUTTON(Material.STICK,new Object[][]{{"CustomModelData", 18}}),
    BLUE_BUTTON(Material.STICK,new Object[][]{{"CustomModelData", 19}}),
    RED_BUTTON(Material.STICK,new Object[][]{{"CustomModelData", 21}}),
    YELLOW_BUTTON(Material.STICK,new Object[][]{{"CustomModelData", 20}}),
    SWORD_LEVEL_1(Material.WOODEN_SWORD,new Object[][]{{"CustomModelData", 2}}),
    SWORD_LEVEL_2(Material.IRON_SWORD,new Object[][]{{"CustomModelData", 2}}),
    SWORD_LEVEL_3(Material.DIAMOND_SWORD,new Object[][]{{"CustomModelData", 2}}),
    ARROW(Material.ARROW),
    SHARPNESS_UPGRADE(Material.GOLDEN_SWORD,new Object[][]{{"CustomModelData", 2}}),
    SHEARS(Material.SHEARS,new Object[][]{{"CustomModelData", 2}}),
    LEVITATOR(Material.STICK,new Object[][]{{"CustomModelData", 25}}),
    DETONATOR(Material.STICK,new Object[][]{{"CustomModelData", 24}}),
    KNOCK_BACK(Material.STICK,new Object[][]{{"CustomModelData", 28}}),
    AUTO_WALL(Material.EGG,new Object[][]{{"CustomModelData", 2}}),
    PROTECTION_UPGRADE(Material.STICK,new Object[][]{{"CustomModelData", 23}}),

    FIRE_CHARGE(Material.FIRE_CHARGE,new Object[][]{{"CustomModelData", 2}}),
    REVERSE_ARROW(Material.TIPPED_ARROW,new Object[][]{{"CustomModelData", 2}}),
    TOWER_LEVEL_1(Material.WOODEN_PICKAXE,new Object[][]{{"CustomModelData", 2}}),
    TOWER_LEVEL_2(Material.GOLDEN_PICKAXE,new Object[][]{{"CustomModelData", 2}}),
    TOWER_LEVEL_3(Material.DIAMOND_PICKAXE,new Object[][]{{"CustomModelData", 2}}),

    HELMET_WHITE(Material.STICK,new Object[][]{{"CustomModelData", 2}}),
    HELMET_ORANGE(Material.STICK,new Object[][]{{"CustomModelData", 3}}),
    HELMET_MAGENTA(Material.STICK,new Object[][]{{"CustomModelData", 4}}),
    HELMET_LIGHT_BLUE(Material.STICK,new Object[][]{{"CustomModelData", 5}}),
    HELMET_YELLOW(Material.STICK,new Object[][]{{"CustomModelData", 6}}),
    HELMET_LIME(Material.STICK,new Object[][]{{"CustomModelData", 7}}),
    HELMET_PINK(Material.STICK,new Object[][]{{"CustomModelData", 8}}),
    HELMET_GRAY(Material.STICK,new Object[][]{{"CustomModelData", 9}}),
    HELMET_LIGHT_GRAY(Material.STICK,new Object[][]{{"CustomModelData", 10}}),
    HELMET_CYAN(Material.STICK,new Object[][]{{"CustomModelData", 11}}),
    HELMET_PURPLE(Material.STICK,new Object[][]{{"CustomModelData", 12}}),
    HELMET_BLUE(Material.STICK,new Object[][]{{"CustomModelData", 13}}),
    HELMET_BROWN(Material.STICK,new Object[][]{{"CustomModelData", 14}}),
    HELMET_GREEN(Material.STICK,new Object[][]{{"CustomModelData", 15}}),
    HELMET_RED(Material.STICK,new Object[][]{{"CustomModelData", 16}}),
    HELMET_BLACK(Material.STICK,new Object[][]{{"CustomModelData", 17}}),

    PROPULSOR(Material.GOLDEN_HOE,new Object[][]{{"CustomModelData", 2}});

    private final Material material;

    private final Map<String, Integer> nbtTags;

    TexturedItem(Material material) {
        this(material,new Object[][]{});
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
