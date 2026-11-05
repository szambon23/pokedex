package io.github.szambon23.pokedex.pokemon;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import static org.assertj.core.api.Assertions.assertThat;

public class PokemonSortPropertiesTest {
    Pageable input = PageRequest.of(2, 20, Sort.by(Sort.Direction.DESC, "speed"));

    Pageable result = PokemonSortProperties.toEntityPageable(input);


    @Test
    void translatesApiPropertyToEntityProperty(){
        assertThat(result.getSort()).extracting(Sort.Order::getProperty)
                .containsExactly("stats.speed", "pokedexNumber");
    }




}
