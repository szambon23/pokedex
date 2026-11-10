package io.github.szambon23.pokedex.team;

import java.util.List;
import java.util.stream.Collectors;

public class SpeciesClauseViolationException extends RuntimeException {

    private final List<Integer> duplicatedPokedexNumbers;

    public SpeciesClauseViolationException(List<Integer> duplicatedPokedexNumbers) {
        super("Cannot enable the Species Clause: the team has duplicated Pokémon "
                + duplicatedPokedexNumbers.stream().map(number -> "#" + number).collect(Collectors.joining(", ")));
        this.duplicatedPokedexNumbers = List.copyOf(duplicatedPokedexNumbers);
    }

    public List<Integer> getDuplicatedPokedexNumbers() {
        return duplicatedPokedexNumbers;
    }
}
