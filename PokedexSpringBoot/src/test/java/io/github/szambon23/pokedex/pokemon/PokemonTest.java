package io.github.szambon23.pokedex.pokemon;

import io.github.szambon23.pokedex.type.Type;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PokemonTest {
    String pikachuName = "pikachu";
    Type electicType = new Type("electric");
    BaseStats pikachuBaseStats = new BaseStats(35, 55, 40, 50, 50, 90);

    String bulbasaurName = "bulbasaur";
    Type grassType = new Type("grass");
    Type poisonType = new Type("poison");
    BaseStats bulbasaurBaseStats = new BaseStats(45, 49, 49, 65, 65, 45);



    Pokemon pikachu = new Pokemon(25, pikachuName, 4, 60, "notBeingTested", pikachuBaseStats, electicType);
    Pokemon bulbasuar = new Pokemon(1, bulbasaurName, 7, 69, "notBeingTested", bulbasaurBaseStats, grassType, poisonType);


    @Test
    public void createsValidPokemon() {
        assertThat(pikachu.getName()).isEqualTo(pikachuName);
        assertThat(pikachu.getPrimaryType()).isEqualTo(electicType);
    }

    @Test
    public void rejectsNullPokemonType() {
        assertThatThrownBy(() -> new Pokemon(25, pikachuName, 4, 60, "notBeingTested", pikachuBaseStats, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("primaryType must not be null");
    }

    @Test
    public void validatesBulbasuarSecondaryType() {
        assertThat(bulbasuar.getSecondaryType().get()).isEqualTo(poisonType);
    }

    @Test
    public void validatesNullSecondaryType() {
        assertThat(pikachu.getSecondaryType()).isEmpty();
    }

    @Test
    public void rejectsListTypeModifying(){
        assertThatThrownBy(() -> bulbasuar.getTypes().add(electicType))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void rejectsEqualPrimarySecondaryType(){
        assertThatThrownBy(() -> new Pokemon(1, bulbasaurName, 7, 69, "notBeingTested", bulbasaurBaseStats, grassType, grassType))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("primaryType and secondaryType must be different");
    }


}



