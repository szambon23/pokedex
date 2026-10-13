package io.github.szambon23.pokedex.pokemon.dto;

public record StatsResponse(
        int hp,
        int attack,
        int defense,
        int specialAttack,
        int specialDefense,
        int speed
) {
}
