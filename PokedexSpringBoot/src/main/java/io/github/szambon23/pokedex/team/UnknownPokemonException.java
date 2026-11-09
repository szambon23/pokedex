package io.github.szambon23.pokedex.team;

public class UnknownPokemonException extends RuntimeException {
    private final int pokedexNumber;

    public UnknownPokemonException(int pokedexNumber) {
        super("Pokémon #" + pokedexNumber + " does not exist");
        this.pokedexNumber = pokedexNumber;
    }

    public int getPokedexNumber() {
        return pokedexNumber;
    }
}
