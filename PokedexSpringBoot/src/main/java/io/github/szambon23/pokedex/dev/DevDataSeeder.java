package io.github.szambon23.pokedex.dev;

import io.github.szambon23.pokedex.pokemon.BaseStats;
import io.github.szambon23.pokedex.pokemon.Pokemon;
import io.github.szambon23.pokedex.pokemon.PokemonRepository;
import io.github.szambon23.pokedex.type.Type;
import io.github.szambon23.pokedex.type.TypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@Profile("dev")
public class DevDataSeeder implements CommandLineRunner {
    private final PokemonRepository pokemonRepository;
    private final TypeRepository typeRepository;
    private static final String SPRITE_BASE_URL =
            "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/";

    public DevDataSeeder(PokemonRepository pokemonRepository, TypeRepository typeRepository) {
        this.pokemonRepository = pokemonRepository;
        this.typeRepository = typeRepository;
    }



    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (pokemonRepository.count() == 0) {
            Type grassType = new Type("grass");
            Type poisonType = new Type("poison");
            Type fireType = new Type("fire");
            Type flyingType = new Type("flying");
            Type waterType = new Type("water");
            Type electricType = new Type("electric");

            typeRepository.save(grassType);
            typeRepository.save(poisonType);
            typeRepository.save(fireType);
            typeRepository.save(flyingType);
            typeRepository.save(waterType);
            typeRepository.save(electricType);

            pokemonRepository.save(new Pokemon(1, "bulbasaur", 7,
                    69, spriteUrl(1),
                    new BaseStats(45, 49, 49, 65,65,45),
                    grassType, poisonType));
            pokemonRepository.save(new Pokemon(4, "charmander", 6,
                    85, spriteUrl(4),
                    new BaseStats(39, 52, 43, 60,50,65),
                    fireType));
            pokemonRepository.save(new Pokemon(6, "charizard", 17,
                    905, spriteUrl(6),
                    new BaseStats(78, 84, 78, 109,85,100),
                    fireType, flyingType));
            pokemonRepository.save(new Pokemon(7, "squirtle", 5,
                    90, spriteUrl(7),
                    new BaseStats(44, 48, 65, 50,64,43),
                    waterType));
            pokemonRepository.save(new Pokemon(25, "pikachu", 4,
                    60, spriteUrl(25) ,
                    new BaseStats(35, 55, 40, 50,50,90),
                    electricType));
        }
    }

    private String spriteUrl(int index){
        return SPRITE_BASE_URL + index + ".png";
    }
}
