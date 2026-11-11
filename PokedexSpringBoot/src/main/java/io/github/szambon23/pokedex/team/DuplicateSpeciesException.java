package io.github.szambon23.pokedex.team;

public class DuplicateSpeciesException extends RuntimeException {

    private final int pokedexNumber;

    public DuplicateSpeciesException(int pokedexNumber) {
        super("Pokémon #" + pokedexNumber + " is already in the team and the Species Clause is enabled");
        this.pokedexNumber = pokedexNumber;
    }

    public int getPokedexNumber() {
        return pokedexNumber;
    }
}
