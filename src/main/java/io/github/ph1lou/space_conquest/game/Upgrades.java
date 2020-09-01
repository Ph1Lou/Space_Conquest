package io.github.ph1lou.space_conquest.game;

public class Upgrades {

    public boolean isIronChestPlate() {
        return ironChestPlate;
    }

    public void setIronChestPlate(boolean ironChestPlate) {
        this.ironChestPlate = ironChestPlate;
    }

    private boolean ironChestPlate=false;

    public boolean isProtection() {
        return protection;
    }

    public void setProtection(boolean protection) {
        this.protection = protection;
    }

    private boolean protection=false;

    public boolean isSharpness() {
        return sharpness;
    }

    public void setSharpness(boolean sharpness) {
        this.sharpness = sharpness;
    }

    private boolean sharpness=false;

    public boolean isPunch() {
        return punch;
    }

    public void setPunch(boolean punch) {
        this.punch = punch;
    }

    private boolean punch=false;

}
