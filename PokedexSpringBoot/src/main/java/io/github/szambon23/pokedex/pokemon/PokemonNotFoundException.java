package io.github.szambon23.pokedex.pokemon;

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
