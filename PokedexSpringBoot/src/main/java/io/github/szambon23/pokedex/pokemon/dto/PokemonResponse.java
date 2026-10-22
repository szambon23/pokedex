package io.github.szambon23.pokedex.pokemon.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record PokemonResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int pokedexNumber,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String name,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) double heightMeters,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) double weightKg,
        String spriteUrl,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<String> types,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) StatsResponse stats) {
}
