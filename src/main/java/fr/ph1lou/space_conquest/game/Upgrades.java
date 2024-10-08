package fr.ph1lou.space_conquest.game;

public class Upgrades {

    private final Team team;

    private int power=0;

    private boolean middleTower = false;

    private int tower=0;

    private int chestPlate =0;

    private int protection=0;

    private int sharpness=0;

    public Upgrades(Team team){
        this.team=team;
    }


    public int getChestPlate() {
        return chestPlate;
    }

    public void setChestPlate(int chestPlate) {
        this.chestPlate = chestPlate;
        this.team.updateNpcChestPlate();
    }

    public int isProtection() {
        return protection;
    }

    public void setProtection(int protection) {
        this.protection = protection;
    }

    public int isSharpness() {
        return sharpness;
    }

    public void setSharpness(int sharpness) {
        this.sharpness = sharpness;
    }

    public int isPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public boolean isMiddleTower() {
        return middleTower;
    }

    public void setMiddleTower(boolean middleTower) {
        this.middleTower = middleTower;
    }

    public int getTower() {
        return tower;
    }

    public void setTower(int tower) {
        this.tower = tower;
    }


}
