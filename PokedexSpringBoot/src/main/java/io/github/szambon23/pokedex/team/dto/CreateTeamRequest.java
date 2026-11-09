package io.github.szambon23.pokedex.team.dto;

import io.github.szambon23.pokedex.pokemon.Pokemon;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.List;

public record CreateTeamRequest(
        @NotBlank @Size(max = 50) String name,
        boolean speciesClause,
        @Size(max = 6) List<@NotNull @Positive Integer> pokedexNumbers) {
    public CreateTeamRequest {
        pokedexNumbers = pokedexNumbers == null ? List.of() : pokedexNumbers;
    }
}
