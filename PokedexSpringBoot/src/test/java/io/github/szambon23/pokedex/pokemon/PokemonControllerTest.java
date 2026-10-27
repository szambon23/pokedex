package io.github.szambon23.pokedex.pokemon;

import io.github.szambon23.pokedex.pokemon.dto.PokemonResponse;
import io.github.szambon23.pokedex.pokemon.dto.PokemonSummary;
import io.github.szambon23.pokedex.pokemon.dto.StatsResponse;
import io.github.szambon23.pokedex.shared.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@WebMvcTest(PokemonController.class)
public class PokemonControllerTest {

    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private PokemonService pokemonService;

    @Test
    void getByPokedexNumberReturnsPokemon() {
        PokemonResponse pikachu = new PokemonResponse(25, "pikachu", 0.4, 6.0, "...", List.of("electric"), new StatsResponse(35, 55, 40, 50, 50, 90));
        when(pokemonService.findByPokedexNumber(25)).thenReturn(pikachu);

        MvcTestResult result = mvc.get().uri("/api/v1/pokemon/{pokedexNumber}", 25).exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.name").isEqualTo("pikachu");
        assertThat(result).bodyJson().extractingPath("$.heightMeters").isEqualTo(0.4);
        assertThat(result).bodyJson()
                .extractingPath("$.types")
                .asArray()
                .containsExactly("electric");
    }

    @Test
    void getByPokedexNumberReturns404WhenPokemonDoesNotExist() {
        when(pokemonService.findByPokedexNumber(9999)).thenThrow(new PokemonNotFoundException(9999));

        MvcTestResult result = mvc.get().uri("/api/v1/pokemon/{pokedexNumber}", 9999).exchange();

        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(result).bodyJson()
                .extractingPath("$.title")
                .isEqualTo("Pokémon not found");
        assertThat(result).bodyJson()
                .extractingPath("$.pokedexNumber")
                .isEqualTo(9999);
    }

    @Test
    void getByPokedexNumberReturns400WhenNumberIsNotPositive() {

        MvcTestResult result = mvc.get().uri("/api/v1/pokemon/{pokedexNumber}", 0).exchange();

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).hasContentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON);
        verifyNoInteractions(pokemonService);

    }

    @Test
    void getAllReturnsPageOfSummaries() {
        PokemonSummary pikachu = new PokemonSummary(25, "pikachu", "...", List.of("electric"));
        PageResponse<PokemonSummary> page = new PageResponse<>(List.of(pikachu), 0, 20, 1, 1);
        when(pokemonService.findAll(any(Pageable.class))).thenReturn(page);

        MvcTestResult result = mvc.get().uri("/api/v1/pokemon").exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.content[0].name").isEqualTo("pikachu");
        assertThat(result).bodyJson().extractingPath("$.totalElements").isEqualTo(1);
    }
}
