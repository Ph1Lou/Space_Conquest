package fr.ph1lou.space_conquest.enums;

import fr.ph1lou.space_conquest.utils.TexturedItem;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;

public enum ColorTeam {

    BLUE(ChatColor.DARK_BLUE,Material.BLUE_STAINED_GLASS, Color.NAVY, Material.BLUE_WOOL,Material.BLUE_BANNER,"space-conquest.team.color.blue",BarColor.BLUE, TexturedItem.HELMET_BLUE),
    CYAN(ChatColor.AQUA,Material.CYAN_STAINED_GLASS, Color.AQUA, Material.CYAN_WOOL,Material.CYAN_BANNER,"space-conquest.team.color.cyan",BarColor.BLUE,TexturedItem.HELMET_CYAN),
    LIGHT_WOOL(ChatColor.BLUE,Material.LIGHT_BLUE_STAINED_GLASS, Color.BLUE, Material.LIGHT_BLUE_WOOL,Material.LIGHT_BLUE_BANNER,"space-conquest.team.color.light_blue",BarColor.BLUE,TexturedItem.HELMET_LIGHT_BLUE),

    GREEN(ChatColor.DARK_GREEN,Material.GREEN_STAINED_GLASS, Color.GREEN, Material.GREEN_WOOL,Material.GREEN_BANNER,"space-conquest.team.color.green",BarColor.GREEN,TexturedItem.HELMET_GREEN),
    LIME(ChatColor.GREEN,Material.LIME_STAINED_GLASS, Color.LIME, Material.LIME_WOOL,Material.LIME_BANNER,"space-conquest.team.color.lime",BarColor.GREEN,TexturedItem.HELMET_LIME),

    RED(ChatColor.RED, Material.RED_STAINED_GLASS, Color.RED, Material.RED_WOOL,Material.RED_BANNER,"space-conquest.team.color.red",BarColor.RED,TexturedItem.HELMET_RED),

    MAGENTA(ChatColor.DARK_PURPLE,Material.MAGENTA_STAINED_GLASS, Color.FUCHSIA, Material.MAGENTA_WOOL,Material.MAGENTA_BANNER,"space-conquest.team.color.magenta",BarColor.RED,TexturedItem.HELMET_MAGENTA),
    PINK(ChatColor.LIGHT_PURPLE, Material.PINK_STAINED_GLASS, Color.TEAL, Material.PINK_WOOL,Material.PINK_BANNER,"space-conquest.team.color.pink",BarColor.PINK,TexturedItem.HELMET_PINK),
    PURPLE(ChatColor.DARK_PURPLE,Material.PURPLE_STAINED_GLASS, Color.PURPLE, Material.PURPLE_WOOL,Material.PURPLE_BANNER,"space-conquest.team.color.purple",BarColor.PURPLE,TexturedItem.HELMET_PURPLE),


    YELLOW(ChatColor.YELLOW,Material.YELLOW_STAINED_GLASS, Color.YELLOW, Material.YELLOW_WOOL,Material.YELLOW_BANNER,"space-conquest.team.color.yellow",BarColor.YELLOW,TexturedItem.HELMET_YELLOW),
    ORANGE(ChatColor.GOLD,Material.ORANGE_STAINED_GLASS, Color.ORANGE, Material.ORANGE_WOOL,Material.ORANGE_BANNER,"space-conquest.team.color.orange",BarColor.YELLOW,TexturedItem.HELMET_ORANGE),

    BROWN(ChatColor.GOLD,Material.BROWN_STAINED_GLASS, Color.MAROON, Material.BROWN_WOOL,Material.BROWN_BANNER,"space-conquest.team.color.brown",BarColor.RED,TexturedItem.HELMET_BROWN),
    BLACK(ChatColor.BLACK,Material.BLACK_STAINED_GLASS, Color.BLACK, Material.BLACK_WOOL,Material.BLACK_BANNER,"space-conquest.team.color.black",BarColor.WHITE,TexturedItem.HELMET_BLACK),
    GRAY(ChatColor.DARK_GRAY,Material.GRAY_STAINED_GLASS, Color.GRAY, Material.GRAY_WOOL,Material.GRAY_BANNER,"space-conquest.team.color.gray",BarColor.WHITE,TexturedItem.HELMET_GRAY),
    LIGHT_GRAY(ChatColor.GRAY,Material.LIGHT_GRAY_STAINED_GLASS, Color.SILVER, Material.LIGHT_GRAY_WOOL,Material.LIGHT_GRAY_BANNER,"space-conquest.team.color.light_gray",BarColor.WHITE,TexturedItem.HELMET_LIGHT_GRAY);

    private final ChatColor chatColor;

    private final Material material;

    private final Material constructionMaterial;

    private final BarColor barColor;

    private final Material banner;

    private final Color color;

    private final String name;



    private final TexturedItem helmet;

    ColorTeam(ChatColor chatColor, Material material, Color color, Material constructionMaterial, Material banner, String name, BarColor barColor, TexturedItem helmet) {
        this.chatColor=chatColor;
        this.material=material;
        this.color = color;
        this.constructionMaterial=constructionMaterial;
        this.banner=banner;
        this.name=name;
        this.barColor=barColor;
        this.helmet = helmet;
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

    public TexturedItem getHelmet() {
        return helmet;
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

