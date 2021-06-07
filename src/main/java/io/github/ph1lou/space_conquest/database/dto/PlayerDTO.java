package io.github.ph1lou.space_conquest.database.dto;

public class PlayerDTO {

    private final String name;
    private  final boolean captain;
    private final String team;

    public PlayerDTO(String name, boolean captain, String team){
        this.name=name;
        this.captain=captain;
        this.team=team;
    }

    public String getName() {
        return name;
    }

    public boolean isCaptain() {
        return captain;
    }

    public String getTeam() {
        return team;
    }
}
