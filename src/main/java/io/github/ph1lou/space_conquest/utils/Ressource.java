package io.github.ph1lou.space_conquest.utils;

import java.util.Arrays;
import java.util.Optional;

public enum Ressource {
    IRON(TexturedItem.IRON_RESSOURCE,"space-conquest.gui.ressources.iron"),
    GOLD(TexturedItem.GOLD_RESSOURCE,"space-conquest.gui.ressources.gold"),
    DIAMOND(TexturedItem.DIAMOND_RESSOURCE,"space-conquest.gui.ressources.diamond"),
    EMERALD(TexturedItem.EMERALD_RESSOURCE,"space-conquest.gui.ressources.emerald"),
    CRYING_OBSIDIAN(TexturedItem.CRYING_OBSIDIAN_RESSOURCE,"space-conquest.gui.ressources.crying_obsidian");



    private final TexturedItem item;
    private final String name;

    Ressource(TexturedItem item, String name){
        this.item=item;
        this.name=name;
    }

    public TexturedItem getItem() {
        return item;
    }

    public String getName() {
        return name;
    }


    public static Optional<Ressource> getRessource(TexturedItem item){
        return Arrays.stream(Ressource.values())
                .filter(ressource -> ressource.getItem().equals(item))
                .findFirst();
    }
}
