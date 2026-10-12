package io.github.szambon23.pokedex.pokemon;

import io.github.szambon23.pokedex.type.Type;
import io.github.szambon23.pokedex.type.TypeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import io.github.szambon23.pokedex.TestcontainersConfiguration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestcontainersConfiguration.class)
public class PokemonRepositoryTest {

    @Autowired
    private PokemonRepository pokemonRepository;

    @Autowired
    private TypeRepository typeRepository;

    @Autowired
    private TestEntityManager entityManager;

    BaseStats bulbasaurBaseStats = new BaseStats(45, 49, 49, 65, 65, 45);
    BaseStats pikachuBaseStats = new BaseStats(35, 55, 40, 50, 50, 90);

    @Test
    void savesAndFindsPokemonByPokedexNumbre() {

        Type electricType = typeRepository.save(new Type("electric"));
        Type grassType = typeRepository.save(new Type("grass"));
        Type poisonType = typeRepository.save(new Type("poison"));

        pokemonRepository.save(new Pokemon(1, "bulbasaur", 7, 69, "notBeingTested", bulbasaurBaseStats, grassType, poisonType));
        pokemonRepository.save(new Pokemon(25, "pikachu", 4, 60, "notBingTested", pikachuBaseStats, electricType));

        entityManager.flush();
        entityManager.clear();

        Pokemon bulbasuar = pokemonRepository.findByPokedexNumber(1).orElseThrow();

        assertThat(bulbasuar.getName()).isEqualTo("bulbasaur");
        assertThat(bulbasuar.getPrimaryType()).isEqualTo(grassType);
        assertThat(bulbasuar.getSecondaryType().get()).isEqualTo(poisonType);
        assertThat(bulbasuar.getStats()).isEqualTo(bulbasaurBaseStats);
    }

    @Test
    void returnsEmptyWhenPokedexNumberDoesNotExist() {
        assertThat(pokemonRepository.findByPokedexNumber(9999)).isEmpty();
    }

    @Test
    void savesAndFindsTypeByName() {
        typeRepository.save(new Type("electric"));

        entityManager.flush();
        entityManager.clear();

        Type electricType = typeRepository.findByName("electric").orElseThrow();

        assertThat(electricType.getName()).isEqualTo("electric");
    }

    @Test
    void rejectsDuplicatePokedexNumber() {
        Type electricType = typeRepository.save(new Type("electric"));
        Type poisonType = typeRepository.save(new Type("poison"));

        pokemonRepository.saveAndFlush(new Pokemon(25, "pikachu", 4, 60, "notBingTested", pikachuBaseStats, electricType));

        assertThatThrownBy(() -> pokemonRepository.saveAndFlush(
                new Pokemon(25, "poisonousPikachu", 4, 60, "notBingTested", pikachuBaseStats, electricType, poisonType)))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

}