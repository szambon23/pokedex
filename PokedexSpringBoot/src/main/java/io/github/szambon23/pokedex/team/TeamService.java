package io.github.szambon23.pokedex.team;

import io.github.szambon23.pokedex.pokemon.Pokemon;
import io.github.szambon23.pokedex.pokemon.PokemonRepository;
import io.github.szambon23.pokedex.team.dto.TeamRequest;
import io.github.szambon23.pokedex.team.dto.TeamResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
    public TeamResponse create(TeamRequest request) {
        Team team = new Team(request.name(), request.speciesClause());
        findMembers(request.pokedexNumbers()).forEach(team::addMember);
        return teamMapper.toResponse(teamRepository.save(team));
    }

    public TeamResponse findById(UUID id) {
        return teamRepository.findById(id)
                .map(teamMapper::toResponse)
                .orElseThrow(() -> new TeamNotFoundException(id));
    }

    @Transactional
    public TeamResponse update(UUID id, TeamRequest request) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new TeamNotFoundException(id));
        team.update(request.name(), request.speciesClause(), findMembers(request.pokedexNumbers()));
        return teamMapper.toResponse(team);
    }

    private List<Pokemon> findMembers(List<Integer> pokedexNumbers) {
        return pokedexNumbers.stream()
                .map(pokedexNumber -> pokemonRepository.findByPokedexNumber(pokedexNumber)
                        .orElseThrow(() -> new UnknownPokemonException(pokedexNumber)))
                .toList();
    }
}