package io.github.szambon23.pokedex.pokeapi;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface PokeApiClient {
    @GetExchange("/pokemon/{id}")
    PokeApiPokemon getPokemon(@PathVariable int id);
}