package io.github.szambon23.pokedex.team;

import io.github.szambon23.pokedex.pokemon.BaseStats;
import io.github.szambon23.pokedex.pokemon.Pokemon;
import io.github.szambon23.pokedex.pokemon.PokemonMapper;
import io.github.szambon23.pokedex.shared.PageResponse;
import io.github.szambon23.pokedex.team.dto.TeamRequest;
import io.github.szambon23.pokedex.type.Type;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import io.github.szambon23.pokedex.TestcontainersConfiguration;
import io.github.szambon23.pokedex.team.dto.TeamResponse;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({TestcontainersConfiguration.class, TeamService.class, TeamMapper.class, PokemonMapper.class})
class TeamServiceTest {

    @Autowired
    private TeamService teamService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TestEntityManager entityManager;

    @BeforeEach
    void setUp() {
        Type grass = entityManager.persist(new Type("grass"));
        Type fire = entityManager.persist(new Type("fire"));
        Type water = entityManager.persist(new Type("water"));

        entityManager.persist(new Pokemon(1, "bulbasaur", 7, 69, null, stats(), grass));
        entityManager.persist(new Pokemon(4, "charmander", 6, 85, null, stats(), fire));
        entityManager.persist(new Pokemon(7, "squirtle", 5, 90, null, stats(), water));

        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void createsTeamWithMembersInOrder() {
        TeamResponse created = teamService.create(new TeamRequest("Kanto starters", false, List.of(7, 1, 4)));
        entityManager.flush();
        entityManager.clear();

        Team saved = teamRepository.findById(created.id()).orElseThrow();
        assertThat(saved.getMembers())
                .extracting(Pokemon::getPokedexNumber)
                .containsExactly(7, 1, 4);
    }

    @Test
    void updatePersistsChangesAndKeepsCreatedAt() {
        TeamResponse created = teamService.create(new TeamRequest("Kanto starters", false, List.of(1, 4, 7)));
        entityManager.flush();
        entityManager.clear();

        teamService.update(created.id(), new TeamRequest("Kanto base starters", true, List.of(7, 1)));
        entityManager.flush();
        entityManager.clear();

        Team reloaded = teamRepository.findById(created.id()).orElseThrow();
        assertThat(reloaded.getName()).isEqualTo("Kanto base starters");
        assertThat(reloaded.isSpeciesClause()).isTrue();
        assertThat(reloaded.getMembers()).extracting(Pokemon::getPokedexNumber).containsExactly(7, 1);
        assertThat(reloaded.getCreatedAt()).isEqualTo(created.createdAt());
    }

    @Test
    void persistsNameWithMaxLength() {
        TeamResponse created = teamService.create(new TeamRequest("a".repeat(50), false, List.of()));
        entityManager.flush();
        entityManager.clear();

        Team saved = teamRepository.findById(created.id()).orElseThrow();
        assertThat(saved.getName()).isEqualTo("a".repeat(50));
    }

    @Test
    void keepsCreatedAtAfterReloading() {
        TeamResponse created = teamService.create(new TeamRequest("Kanto starters", false, List.of()));
        entityManager.flush();
        entityManager.clear();

        Team saved = teamRepository.findById(created.id()).orElseThrow();
        assertThat(saved.getCreatedAt()).isEqualTo(created.createdAt());
    }

    @Test
    void rejectsUnknownPokemon() {
        assertThatThrownBy(() -> teamService.create(new TeamRequest("Kanto starters", false, List.of(7, 999))))
                .isInstanceOf(UnknownPokemonException.class);

        assertThat(teamRepository.count()).isZero();
    }

    @Test
    void findByIdThrowsWhenTeamDoesNotExist() {
        assertThatThrownBy(() -> teamService.findById(UUID.fromString("00000000-0000-0000-0000-000000000000")))
                .isInstanceOf(TeamNotFoundException.class);
    }

    @Test
    void findAllReturnsTeamsInCreationOrder() {
        TeamResponse first = teamService.create(new TeamRequest("team1", false, List.of())),
        second = teamService.create(new TeamRequest("team2", false, List.of())),
        third = teamService.create(new TeamRequest("team3", false, List.of()));

        entityManager.flush();
        entityManager.clear();

        PageResponse<TeamResponse> page1 = teamService.findAll(0, 2);
        PageResponse<TeamResponse> page2 = teamService.findAll(1, 2);

        assertThat(page1.content()).extracting(TeamResponse::name).containsExactly("team1", "team2");
        assertThat(page1.totalElements()).isEqualTo(3);

        assertThat(page2.content()).extracting(TeamResponse::name).containsExactly("team3");
    }

    private static BaseStats stats() {
        return new BaseStats(50, 50, 50, 50, 50, 50);
    }
}