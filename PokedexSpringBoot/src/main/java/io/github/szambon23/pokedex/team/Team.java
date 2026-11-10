package io.github.szambon23.pokedex.team;

import io.github.szambon23.pokedex.pokemon.Pokemon;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Entity
public class Team {
    public static final int MAX_MEMBERS = 6;
    public static final int MAX_NAME_LENGTH = 50;
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Getter
    private UUID id;

    @Getter
    private String name;

    @Getter
    private boolean speciesClause;

    @Getter
    private Instant createdAt;

    @ManyToMany
    @JoinTable(name = "team_pokemon",
            joinColumns = @JoinColumn(name = "team_id"),
            inverseJoinColumns = @JoinColumn(name = "pokemon_id"))
    @OrderColumn(name = "slot_index")
    private List<Pokemon> members = new ArrayList<>();

    public Team(String name, boolean speciesClause) {
        this.name = requireValidName(name);
        this.speciesClause = speciesClause;
        this.createdAt = Instant.now().truncatedTo(ChronoUnit.MICROS);
    }

    protected Team() {
    }

    public void update(String name, boolean speciesClause, List<Pokemon> newMembers) {
        Objects.requireNonNull(newMembers, "Members must not be null");
        this.name = requireValidName(name);
        this.speciesClause = speciesClause;
        this.members.clear();
        newMembers.forEach(this::addMember);
    }


    public void addMember(Pokemon pokemon) {
        Objects.requireNonNull(pokemon);
        if (members.size() == MAX_MEMBERS) {
            throw new TeamFullException(MAX_MEMBERS);
        }
        if (speciesClause && containsSpecies(pokemon)) {
            throw new DuplicateSpeciesException(pokemon.getPokedexNumber());
        }
        members.add(pokemon);
    }

    private boolean containsSpecies(Pokemon pokemon) {
        return members.stream().anyMatch(member -> member.getPokedexNumber() == pokemon.getPokedexNumber());
    }

    private List<Integer> duplicatedPokedexNumbers() {
        Set<Integer> seen = new HashSet<>();
        Set<Integer> duplicated = new TreeSet<>();
        for (Pokemon member : members) {
            if (!seen.add(member.getPokedexNumber())) {
                duplicated.add(member.getPokedexNumber());
            }
        }
        return List.copyOf(duplicated);
    }

    public void disableSpeciesClause() {
        this.speciesClause = false;
    }

    public List<Pokemon> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public static String requireValidName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Team name must not be blank");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("Team name must be at most " + MAX_NAME_LENGTH + " characters");
        }
        return name;
    }

}
