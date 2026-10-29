package io.github.szambon23.pokedex.pokeapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PokeApiPokemon(
        int id,
        String name,
        int height,
        int weight,
        Sprites sprites,
        List<TypeSlot> types,
        List<StatSlot> stats) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sprites(Other other) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Other(@JsonProperty("official-artwork") OfficialArtwork officialArtwork) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record OfficialArtwork(@JsonProperty("front_default") String frontDefault) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record TypeSlot(int slot, NamedResource type) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record StatSlot(@JsonProperty("base_stat") int baseStat, NamedResource stat) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record NamedResource(String name, String url) {}
}