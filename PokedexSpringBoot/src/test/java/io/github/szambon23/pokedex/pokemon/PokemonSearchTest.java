package io.github.szambon23.pokedex.pokemon;

import io.github.szambon23.pokedex.TestcontainersConfiguration;
import io.github.szambon23.pokedex.pokemon.dto.PokemonFilter;
import io.github.szambon23.pokedex.pokemon.dto.PokemonSummary;
import io.github.szambon23.pokedex.shared.PageResponse;
import io.github.szambon23.pokedex.type.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, PokemonService.class, PokemonMapper.class})
class PokemonSearchTest {
    private static final Pageable FIRST_PAGE = PageRequest.of(0, 20, Sort.by("pokedexNumber"));
    private static final Pageable FIRST_PAGE_WITH_SPEED_SORT = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "speed"));
    @Autowired
    private PokemonService pokemonService;
    @Autowired
    private TestEntityManager entityManager;

    private static BaseStats statsWithSpeed(int speed) {
        return new BaseStats(50, 50, 50, 50, 50, speed);
    }

    @BeforeEach
    void setUp() {
        Type fire = entityManager.persist(new Type("fire"));
        Type flying = entityManager.persist(new Type("flying"));
        Type water = entityManager.persist(new Type("water"));

        entityManager.persist(new Pokemon(4, "charmander", 6, 85, null, statsWithSpeed(65), fire));
        entityManager.persist(new Pokemon(6, "charizard", 17, 905, null, statsWithSpeed(100), fire, flying));
        entityManager.persist(new Pokemon(7, "squirtle", 5, 90, null, statsWithSpeed(43), water));

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void returnsAllPokemonWithoutFilters() {
        PageResponse<PokemonSummary> result = pokemonService.findAll(new PokemonFilter(null, null), FIRST_PAGE);

        assertThat(result.content()).extracting(PokemonSummary::name).containsExactly("charmander", "charizard", "squirtle");
    }

    @Test
    void filtersByNameIgnoringCase() {
        PageResponse<PokemonSummary> result = pokemonService.findAll(new PokemonFilter("CHAR", null), FIRST_PAGE);

        assertThat(result.content()).extracting(PokemonSummary::name).containsExactly("charmander", "charizard");
    }

    @Test
    void filtersByTypeIgnoringCase() {
        PageResponse<PokemonSummary> result = pokemonService.findAll(new PokemonFilter(null, "Fire"), FIRST_PAGE);

        assertThat(result.content()).extracting(PokemonSummary::name).containsExactly("charmander", "charizard");
    }

    @Test
    void filtersBySecondaryType() {
        PageResponse<PokemonSummary> result = pokemonService.findAll(new PokemonFilter(null, "flying"), FIRST_PAGE);

        assertThat(result.content()).extracting(PokemonSummary::name).containsExactly("charizard");
    }

    @Test
    void countsOnlyFilteredResults() {

        PageResponse<PokemonSummary> result = pokemonService.findAll(new PokemonFilter(null, "fire"), FIRST_PAGE);

        assertThat(result.totalElements()).isEqualTo(2);
    }

    @Test
    void combinesNameAndTypeFilters() {

        PageResponse<PokemonSummary> result = pokemonService.findAll(new PokemonFilter("zard", "fire"), FIRST_PAGE);

        assertThat(result.content()).extracting(PokemonSummary::name).containsExactly("charizard");
    }

    @Test
    void sortsBySpeedDescending() {
        PageResponse<PokemonSummary> result = pokemonService.findAll(new PokemonFilter(null, null), FIRST_PAGE_WITH_SPEED_SORT);

        assertThat(result.content()).extracting(PokemonSummary::name).containsExactly("charizard", "charmander", "squirtle");
    }
}

