package io.github.szambon23.pokedex.pokeapi;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;


class PokeApiPokemonTest {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Test
    void deserializesExternalApiResponse() throws IOException {
        try (InputStream json = getClass().getResourceAsStream("/pokeapi/ditto.json")) {
            PokeApiPokemon ditto = jsonMapper.readValue(json, PokeApiPokemon.class);

            assertThat(ditto.id()).isEqualTo(132);
            assertThat(ditto.name()).isEqualTo("ditto");
            assertThat(ditto.height()).isEqualTo(3);
            assertThat(ditto.weight()).isEqualTo(40);
            assertThat(ditto.sprites().other().officialArtwork().frontDefault()).endsWith("/132.png");
            assertThat(ditto.stats())
                    .hasSize(6)
                    .extracting(PokeApiPokemon.StatSlot::baseStat)
                    .containsOnly(48);
            assertThat(ditto.types())
                    .extracting(slot -> slot.type().name())
                    .containsExactly("normal");

        }
    }
}