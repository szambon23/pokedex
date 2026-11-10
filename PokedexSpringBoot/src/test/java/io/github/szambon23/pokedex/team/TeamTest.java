package io.github.szambon23.pokedex.team;

import io.github.szambon23.pokedex.pokemon.BaseStats;
import io.github.szambon23.pokedex.pokemon.Pokemon;
import io.github.szambon23.pokedex.type.Type;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

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
    void updateReplacesNameClauseAndMembers() {
        Team team = new Team("Friends", false);
        team.addMember(pokemon(25, "pikachu"));
        team.addMember(pokemon(1, "bulbasaur"));

        team.update("Rivals", true, List.of(pokemon(150, "mewtwo"), pokemon(4, "charmander")));

        assertThat(team.getName()).isEqualTo("Rivals");
        assertThat(team.isSpeciesClause()).isTrue();
        assertThat(team.getMembers()).extracting(Pokemon::getName).containsExactly("mewtwo", "charmander");
    }

    @Test
    void updateEnablesClauseEvenIfOldMembersHadDuplicates() {
        Team team = new Team("Twins", false);
        team.addMember(pokemon(25, "pikachu"));
        team.addMember(pokemon(25, "pikachu"));

        team.update("Twins", true, List.of(pokemon(1, "bulbasaur"), pokemon(4, "charmander")));

        assertThat(team.isSpeciesClause()).isTrue();
        assertThat(team.getMembers()).extracting(Pokemon::getName).containsExactly("bulbasaur", "charmander");
    }

    @Test
    void updateRejectsDuplicatesWhenClauseIsEnabled() {
        Team team = new Team("Friends", false);

        assertThatThrownBy(() -> team.update("Friends", true,
                List.of(pokemon(25, "pikachu"), pokemon(1, "bulbasaur"), pokemon(25, "pikachu"))))
                .isInstanceOf(DuplicateSpeciesException.class)
                .hasMessageContaining("#25");
    }

    @Test
    void updateAllowsDuplicatesWhenClauseIsDisabled() {
        Team team = new Team("Not really relevant", true);

        team.update("Not really relevant", false, List.of(pokemon(25, "pikachu"),  pokemon(25, "pikachu")));

        assertThat(team.getMembers()).extracting(Pokemon::getName).containsExactly("pikachu", "pikachu");
        assertThat(team.isSpeciesClause()).isFalse();
    }

    @Test
    void updateAllowsEmptyMembers() {
        Team team = new Team("Friends", false);
        team.addMember(pokemon(25, "pikachu"));
        team.addMember(pokemon(1, "bulbasaur"));

        team.update("Friends", false, List.of());

        assertThat(team.getMembers()).isEmpty();
    }

    @Test
    void updateWithBlankNameLeavesTeamUnchanged() {
        Team team = new Team("Friends", false);
        team.addMember(pokemon(25, "pikachu"));
        team.addMember(pokemon(1, "bulbasaur"));

        assertThatThrownBy(() -> team.update(" ", true, List.of(pokemon(150, "mewtwo"), pokemon(4, "charmander"))))
                .isInstanceOf(IllegalArgumentException.class);

        assertThat(team.getName()).isEqualTo("Friends");
        assertThat(team.isSpeciesClause()).isFalse();
        assertThat(team.getMembers()).extracting(Pokemon::getName).containsExactly("pikachu", "bulbasaur");
    }


    private static Pokemon pokemon(int pokedexNumber, String name) {
        return new Pokemon(pokedexNumber, name, 10, 100, null,
                new BaseStats(50, 50, 50, 50, 50, 50), new Type("normal"));
    }
}



