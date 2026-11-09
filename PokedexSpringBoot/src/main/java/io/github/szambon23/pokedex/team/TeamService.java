package io.github.szambon23.pokedex.team;

import io.github.szambon23.pokedex.pokemon.*;
import io.github.szambon23.pokedex.team.dto.CreateTeamRequest;
import io.github.szambon23.pokedex.team.dto.TeamResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class TeamService {

    private final PokemonRepository pokemonRepository;
    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    public TeamService(PokemonRepository pokemonRepository, TeamRepository teamRepository, TeamMapper teamMapper) {
        this.pokemonRepository = pokemonRepository;
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
    }

    @Transactional
    public TeamResponse create(CreateTeamRequest request) {
        Team team = new Team(request.name(), request.speciesClause());
        for (Integer pokedexNumber : request.pokedexNumbers()) {
            Pokemon pokemon = pokemonRepository.findByPokedexNumber(pokedexNumber)
                    .orElseThrow(() -> new UnknownPokemonException(pokedexNumber));
            team.addMember(pokemon);
        }
        return teamMapper.toResponse(teamRepository.save(team));
    }

    public TeamResponse findById(UUID id) {
        return teamRepository.findById(id)
                .map(teamMapper::toResponse)
                .orElseThrow(() -> new TeamNotFoundException(id));
    }
}