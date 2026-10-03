package io.github.szambon23.pokedex;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class PokedexSpringBootApplicationTests {

    @Test
    void contextLoads() {
    }

}
