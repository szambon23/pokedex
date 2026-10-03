package io.github.szambon23.pokedex;

import org.springframework.boot.SpringApplication;

public class TestPokedexSpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.from(PokedexSpringBootApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
