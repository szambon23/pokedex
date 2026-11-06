package io.github.szambon23.pokedex.type.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record TypeResponse(@Schema(requiredMode = Schema.RequiredMode.REQUIRED) String name) {
}
