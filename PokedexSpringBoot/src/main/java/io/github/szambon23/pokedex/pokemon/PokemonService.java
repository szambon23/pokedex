package io.github.szambon23.pokedex.pokemon;

import io.github.szambon23.pokedex.pokemon.dto.PokemonResponse;
import io.github.szambon23.pokedex.pokemon.dto.PokemonSummary;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import io.github.szambon23.pokedex.shared.PageResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class PokemonService {
    private final PokemonRepository pokemonRepository;
    private final PokemonMapper pokemonMapper;

    public PokemonService(PokemonRepository pokemonRepository, PokemonMapper pokemonMapper) {
        this.pokemonRepository = pokemonRepository;
        this.pokemonMapper = pokemonMapper;
    }

    public Optional<PokemonResponse> findByPokedexNumber(int pokedexNumber){
        return pokemonRepository.findByPokedexNumber(pokedexNumber)
                .map(pokemonMapper::toResponse);
    }

    public PageResponse<PokemonSummary> findAll(Pageable pageable){
        Page<Pokemon> pokemons = pokemonRepository.findAll(pageable);
        Page<PokemonSummary> pokemonsSummary = pokemons.map(pokemonMapper::toSummary);

        return PageResponse.from(pokemonsSummary);
    }

}
