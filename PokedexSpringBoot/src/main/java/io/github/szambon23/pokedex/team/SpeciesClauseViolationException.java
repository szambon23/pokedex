package io.github.szambon23.pokedex.team;

import java.util.List;

public class SpeciesClauseViolationException extends RuntimeException {
    List<Integer> duplicatedPokedexNumbers;
    public SpeciesClauseViolationException(List<Integer> duplicated) {
        this.duplicatedPokedexNumbers = duplicated;
    }

    public List<Integer> getDuplicatedPokedexNumbers() {
        return duplicatedPokedexNumbers;
    }
}
