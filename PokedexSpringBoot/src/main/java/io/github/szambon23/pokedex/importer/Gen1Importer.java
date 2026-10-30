package io.github.szambon23.pokedex.importer;

import io.github.szambon23.pokedex.pokeapi.PokeApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("import")
class Gen1ImportRunner implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(Gen1ImportRunner.class);
    private static final int GEN_1_LAST_POKEDEX_NUMBER = 151;

    private final PokeApiClient pokeApiClient;
    private final PokemonImporter pokemonImporter;

    Gen1ImportRunner(PokeApiClient pokeApiClient, PokemonImporter pokemonImporter) {
        this.pokeApiClient = pokeApiClient;
        this.pokemonImporter = pokemonImporter;
    }

    @Override
    public void run(String... args) {
        int created = 0, skipped = 0, failed = 0;

        for (int i = 1; i <= GEN_1_LAST_POKEDEX_NUMBER; i++) {
            try {
                ImportResult result = pokemonImporter.importPokemon(pokeApiClient.getPokemon(i));

                if (result == ImportResult.CREATED) {
                    created++;
                } else {
                    skipped++;
                }
            } catch (RuntimeException e) {
                failed++;
                log.warn("Could not import pokemon {} due to {}", i, e.getMessage());
            }
        }
        log.info("Import finished: {} created, {} skipped, {} failed", created, skipped, failed);
    }

}
