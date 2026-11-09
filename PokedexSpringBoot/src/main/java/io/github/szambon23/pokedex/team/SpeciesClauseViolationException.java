package io.github.szambon23.pokedex.team;

import java.util.List;

public class SpeciesClauseViolationException extends RuntimeException {
    public SpeciesClauseViolationException(List<Integer> duplicated) {
    }
}
