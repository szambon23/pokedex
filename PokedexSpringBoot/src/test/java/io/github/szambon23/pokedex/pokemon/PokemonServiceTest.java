package io.github.szambon23.pokedex.pokemon;

import io.github.szambon23.pokedex.pokemon.dto.PokemonResponse;
import io.github.szambon23.pokedex.pokemon.dto.PokemonSummary;
import io.github.szambon23.pokedex.shared.PageResponse;
import io.github.szambon23.pokedex.type.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PokemonServiceTest {

    @Mock
    private PokemonRepository pokemonRepository;
    private PokemonService pokemonService;

    @BeforeEach
    void setUp() {
        pokemonService = new PokemonService(pokemonRepository, new PokemonMapper());
    }

    @Test
    void findByPokedexNumberReturnsMappedPokemonWhenItExists() {
        when(pokemonRepository.findByPokedexNumber(25)).thenReturn(Optional.of(pikachu()));

        PokemonResponse pokemonResponse = pokemonService.findByPokedexNumber(25);

        assertThat(pokemonResponse.name()).isEqualTo("pikachu");
    }

    @Test
    void findByPokedexNumberThrowsWhenPokemonDoesNotExist() {

        when(pokemonRepository.findByPokedexNumber(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pokemonService.findByPokedexNumber(999))
                .isInstanceOf(PokemonNotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void findAllMapsPageContentAndMetadata() {
        // given: los datos y el guion del mock
        Pageable pageable = PageRequest.of(0, 20);
        Page<Pokemon> page = new PageImpl<>(List.of(pikachu()), pageable, 1);
        when(pokemonRepository.findAll(pageable)).thenReturn(page);

        PageResponse<PokemonSummary> response = pokemonService.findAll(pageable);

        assertThat(response.content())
                .extracting(PokemonSummary::name)
                .containsExactly("pikachu");

        assertThat(response.page()).isEqualTo(0);
        assertThat(response.size()).isEqualTo(20);
        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.totalPages()).isEqualTo(1);
    }

    private static Pokemon pikachu() {
        return new Pokemon(25, "pikachu", 4, 60, "...", new BaseStats(35, 55, 40, 50, 50, 90), new Type("electric"));
    }
}
