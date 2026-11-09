package io.github.szambon23.pokedex.team.dto;

import io.github.szambon23.pokedex.pokemon.Pokemon;
import io.github.szambon23.pokedex.pokemon.dto.PokemonSummary;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record TeamResponse(UUID id,
                           String name,
                           boolean speciesClause,
                           Instant createdAt,
                           List<PokemonSummary> members) {
}
