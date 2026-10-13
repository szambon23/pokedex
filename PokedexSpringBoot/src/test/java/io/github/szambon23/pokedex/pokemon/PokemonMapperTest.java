package io.github.szambon23.pokedex.pokemon;

import io.github.szambon23.pokedex.pokemon.dto.PokemonResponse;
import io.github.szambon23.pokedex.pokemon.dto.PokemonSummary;
import io.github.szambon23.pokedex.pokemon.dto.StatsResponse;
import io.github.szambon23.pokedex.type.Type;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PokemonMapperTest {

    String bulbasaurName = "bulbasaur";
    Type grassType = new Type("grass");
    Type poisonType = new Type("poison");
    BaseStats bulbasaurBaseStats = new BaseStats(45, 49, 49, 65, 65, 45);

    Pokemon bulbasaur = new Pokemon(1, bulbasaurName,
            7, 69,
            "notBeingTested", bulbasaurBaseStats,
            grassType, poisonType);

    PokemonMapper mapper = new PokemonMapper();

    PokemonResponse expectedBulbasaurToResponse = new PokemonResponse(
            1, "bulbasaur", 0.7, 6.9, "notBeingTested",
            List.of("grass", "poison"),
            new StatsResponse(45, 49, 49, 65, 65, 45));

    PokemonSummary expectedBulbasaurToSummary = new PokemonSummary(
        1, bulbasaurName,
            "notBeingTested", List.of("grass", "poison")
    );

    @Test
    public void mapsPokemonToResponseConvertingUnits(){
        assertThat(mapper.toResponse(bulbasaur)).isEqualTo(expectedBulbasaurToResponse);
    }

    @Test
    public void mapsPokemonToSummaryResponse(){
        assertThat(mapper.toSummary(bulbasaur)).isEqualTo(expectedBulbasaurToSummary);
    }
}
