package io.github.szambon23.pokedex.pokemon;

import io.github.szambon23.pokedex.shared.InvalidSortPropertyException;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PokemonSortPropertiesTest {

    @Test
    void translatesApiPropertyToEntityProperty() {
        Pageable input = PageRequest.of(0, 20, Sort.by("heightMeters"));

        Pageable result = PokemonSortProperties.toEntityPageable(input);

        assertThat(result.getSort().getOrderFor("heightDecimeters")).isNotNull();
    }

    @Test
    void keepsSortDirection() {
        Pageable input = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "speed"));

        Pageable result = PokemonSortProperties.toEntityPageable(input);

        assertThat(result.getSort().getOrderFor("stats.speed").getDirection())
                .isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void addsPokedexNumberAsTieBreaker() {
        Pageable input = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "speed"));

        Pageable result = PokemonSortProperties.toEntityPageable(input);

        assertThat(result.getSort())
                .extracting(Sort.Order::getProperty)
                .containsExactly("stats.speed", "pokedexNumber");
    }

    @Test
    void doesNotAddTieBreakerWhenAlreadySortedByPokedexNumber() {
        Pageable input = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "pokedexNumber"));

        Pageable result = PokemonSortProperties.toEntityPageable(input);

        assertThat(result.getSort())
                .extracting(Sort.Order::getProperty)
                .containsExactly("pokedexNumber");
        assertThat(result.getSort().getOrderFor("pokedexNumber").getDirection())
                .isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void keepsPageNumberAndSize() {
        Pageable input = PageRequest.of(7, 47, Sort.by(Sort.Direction.DESC, "pokedexNumber"));

        Pageable result = PokemonSortProperties.toEntityPageable(input);

        assertThat(result.getPageNumber()).isEqualTo(7);
        assertThat(result.getPageSize()).isEqualTo(47);
    }

    @Test
    void rejectsUnknownProperty() {
        Pageable input = PageRequest.of(0, 20, Sort.by("id"));

        assertThatThrownBy(() -> PokemonSortProperties.toEntityPageable(input))
                .isInstanceOfSatisfying(InvalidSortPropertyException.class,
                        ex -> assertThat(ex.getProperty()).isEqualTo("id"));
    }

    @Test
    void reportsAllowedPropertiesWithApiNames() {
        Pageable input = PageRequest.of(0, 20, Sort.by("id"));

        assertThatThrownBy(() -> PokemonSortProperties.toEntityPageable(input))
                .isInstanceOfSatisfying(InvalidSortPropertyException.class,
                        ex -> assertThat(ex.getAllowedProperties())
                                .contains("heightMeters", "speed")
                                .doesNotContain("heightDecimeters", "stats.speed"));
    }
}



