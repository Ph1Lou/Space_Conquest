package io.github.ph1lou.space_conquest.enums;

public enum TowerMode {
    MINE(0,"space-conquest.gui.beacon.modes.mine"),
    CONQUEST(1,"space-conquest.gui.beacon.modes.conquest"),
    DEFEND(1,"space-conquest.gui.beacon.modes.defend" ),
    CONQUEST_AND_MINE(2,"space-conquest.gui.beacon.modes.mine_and_conquest"),
    DEFEND_AND_MINE(2,"space-conquest.gui.beacon.modes.mine_and_defend"),
    DEFEND_AND_CONQUEST(2,"space-conquest.gui.beacon.modes.conquest_and_defend"),
    ATTACK(3,"space-conquest.gui.beacon.modes.attack");



    private final int level;

    private final String key;

    TowerMode(int level,String key){
        this.level=level;
        this.key=key;
    }

    public String getKey() {
        return key;
    }

    public int getLevel() {
        return level;
    }
}
