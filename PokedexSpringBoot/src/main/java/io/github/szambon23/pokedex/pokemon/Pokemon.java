package io.github.szambon23.pokedex.pokemon;

import io.github.szambon23.pokedex.type.Type;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Table(name = "pokemon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pokemon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int pokedexNumber;
    private String name;
    private int heightDecimeters;
    private int weightHectograms;
    private String spriteUrl;

    @Embedded
    private BaseStats stats;

    @ManyToMany
    @JoinTable(
            name = "pokemon_type",
            joinColumns = @JoinColumn(name = "pokemon_id"),
            inverseJoinColumns = @JoinColumn(name = "type_id"))
    @OrderColumn(name = "slot_index")
    private List<Type> types = new ArrayList<>();

    public Pokemon(int pokedexNumber, String name, int heightDecimeters,
                    int weightHectograms, String spriteUrl, BaseStats stats,Type primaryType) {
        this.pokedexNumber = pokedexNumber;
        this.name = name;
        this.heightDecimeters = heightDecimeters;
        this.weightHectograms = weightHectograms;
        this.spriteUrl = spriteUrl;
        this.stats = stats;
        Objects.requireNonNull(primaryType, "primaryType must not be null");
        this.types.add(primaryType);
    }

    public Pokemon(int pokedexNumber, String name, int heightDecimeters,
                    int weightHectograms, String spriteUrl, BaseStats stats, Type primaryType, Type secondaryType) {

        this(pokedexNumber, name, heightDecimeters, weightHectograms, spriteUrl, stats, primaryType);

        Objects.requireNonNull(secondaryType, "secondaryType must not be null");

        if (Objects.equals(secondaryType, primaryType)) {
            throw new IllegalArgumentException("primaryType and secondaryType must be different");
        }
        this.types.add(secondaryType);
    }

    public Type getPrimaryType(){
        return this.types.getFirst();
    }

    public Optional<Type> getSecondaryType(){
        if (this.types.size() > 1) {
            return Optional.of(types.get(1));
        }else {
            return Optional.empty();
        }
    }

    public List<Type> getTypes() {
        return Collections.unmodifiableList(this.types);
    }

    public String toString() {
        return id.toString() + "#" + pokedexNumber + name;
    }

}