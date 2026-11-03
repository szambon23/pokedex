package io.github.szambon23.pokedex.pokemon;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface PokemonRepository extends JpaRepository<Pokemon, Long>, JpaSpecificationExecutor<Pokemon> {
    Optional<Pokemon> findByPokedexNumber(int pokedexNumber);
    boolean existsByPokedexNumber(int pokedexNumber);

}

