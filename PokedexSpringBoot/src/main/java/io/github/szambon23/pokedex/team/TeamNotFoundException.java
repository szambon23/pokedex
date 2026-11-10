package io.github.szambon23.pokedex.team;

import java.util.UUID;

public class TeamNotFoundException extends RuntimeException {

    private final UUID teamId;

    public TeamNotFoundException(UUID teamId) {
        super("Team " + teamId + " not found");
        this.teamId = teamId;
    }

    public UUID getTeamId() {
        return teamId;
    }
}
