package fr.ph1lou.space_conquest.enums;

public enum SpecialItem {
    PROPULSION("propulsion"),
    LEVITATION("levitation"),
    EXPLOSION("explosion"),
    NO_GRAVITY("no-gravity"),
    FIRE_CHARGE("fire-charge");

    private final String propulsion;

    SpecialItem(String propulsion) {
        this.propulsion = propulsion;
    }

    public String getKey() {
        return propulsion;
    }
}
