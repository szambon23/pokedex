package io.github.szambon23.pokedex.shared;


import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Pokédex API",
                version = "v1",
                description = "REST API to browse Pokémon species: detail by Pokédex number and paginated list."
        )
)
public class OpenApiConfig {


}