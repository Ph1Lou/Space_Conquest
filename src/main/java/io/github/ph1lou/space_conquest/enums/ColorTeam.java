package io.github.ph1lou.space_conquest.enums;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;

public enum ColorTeam {

    BLUE(ChatColor.DARK_BLUE,Material.BLUE_STAINED_GLASS, Color.NAVY, Material.BLUE_WOOL,Material.BLUE_BANNER,"space-conquest.team.color.blue",BarColor.BLUE),
    CYAN(ChatColor.AQUA,Material.CYAN_STAINED_GLASS, Color.AQUA, Material.CYAN_WOOL,Material.CYAN_BANNER,"space-conquest.team.color.cyan",BarColor.BLUE),
    LIGHT_WOOL(ChatColor.BLUE,Material.LIGHT_BLUE_STAINED_GLASS, Color.BLUE, Material.LIGHT_BLUE_WOOL,Material.LIGHT_BLUE_BANNER,"space-conquest.team.color.light_blue",BarColor.BLUE),

    GREEN(ChatColor.DARK_GREEN,Material.GREEN_STAINED_GLASS, Color.GREEN, Material.GREEN_WOOL,Material.GREEN_BANNER,"space-conquest.team.color.green",BarColor.GREEN),
    LIME(ChatColor.GREEN,Material.LIME_STAINED_GLASS, Color.LIME, Material.LIME_WOOL,Material.LIME_BANNER,"space-conquest.team.color.lime",BarColor.GREEN),

    RED(ChatColor.RED, Material.RED_STAINED_GLASS, Color.RED, Material.RED_WOOL,Material.RED_BANNER,"space-conquest.team.color.red",BarColor.RED),

    MAGENTA(ChatColor.DARK_PURPLE,Material.MAGENTA_STAINED_GLASS, Color.FUCHSIA, Material.MAGENTA_WOOL,Material.MAGENTA_BANNER,"space-conquest.team.color.magenta",BarColor.RED),
    PINK(ChatColor.LIGHT_PURPLE, Material.PINK_STAINED_GLASS, Color.TEAL, Material.PINK_WOOL,Material.PINK_BANNER,"space-conquest.team.color.pink",BarColor.PINK),
    PURPLE(ChatColor.DARK_PURPLE,Material.PURPLE_STAINED_GLASS, Color.PURPLE, Material.PURPLE_WOOL,Material.PURPLE_BANNER,"space-conquest.team.color.purple",BarColor.PURPLE),


    YELLOW(ChatColor.YELLOW,Material.YELLOW_STAINED_GLASS, Color.YELLOW, Material.YELLOW_WOOL,Material.YELLOW_BANNER,"space-conquest.team.color.yellow",BarColor.YELLOW),
    ORANGE(ChatColor.GOLD,Material.ORANGE_STAINED_GLASS, Color.ORANGE, Material.ORANGE_WOOL,Material.ORANGE_BANNER,"space-conquest.team.color.orange",BarColor.YELLOW),

    BROWN(ChatColor.GOLD,Material.BROWN_STAINED_GLASS, Color.MAROON, Material.BROWN_WOOL,Material.BROWN_BANNER,"space-conquest.team.color.brown",BarColor.RED),
    BLACK(ChatColor.BLACK,Material.BLACK_STAINED_GLASS, Color.BLACK, Material.BLACK_WOOL,Material.BLACK_BANNER,"space-conquest.team.color.black",BarColor.WHITE),
    GRAY(ChatColor.DARK_GRAY,Material.GRAY_STAINED_GLASS, Color.GRAY, Material.GRAY_WOOL,Material.GRAY_BANNER,"space-conquest.team.color.gray",BarColor.WHITE),
    LIGHT_GRAY(ChatColor.GRAY,Material.LIGHT_GRAY_STAINED_GLASS, Color.SILVER, Material.LIGHT_GRAY_WOOL,Material.LIGHT_GRAY_BANNER,"space-conquest.team.color.light_gray",BarColor.WHITE);

    private final ChatColor chatColor;

    private final Material material;

    private final Material constructionMaterial;

    private final BarColor barColor;

    private final Material banner;

    private final Color color;

    private final String name;

    ColorTeam(ChatColor chatColor, Material material, Color color, Material constructionMaterial, Material banner, String name,BarColor barColor) {
        this.chatColor=chatColor;
        this.material=material;
        this.color = color;
        this.constructionMaterial=constructionMaterial;
        this.banner=banner;
        this.name=name;
        this.barColor=barColor;
    }

    public Material getConstructionMaterial() {
        return this.constructionMaterial;
    }

    public String getName() {
        return this.name;
    }

    public Color getColor() {
        return color;
    }

    public Material getMaterial() {
        return material;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public Material getBanner() {
        return banner;
    }

    public BarColor getBarColor() {
        return barColor;
    }
}

