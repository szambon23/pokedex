package io.github.szambon23.pokedex.pokemon;


import io.github.szambon23.pokedex.pokemon.dto.PokemonResponse;
import io.github.szambon23.pokedex.pokemon.dto.PokemonSummary;
import io.github.szambon23.pokedex.shared.PageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pokemon")
public class PokemonController {
    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @GetMapping("/{pokedexNumber}")
    public ResponseEntity<PokemonResponse> getByPokedexNumber(@PathVariable int pokedexNumber){
        return ResponseEntity.of(pokemonService.findByPokedexNumber(pokedexNumber));
    }

    @GetMapping
    public PageResponse<PokemonSummary> getAll(
            @PageableDefault(size = 20, sort = "pokedexNumber") Pageable pageable){
        return pokemonService.findAll(pageable);
    }



}
