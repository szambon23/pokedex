package io.github.szambon23.pokedex.team;

import io.github.szambon23.pokedex.pokemon.BaseStats;
import io.github.szambon23.pokedex.pokemon.Pokemon;
import io.github.szambon23.pokedex.type.Type;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TeamTest {

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void rejectsBlankName(String name) {
        assertThatThrownBy(() -> new Team(name, false))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void acceptsNameWithMaxLength() {
        Team team = new Team("a".repeat(50), false);

        assertThat(team.getName()).isEqualTo("a".repeat(50));
    }

    @Test
    void rejectsNameLongerThanMaxLength() {
        assertThatThrownBy(() -> new Team("a".repeat(51), false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Team name must be at most " + 50 + " characters");
    }

    @Test
    void addsMembersInOrder() {
        Team team = new Team("Team in order", false);
        team.addMember(pokemon(25, "pikachu"));
        team.addMember(pokemon(1, "bulbasaur"));
        team.addMember(pokemon(150, "mewtwo"));

        assertThat(team.getMembers())
                .extracting(Pokemon::getPokedexNumber)
                .containsExactly(25, 1, 150);
    }

    @Test
    void rejectsSeventhMember() {
        Team team = new Team("Full team", false);
        for (int i = 1; i <= Team.MAX_MEMBERS; i++) {
            team.addMember(pokemon(i, "pokemon" + i));
        }

        assertThatThrownBy(() -> team.addMember(pokemon(7, "seventh")))
                .isInstanceOf(TeamFullException.class);
        assertThat(team.getMembers()).hasSize(Team.MAX_MEMBERS);
    }

    @Test
    void getMembersIsReadOnly() {
        Team team = new Team("Not editable", false);

        team.addMember(pokemon(25, "pikachu"));
        team.addMember(pokemon(1, "bulbasaur"));
        team.addMember(pokemon(150, "mewtwo"));

        assertThatThrownBy(() -> team.getMembers().add(pokemon(7, "seventh")))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void allowsDuplicateSpeciesWhenClauseIsDisabled() {
        Team team = new Team("We allow twins", false);
        team.addMember(pokemon(25, "pikachu"));
        team.addMember(pokemon(1, "bulbasaur"));
        team.addMember(pokemon(150, "mewtwo"));
        team.addMember(pokemon(25, "pikachu"));

        assertThat(team.getMembers())
                .extracting(Pokemon::getPokedexNumber)
                .containsExactly(25, 1, 150, 25);
    }

    @Test
    void rejectsDuplicateSpeciesWhenClauseIsEnabled() {
        Team team = new Team("We don't allow twins", true);

        team.addMember(pokemon(25, "pikachu"));
        team.addMember(pokemon(1, "bulbasaur"));
        team.addMember(pokemon(150, "mewtwo"));

        assertThatThrownBy(() -> team.addMember(pokemon(25, "pikachu")))
                .isInstanceOf(DuplicateSpeciesException.class);

        assertThat(team.getMembers()).hasSize(3);
    }

    @Test
    void enablesClauseWhenThereAreNoDuplicates() {
        Team team = new Team("We allow twins for a time", false);

        team.addMember(pokemon(25, "pikachu"));
        team.addMember(pokemon(1, "bulbasaur"));
        team.addMember(pokemon(150, "mewtwo"));

        team.enableSpeciesClause();

        assertThat(team.isSpeciesClause()).isTrue();
    }

    @Test
    void refusesToEnableClauseWithDuplicates() {
        Team team = new Team("We allow twins for a time", false);

        team.addMember(pokemon(25, "pikachu"));
        team.addMember(pokemon(1, "bulbasaur"));
        team.addMember(pokemon(150, "mewtwo"));
        team.addMember(pokemon(25, "pikachu"));

        assertThatThrownBy(team::enableSpeciesClause)
                .isInstanceOfSatisfying(SpeciesClauseViolationException.class,
                        ex -> assertThat(ex.getDuplicatedPokedexNumbers()).containsExactly(25));
        assertThat(team.isSpeciesClause()).isFalse();

    }

    private static Pokemon pokemon(int pokedexNumber, String name) {
        return new Pokemon(pokedexNumber, name, 10, 100, null,
                new BaseStats(50, 50, 50, 50, 50, 50), new Type("normal"));
    }



}



