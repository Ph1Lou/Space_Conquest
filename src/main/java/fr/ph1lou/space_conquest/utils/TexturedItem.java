package fr.ph1lou.space_conquest.utils;

import org.bukkit.Material;


public enum TexturedItem {

    GOLD_RESSOURCE(Material.HONEYCOMB,2),
    IRON_RESSOURCE(Material.STICK,26),
    EMERALD_RESSOURCE(Material.STICK,27),
    CRYING_OBSIDIAN_RESSOURCE(Material.NAUTILUS_SHELL,2),
    DIAMOND_RESSOURCE(Material.HEART_OF_THE_SEA,2),
    GREEN_BUTTON(Material.STICK,22),
    PURPLE_BUTTON(Material.STICK,18),
    BLUE_BUTTON(Material.STICK,19),
    RED_BUTTON(Material.STICK,21),
    YELLOW_BUTTON(Material.STICK,20),
    SWORD_LEVEL_1(Material.WOODEN_SWORD,2),
    SWORD_LEVEL_2(Material.IRON_SWORD,2),
    SWORD_LEVEL_3(Material.DIAMOND_SWORD,2),
    ARROW(Material.ARROW),
    SHARPNESS_UPGRADE(Material.GOLDEN_SWORD,2),
    SHEARS(Material.SHEARS,2),
    LEVITATOR(Material.STICK,25),
    DETONATOR(Material.STICK,24),
    KNOCK_BACK(Material.STICK,28),
    AUTO_WALL(Material.EGG,2),
    PROTECTION_UPGRADE(Material.STICK,23),

    FIRE_CHARGE(Material.FIRE_CHARGE,2),
    REVERSE_ARROW(Material.TIPPED_ARROW,2),
    TOWER_LEVEL_1(Material.WOODEN_PICKAXE,2),
    TOWER_LEVEL_2(Material.GOLDEN_PICKAXE,2),
    TOWER_LEVEL_3(Material.DIAMOND_PICKAXE,2),

    HELMET_WHITE(Material.STICK,2),
    HELMET_ORANGE(Material.STICK,3),
    HELMET_MAGENTA(Material.STICK,4),
    HELMET_LIGHT_BLUE(Material.STICK,5),
    HELMET_YELLOW(Material.STICK,6),
    HELMET_LIME(Material.STICK,7),
    HELMET_PINK(Material.STICK,8),
    HELMET_GRAY(Material.STICK,9),
    HELMET_LIGHT_GRAY(Material.STICK,10),
    HELMET_CYAN(Material.STICK,11),
    HELMET_PURPLE(Material.STICK,12),
    HELMET_BLUE(Material.STICK,13),
    HELMET_BROWN(Material.STICK,14),
    HELMET_GREEN(Material.STICK,15),
    HELMET_RED(Material.STICK,16),
    HELMET_BLACK(Material.STICK,17),

    PROPULSOR(Material.GOLDEN_HOE,2);

    private final Material material;

    private final int customModelData;

    TexturedItem(Material material) {
        this(material, -1);
    }


    TexturedItem(Material material, int customModelData) {
        this.material = material;
        this.customModelData = customModelData;
    }

    public Material getMaterial() {
        return material;
    }
    
    public ItemBuilder getItemBuilder(){
        return new ItemBuilder(this.material)
                .setCustomModelData(this.customModelData);
    }
}
