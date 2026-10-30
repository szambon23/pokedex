package io.github.szambon23.pokedex.importer;

import io.github.szambon23.pokedex.TestcontainersConfiguration;
import io.github.szambon23.pokedex.pokeapi.PokeApiPokemon;
import io.github.szambon23.pokedex.pokemon.BaseStats;
import io.github.szambon23.pokedex.pokemon.Pokemon;
import io.github.szambon23.pokedex.pokemon.PokemonRepository;
import io.github.szambon23.pokedex.type.Type;
import io.github.szambon23.pokedex.type.TypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, PokemonImporter.class})
class PokemonImporterTest {
    @Autowired
    private PokemonImporter importer;
    @Autowired
    private PokemonRepository pokemonRepository;
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private TypeRepository typeRepository;

    @Test
    void createsPokemonWithTranslatedFields() {
        PokeApiPokemon pikachu = pikachu();

        ImportResult result = importer.importPokemon(pikachu);
        entityManager.flush();
        entityManager.clear();

        Pokemon saved = pokemonRepository.findByPokedexNumber(25).orElseThrow();
        assertThat(result).isEqualTo(ImportResult.CREATED);
        assertThat(saved.getName()).isEqualTo("pikachu");
        assertThat(saved.getHeightDecimeters()).isEqualTo(4);
        assertThat(saved.getWeightHectograms()).isEqualTo(60);
        assertThat(saved.getSpriteUrl()).isEqualTo("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/25.png");
        assertThat(saved.getStats()).isEqualTo(new BaseStats(35, 55, 40, 50, 50, 90));
        assertThat(saved.getPrimaryType()).isEqualTo(new Type("electric"));
        assertThat(saved.getSecondaryType()).isEmpty();
    }

    @Test
    void ordersTypesBySlot() {
        PokeApiPokemon bulbasaur = bulbasaurWithUnarrangedTypes();

        ImportResult result = importer.importPokemon(bulbasaur);
        entityManager.flush();
        entityManager.clear();

        Pokemon saved = pokemonRepository.findByPokedexNumber(1).orElseThrow();
        assertThat(result).isEqualTo(ImportResult.CREATED);
        assertThat(saved.getName()).isEqualTo("bulbasaur");
        assertThat(saved.getPrimaryType()).isEqualTo(new Type("grass"));
        assertThat(saved.getSecondaryType()).contains(new Type("poison"));
    }

    @Test
    void skipsPokemonThatAlreadyExists() {
        ImportResult first = importer.importPokemon(pikachu());

        entityManager.flush();
        entityManager.clear();

        ImportResult second = importer.importPokemon(pikachu());
        assertThat(first).isEqualTo(ImportResult.CREATED);
        assertThat(second).isEqualTo(ImportResult.SKIPPED);
        assertThat(pokemonRepository.count()).isEqualTo(1);
    }

    @Test
    void reusesExistingTypes() {
        PokeApiPokemon ivysaur = ivysaur();
        PokeApiPokemon bulbasaur = bulbasaurWithUnarrangedTypes();

        ImportResult resultBulbasaur = importer.importPokemon(bulbasaur);
        entityManager.flush();
        entityManager.clear();

        ImportResult resultIvysaur = importer.importPokemon(ivysaur);

        assertThat(resultBulbasaur).isEqualTo(ImportResult.CREATED);
        assertThat(resultIvysaur).isEqualTo(ImportResult.CREATED);
        assertThat(typeRepository.count()).isEqualTo(2);
        assertThat(pokemonRepository.count()).isEqualTo(2);
    }

    @Test
    void storesNullSpriteWhenArtworkIsMissing() {
        ImportResult result = importer.importPokemon(charmanderWithNullSprites());

        entityManager.flush();
        entityManager.clear();

        Pokemon saved = pokemonRepository.findByPokedexNumber(4).orElseThrow();

        assertThat(result).isEqualTo(ImportResult.CREATED);
        assertThat(saved.getName()).isEqualTo("charmander");
        assertThat(saved.getSpriteUrl()).isNull();
    }

    private static PokeApiPokemon pikachu() {
        return new PokeApiPokemon(25, "pikachu", 4, 60,
                sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/25.png"),
                List.of(typeSlot(1, "electric")),
                stats(35, 55, 40, 50, 50, 90));
    }

    private static PokeApiPokemon bulbasaurWithUnarrangedTypes() {
        return new PokeApiPokemon(1, "bulbasaur", 7, 69,
                sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/1.png"),
                List.of(typeSlot(2, "poison"), typeSlot(1, "grass")),
                stats(45, 49, 49, 65, 65, 45));
    }

    private static PokeApiPokemon ivysaur() {
        return new PokeApiPokemon(2, "ivysaur", 10, 130,
                sprites("https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/2.png"),
                List.of(typeSlot(1, "grass"), typeSlot(2, "poison")),
                stats(60, 62, 63, 80, 80, 60));
    }

    private static PokeApiPokemon charmanderWithNullSprites() {
        return new PokeApiPokemon(4, "charmander", 6, 85,
                null,
                List.of(typeSlot(1, "fire")),
                stats(39, 52, 43, 60, 50, 65));
    }

    private static PokeApiPokemon.TypeSlot typeSlot(int slot, String name) {
        return new PokeApiPokemon.TypeSlot(slot, new PokeApiPokemon.NamedResource(name, null));
    }

    private static PokeApiPokemon.Sprites sprites(String url) {
        return new PokeApiPokemon.Sprites(
                new PokeApiPokemon.Other(new PokeApiPokemon.OfficialArtwork(url)));
    }

    private static List<PokeApiPokemon.StatSlot> stats(int hp, int attack, int defense,
                                                       int specialAttack, int specialDefense, int speed) {
        return List.of(
                statSlot("hp", hp),
                statSlot("attack", attack),
                statSlot("defense", defense),
                statSlot("special-attack", specialAttack),
                statSlot("special-defense", specialDefense),
                statSlot("speed", speed));
    }

    private static PokeApiPokemon.StatSlot statSlot(String name, int value) {
        return new PokeApiPokemon.StatSlot(value, new PokeApiPokemon.NamedResource(name, null));
    }
}