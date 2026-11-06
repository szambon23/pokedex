package io.github.szambon23.pokedex.pokemon;

import io.github.szambon23.pokedex.pokemon.dto.PokemonFilter;
import io.github.szambon23.pokedex.pokemon.dto.PokemonResponse;
import io.github.szambon23.pokedex.pokemon.dto.PokemonSummary;
import io.github.szambon23.pokedex.shared.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PokemonService {
    private final PokemonRepository pokemonRepository;
    private final PokemonMapper pokemonMapper;

    public PokemonService(PokemonRepository pokemonRepository, PokemonMapper pokemonMapper) {
        this.pokemonRepository = pokemonRepository;
        this.pokemonMapper = pokemonMapper;
    }

    public PokemonResponse findByPokedexNumber(int pokedexNumber){
        return pokemonRepository.findByPokedexNumber(pokedexNumber)
                .map(pokemonMapper::toResponse)
                .orElseThrow(() -> new PokemonNotFoundException(pokedexNumber));
    }

    public PageResponse<PokemonSummary> findAll(PokemonFilter filter, Pageable pageable) {
        Specification<Pokemon> spec = Specification.unrestricted();

        if (filter.name() != null && !filter.name().isBlank()) {
            spec = spec.and(PokemonSpecifications.nameContains(filter.name()));
        }
        if (filter.type() != null && !filter.type().isBlank()) {
            spec = spec.and(PokemonSpecifications.hasType(filter.type()));
        }

        Pageable entityPageable = PokemonSortProperties.toEntityPageable(pageable);   // ← nuevo

        Page<PokemonSummary> page = pokemonRepository.findAll(spec, entityPageable)  // ← entityPageable, no pageable
                .map(pokemonMapper::toSummary);
        return PageResponse.from(page);
    }

}
