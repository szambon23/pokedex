package io.github.szambon23.pokedex.importer;

import io.github.szambon23.pokedex.pokeapi.PokeApiPokemon;
import io.github.szambon23.pokedex.pokemon.Pokemon;
import io.github.szambon23.pokedex.pokemon.PokemonRepository;
import io.github.szambon23.pokedex.type.Type;
import io.github.szambon23.pokedex.type.TypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static io.github.szambon23.pokedex.importer.ImportResult.CREATED;
import static io.github.szambon23.pokedex.importer.ImportResult.SKIPPED;

@Service
public class PokemonImporter {
    private PokemonRepository pokemonRepository;
    private TypeRepository typeRepository;

    public PokemonImporter(PokemonRepository pokemonRepository, TypeRepository typeRepository) {
        this.pokemonRepository = pokemonRepository;
        this.typeRepository = typeRepository;
    }

    @Transactional
    public ImportResult importPokemon(PokeApiPokemon source){
        if(pokemonRepository.existsByPokedexNumber(source.id())){
            return SKIPPED;
        }

        List<String> typeNames = source.types().stream()
                .sorted(Comparator.comparingInt(PokeApiPokemon.TypeSlot::slot))
                .map(slot -> slot.type().name())
                .toList();

        Type mainType = typeRepository.findByName(typeNames.getFirst())
                .orElseGet(() -> typeRepository.save(new Type(typeNames.getFirst())));

        if(typeNames.size() > 1){
            Type secondaryType = typeRepository.findByName(typeNames.get(1))
                    .orElseGet(() -> typeRepository.save(new Type(typeNames.get(1))));
        }

        return CREATED;
    }
}
