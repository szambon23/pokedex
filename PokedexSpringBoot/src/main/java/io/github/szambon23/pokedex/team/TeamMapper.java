package io.github.szambon23.pokedex.team;

import io.github.szambon23.pokedex.pokemon.Pokemon;
import io.github.szambon23.pokedex.pokemon.PokemonMapper;
import io.github.szambon23.pokedex.team.dto.TeamResponse;
import org.springframework.stereotype.Component;

@Component
public class TeamMapper {

    private final PokemonMapper pokemonMapper;

    public TeamMapper(PokemonMapper pokemonMapper) {
        this.pokemonMapper = pokemonMapper;
    }

    public TeamResponse toResponse(Team team) {
        return new TeamResponse(
                team.getId(),
                team.getName(),
                team.isSpeciesClause(),
                team.getCreatedAt(),
                team.getMembers().stream().map(pokemonMapper::toSummary).toList());
    }
}