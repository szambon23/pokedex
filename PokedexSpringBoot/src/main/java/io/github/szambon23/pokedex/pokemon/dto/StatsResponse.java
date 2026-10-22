package io.github.szambon23.pokedex.pokemon.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record StatsResponse(
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int hp,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int attack,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int defense,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int specialAttack,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int specialDefense,
        @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int speed
) {
}
