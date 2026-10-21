package io.github.szambon23.pokedex.pokemon;

import org.jspecify.annotations.Nullable;

public class PokemonNotFoundException extends RuntimeException {
    private final int pokedexNumber;

    public PokemonNotFoundException(int pokedexNumber) {

        super("Pokemon #" +  pokedexNumber + " not found");
        this.pokedexNumber = pokedexNumber;
    }

    public int getPokedexNumber() {
        return pokedexNumber;
    }
}
