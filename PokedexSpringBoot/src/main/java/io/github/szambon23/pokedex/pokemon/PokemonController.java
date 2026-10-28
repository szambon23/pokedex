package io.github.szambon23.pokedex.pokemon;


import io.github.szambon23.pokedex.pokemon.dto.PokemonResponse;
import io.github.szambon23.pokedex.pokemon.dto.PokemonSummary;
import io.github.szambon23.pokedex.shared.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pokemon")
@Tag(name = "Pokemon", description = "Browse Pokemon species: " +
        "look up a Pokemon by its Pokedex number " +
        "or list them page by page.")
public class PokemonController {
    private final PokemonService pokemonService;

    public PokemonController(PokemonService pokemonService) {
        this.pokemonService = pokemonService;
    }

    @Operation(summary = "Get a Pokemon by its Pokedex number")


    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "A Pokemon was found"),

            @ApiResponse(responseCode = "400",
                    description = "The Pokedex number must be a positive integer",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class))),

            @ApiResponse(responseCode = "404",
                    description = "Unable to find the specified Pokemon",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)
                    ))
    })

    @GetMapping("/{pokedexNumber}")
    public PokemonResponse getByPokedexNumber(@PathVariable @Positive(message = "must be a positive integer") int pokedexNumber) {
        return pokemonService.findByPokedexNumber(pokedexNumber);
    }


    @Operation(
            summary = "List Pokemon, paginated",
            description = "Returns a page of Pokemon summaries. By default, 20 per page sorted by Pokedex number. "
                    + "Use the page, size and sort parameters to change it (maximum page size: 100)."
    )

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "A page of Pokemon summaries was found."),

            @ApiResponse(responseCode = "400",
                    description = "Invalid sort parameter, such as an unknown property.",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class))),
    })

    @GetMapping
    public PageResponse<PokemonSummary> getAll(
            @ParameterObject @PageableDefault(size = 20, sort = "pokedexNumber") Pageable pageable) {
        return pokemonService.findAll(pageable);
    }
}
