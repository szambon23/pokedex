package io.github.szambon23.pokedex.pokemon.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PokemonSummary(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int pokedexNumber,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String name,
        String spriteUrl,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<String> types
) {
}
