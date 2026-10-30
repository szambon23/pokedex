package io.github.szambon23.pokedex.importer;

import io.github.szambon23.pokedex.pokeapi.PokeApiPokemon;
import io.github.szambon23.pokedex.pokemon.BaseStats;
import io.github.szambon23.pokedex.pokemon.Pokemon;
import io.github.szambon23.pokedex.pokemon.PokemonRepository;
import io.github.szambon23.pokedex.type.Type;
import io.github.szambon23.pokedex.type.TypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Map;

import static io.github.szambon23.pokedex.importer.ImportResult.CREATED;
import static io.github.szambon23.pokedex.importer.ImportResult.SKIPPED;

@Service
public class PokemonImporter {
    private final PokemonRepository pokemonRepository;
    private final TypeRepository typeRepository;

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

        int pokedexNumber = source.id();
        String name = source.name();
        int heightDecimeters = source.height();
        int weightHectograms = source.weight();

        String spriteUrl = Optional.ofNullable(source.sprites())
            .map(PokeApiPokemon.Sprites::other)
            .map(PokeApiPokemon.Other::officialArtwork)
            .map(PokeApiPokemon.OfficialArtwork::frontDefault)
            .orElse(null);
        
        Map<String, Integer> baseStatsMap = source.stats().stream()
            .collect(Collectors.toMap(statSlot -> statSlot.stat().name(), PokeApiPokemon.StatSlot::baseStat));

        BaseStats baseStats = new BaseStats(
            baseStatsMap.get("hp"),
            baseStatsMap.get("attack"),
            baseStatsMap.get("defense"),
            baseStatsMap.get("special-attack"),
            baseStatsMap.get("special-defense"),
            baseStatsMap.get("speed")
        );

        Type mainType = findOrCreateType(typeNames.getFirst());

        if(typeNames.size() > 1){
            Type secondaryType = findOrCreateType(typeNames.get(1));
            
            pokemonRepository.save(new Pokemon(pokedexNumber, name, heightDecimeters, weightHectograms, spriteUrl, baseStats, mainType, secondaryType));
        } else {
            pokemonRepository.save(new Pokemon(pokedexNumber, name, heightDecimeters, weightHectograms, spriteUrl, baseStats, mainType));
        }

        return CREATED;
    }

    private Type findOrCreateType(String name) {
        return typeRepository.findByName(name)
            .orElseGet(() -> typeRepository.save(new Type(name)));
    }
}
