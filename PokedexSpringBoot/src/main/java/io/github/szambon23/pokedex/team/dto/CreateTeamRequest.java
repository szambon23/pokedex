package io.github.szambon23.pokedex.team.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateTeamRequest(
        @NotBlank(message = "must not be blank")
        @Size(max = 50, message = "must be at most 50 characters") String name,
        boolean speciesClause,
        @Size(max = 6, message = "must have at most 6 members")
        List<@NotNull(message = "must not be null") @Positive(message = "must be a positive integer") Integer> pokedexNumbers) {
    public CreateTeamRequest {
        pokedexNumbers = pokedexNumbers == null ? List.of() : pokedexNumbers;
    }
}


