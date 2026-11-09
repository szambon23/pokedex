package io.github.szambon23.pokedex.team;

import java.util.UUID;

public class TeamNotFoundException extends RuntimeException {

    private final UUID teamId;

    public TeamNotFoundException(UUID teamId) {
        this.teamId = teamId;
        super("Team #" +  teamId + " not found");
    }

    public UUID getTeamId() {
        return this.teamId;
    }
}
