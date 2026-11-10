package io.github.szambon23.pokedex.team;

import io.github.szambon23.pokedex.pokemon.dto.PokemonSummary;
import io.github.szambon23.pokedex.pokemon.dto.StatsResponse;
import io.github.szambon23.pokedex.team.dto.TeamRequest;
import io.github.szambon23.pokedex.team.dto.TeamResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@WebMvcTest(TeamController.class)
class TeamControllerTest {
    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private TeamService teamService;

    @Test
    void createReturns201WithLocation() {
        UUID id = UUID.randomUUID();
        TeamResponse created = new TeamResponse(id, "Kanto starters", false, Instant.now(), List.of());
        when(teamService.create(any(TeamRequest.class))).thenReturn(created);

        MvcTestResult result = mvc.post().uri("/api/v1/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "name": "Kanto starters", "pokedexNumbers": [1, 4, 7] }
                        """)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.CREATED);
        assertThat(result.getResponse().getHeader("Location")).endsWith("/api/v1/teams/" + id);
        assertThat(result).bodyJson().extractingPath("$.name").isEqualTo("Kanto starters");
    }

    @Test
    void createReturns400WhenBodyIsInvalid() {
        MvcTestResult result = mvc.post().uri("/api/v1/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        { "name": "", "pokedexNumbers": [1, -4] }
                        """)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyJson().extractingPath("$.title").isEqualTo("Invalid request body");
        assertThat(result).bodyJson()
                .extractingPath("$.errors[*].field")
                .asArray()
                .containsExactly("name", "pokedexNumbers[1]");
        verifyNoInteractions(teamService);
    }

    @Test
    void createReturns422WhenPokemonIsUnknown() {
        when(teamService.create(any(TeamRequest.class)))
                .thenThrow(new UnknownPokemonException(999));

        MvcTestResult result = mvc.post().uri("/api/v1/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "name": "With a ghost", "pokedexNumbers": [1, 999] }
                    """)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.UNPROCESSABLE_CONTENT);
        assertThat(result).bodyJson().extractingPath("$.title").isEqualTo("Unknown Pokémon");
        assertThat(result).bodyJson().extractingPath("$.pokedexNumber").isEqualTo(999);
    }

    @Test
    void createReturns422WhenSpeciesIsDuplicated() {
        when(teamService.create(any(TeamRequest.class)))
                .thenThrow(new DuplicateSpeciesException(25));

        MvcTestResult result = mvc.post().uri("/api/v1/teams")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "name": "Pikachu twins", "speciesClause": true, "pokedexNumbers": [25, 25] }
                    """)
                .exchange();

        assertThat(result).hasStatus(HttpStatus.UNPROCESSABLE_CONTENT);
        assertThat(result).bodyJson().extractingPath("$.title").isEqualTo("Duplicate species");
        assertThat(result).bodyJson().extractingPath("$.pokedexNumber").isEqualTo(25);
    }

    @Test
    void getByIdReturnsTeam() {
        UUID id = UUID.fromString("9077f4c4-375e-4f13-984f-e856c057c7bd");
        PokemonSummary pikachu = new PokemonSummary(25, "pikachu", "...", List.of("electric"),
                new StatsResponse(35, 55, 40, 50, 50, 90));
        TeamResponse team = new TeamResponse(id, "Kanto starters", false,
                Instant.parse("2026-11-09T21:00:46.782165Z"), List.of(pikachu));
        when(teamService.findById(id)).thenReturn(team);

        MvcTestResult result = mvc.get().uri("/api/v1/teams/{id}", id).exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.name").isEqualTo("Kanto starters");
        assertThat(result).bodyJson().extractingPath("$.members[0].name").isEqualTo("pikachu");
        assertThat(result).bodyJson().extractingPath("$.createdAt").isEqualTo("2026-11-09T21:00:46.782165Z");
    }

    @Test
    void getByIdReturns404WhenTeamDoesNotExist() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000000");
        when(teamService.findById(id)).thenThrow(new TeamNotFoundException(id));

        MvcTestResult result = mvc.get().uri("/api/v1/teams/{id}", id).exchange();

        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(result).bodyJson().extractingPath("$.title").isEqualTo("Team not found");
        assertThat(result).bodyJson().extractingPath("$.teamId").isEqualTo(id.toString());
    }

    @Test
    void getByIdReturns400WhenIdIsNotAUuid() {
        MvcTestResult result = mvc.get().uri("/api/v1/teams/{id}", "abc").exchange();

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyJson().extractingPath("$.title").isEqualTo("Invalid request parameter");
        assertThat(result).bodyJson().extractingPath("$.detail").isEqualTo("id: must be a valid UUID");
        verifyNoInteractions(teamService);
    }








    
}