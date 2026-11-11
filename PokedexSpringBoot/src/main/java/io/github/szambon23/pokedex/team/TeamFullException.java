package io.github.szambon23.pokedex.team;

public class TeamFullException extends RuntimeException {

    private final int maxMembers;

    public TeamFullException(int maxMembers) {
        super("A team can have at most " + maxMembers + " Pokémon");
        this.maxMembers = maxMembers;
    }

    public int getMaxMembers() {
        return maxMembers;
    }
}