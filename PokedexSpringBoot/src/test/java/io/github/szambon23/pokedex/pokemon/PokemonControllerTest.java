package io.github.szambon23.pokedex.pokemon;


import io.github.szambon23.pokedex.pokemon.dto.PokemonResponse;
import io.github.szambon23.pokedex.type.Type;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.util.Optional;

import static org.mockito.Mockito.when;

@WebMvcTest(PokemonController.class)
public class PokemonControllerTest {

    @Autowired
    private MockMvcTester test;

    @MockitoBean
    private PokemonService pokemonService;

    @Test
    void getByPokedexNumberReturnsPokemon(){
        //when(pokemonService.findByPokedexNumber(25)).thenReturn(new PokemonResponse(new (pikachu()));
    }


    private static Pokemon pikachu() {
        return new Pokemon(25, "pikachu", 4, 60, "...", new BaseStats(35, 55, 40, 50, 50, 90), new Type("electric"));
    }

}

//void getByCodigoReturnsMateria() {
//    when(materiaService.findByCodigo(82)).thenReturn(new MateriaResponse(82, "Álgebra", 6));
//
//    MvcTestResult result = mvc.get().uri("/api/v1/materias/{codigo}", 82).exchange();
//
//    assertThat(result).hasStatusOk();
//    assertThat(result).bodyJson().extractingPath("$.nombre").isEqualTo("Álgebra");
//    assertThat(result).bodyJson().extractingPath("$.creditos").isEqualTo(6);
//}

