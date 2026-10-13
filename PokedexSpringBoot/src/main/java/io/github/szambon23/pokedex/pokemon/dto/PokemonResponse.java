package io.github.szambon23.pokedex.pokemon.dto;

import io.github.szambon23.pokedex.type.Type;

import java.util.List;

public record PokemonResponse(
        int pokedexNumber,
        String name,
        double heightMeters,
        double weightKg,
        String spriteUrl,
        List<String> types,
        StatsResponse stats) {
}
