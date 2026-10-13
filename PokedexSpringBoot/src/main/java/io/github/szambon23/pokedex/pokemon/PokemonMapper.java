package io.github.szambon23.pokedex.pokemon;

import io.github.szambon23.pokedex.pokemon.dto.PokemonResponse;
import io.github.szambon23.pokedex.pokemon.dto.PokemonSummary;
import io.github.szambon23.pokedex.pokemon.dto.StatsResponse;
import io.github.szambon23.pokedex.type.Type;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PokemonMapper {
    public PokemonSummary toSummary(Pokemon pokemon) {
        return new PokemonSummary(pokemon.getPokedexNumber(), pokemon.getName(),
                pokemon.getSpriteUrl(), typesToStringList(pokemon.getTypes()));
    }

    public PokemonResponse toResponse(Pokemon pokemon) {
        return new PokemonResponse(pokemon.getPokedexNumber(), pokemon.getName(), pokemon.getHeightDecimeters()/10.0, pokemon.getWeightHectograms()/10.0,
                pokemon.getSpriteUrl(), typesToStringList(pokemon.getTypes()), toStatsResponse(pokemon.getStats()));
    }

    private List<String> typesToStringList(List<Type> types) {
        return types.stream()
                .map(Type::getName)
                .toList();
    }

    private StatsResponse toStatsResponse(BaseStats stats) {
        return new StatsResponse(stats.hp(), stats.attack(), stats.defense(), stats.specialAttack(), stats.specialDefense(), stats.speed());
    }

}
