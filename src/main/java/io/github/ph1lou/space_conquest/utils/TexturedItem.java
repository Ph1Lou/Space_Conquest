package io.github.ph1lou.space_conquest.utils;

import org.bukkit.Material;

import java.util.HashMap;
import java.util.Map;


public enum TexturedItem {

    EXAMPLE(Material.STICK, 0, new Object[][]{{"CustomModelData", 1}});

    private final int durability;

    private final Material material;

    private final Map<String, Object> nbtTags;

    TexturedItem(Material material, int durability) {
        this(material,durability,new Object[][]{{}});
    }


    TexturedItem(Material material, int durability, Object[][] nbt) {
        this.material = material;
        this.durability = durability;
        Map<String, Object> nbtMap = new HashMap<>();
        for (Object[] lines : nbt) {
            nbtMap.put((String) lines[0], lines[1]);
        }
        this.nbtTags = nbtMap;
    }

    public int getDurability() {
        return durability;
    }

    public Material getMaterial() {
        return material;
    }

    public Map<String, Object> getNbtTags() {
        return nbtTags;
    }
}
