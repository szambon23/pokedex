package io.github.szambon23.pokedex.pokemon.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record PokemonFilter(
        @Schema(description = "Part of the name, case-insensitive", example = "char") String name,
        @Schema(description = "Exact type name, case-insensitive", example = "fire") String type) {
}