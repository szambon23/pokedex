package io.github.szambon23.pokedex.pokemon;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PokemonRepository extends JpaRepository<Pokemon, Long> {
    Optional<Pokemon> findByPokedexNumber(int pokedexNumber);
}
