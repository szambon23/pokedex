package io.github.szambon23.pokedex.pokemon.dto;

import java.util.List;

public record PokemonSummary(
        int pokedexNumber,
        String name,
        String spriteUrl,
        List<String> types
) {
}
