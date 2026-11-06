package io.github.szambon23.pokedex.type;


import io.github.szambon23.pokedex.type.dto.TypeResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/types")
@Tag(name = "Types", description = "Browse the Pokemon types available in the Pokedex, such as fire or water.")
public class TypeController {
    private TypeService typeService;

    public TypeController(TypeService typeService) {
        this.typeService = typeService;
    }

    @Operation(
            summary = "List all Pokemon types",
            description = "Returns every type that at least one Pokemon in the Pokedex has, sorted alphabetically. "
                    + "The list is small, so it is not paginated. "
                    + "Use a type's name as the type filter of GET /api/v1/pokemon."
    )
    @ApiResponse(responseCode = "200", description = "The list of types, sorted by name.")
    @GetMapping
    public List<TypeResponse> getAll() {
        return typeService.findAll();
    }
}
