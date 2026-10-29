package io.github.szambon23.pokedex.pokeapi;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.service.registry.ImportHttpServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@ImportHttpServices(group = "pokeapi", types = PokeApiClient.class)
class PokeApiConfig {

}
