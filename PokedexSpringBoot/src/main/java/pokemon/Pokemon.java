package pokemon;

import io.github.szambon23.pokedex.type.Type;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "pokemon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pokemon {

    private static final int MAX_TYPES = 2;

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

    public Pokemon (int pokedexNumber, String name, int heightDecimeters,
                    int weightHectograms, String spriteUrl, BaseStats stats) {
        this.pokedexNumber = pokedexNumber;
        this.name = name;
        this.heightDecimeters = heightDecimeters;
        this.weightHectograms = weightHectograms;
        this.spriteUrl = spriteUrl;
        this.stats = stats;
    }

    public void addType(Type type) {
        if (types.size() >= MAX_TYPES) {
            throw new IllegalStateException("A Pokémon can have at most " + MAX_TYPES + " types");
        }
        types.add(type);
    }

    public List<Type> getTypes() {
        return Collections.unmodifiableList(types);
    }
}