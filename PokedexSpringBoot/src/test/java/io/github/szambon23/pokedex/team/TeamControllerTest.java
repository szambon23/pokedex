package io.github.szambon23.pokedex.team;

import io.github.szambon23.pokedex.pokemon.dto.PokemonSummary;
import io.github.szambon23.pokedex.pokemon.dto.StatsResponse;
import io.github.szambon23.pokedex.shared.PageResponse;
import io.github.szambon23.pokedex.team.dto.TeamRequest;
import io.github.szambon23.pokedex.team.dto.TeamResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@WebMvcTest(TeamController.class)
class TeamControllerTest {
    @Autowired
    private MockMvcTester mvc;

    @MockitoBean
    private TeamService teamService;

    @Test
    void createReturns201WithLocation() {
        UUID id = UUID.fromString("3f2b8c1e-6d4a-4b7e-9c2f-1a5d8e7b6c40");
        TeamRequest request = new TeamRequest("Kanto starters", false, List.of(1, 4, 7));
        TeamResponse created = new TeamResponse(id, "Kanto starters", false,
                Instant.parse("2026-11-09T21:00:46.782165Z"), List.of());
        when(teamService.create(request)).thenReturn(created);
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

    @Test
    void updateReturns200WithTeam() {
        UUID id = UUID.fromString("3f2b8c1e-6d4a-4b7e-9c2f-1a5d8e7b6c40");
        TeamRequest request = new TeamRequest("Rivals", true, List.of(150, 4));
        TeamResponse response = new TeamResponse(id, "Rivals", true,
                Instant.parse("2026-11-01T12:00:00Z"), List.of());
        when(teamService.update(eq(id), eq(request))).thenReturn(response);

        MvcTestResult result = mvc.put().uri("/api/v1/teams/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Rivals", "speciesClause": true, "pokedexNumbers": [150, 4]}
                        """)
                .exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.id").isEqualTo(id.toString());
        assertThat(result).bodyJson().extractingPath("$.name").isEqualTo("Rivals");
    }

    @Test
    void updateReturns404WhenTeamDoesNotExist() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000000");
        when(teamService.update(eq(id), any())).thenThrow(new TeamNotFoundException(id));

        assertThat(mvc.put().uri("/api/v1/teams/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "Rivals", "speciesClause": false, "pokedexNumbers": []}
                        """))
                .hasStatus(HttpStatus.NOT_FOUND)
                .bodyJson()
                .extractingPath("$.teamId").isEqualTo(id.toString());
    }

    @Test
    void updateReturns400WhenBodyIsInvalid() {
        UUID id = UUID.fromString("3f2b8c1e-6d4a-4b7e-9c2f-1a5d8e7b6c40");

        assertThat(mvc.put().uri("/api/v1/teams/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"name": "", "speciesClause": false, "pokedexNumbers": []}
                        """))
                .hasStatus(HttpStatus.BAD_REQUEST)
                .bodyJson()
                .extractingPath("$.errors[*].field").asArray().containsExactly("name");

        verifyNoInteractions(teamService);
    }

    @Test
    void listUsesDefaultPageAndSize() {
        UUID id = UUID.fromString("3f2b8c1e-6d4a-4b7e-9c2f-1a5d8e7b6c40");
        TeamResponse team = new TeamResponse(id, "First team", false,
                Instant.parse("2026-11-01T12:00:00Z"), List.of());
        when(teamService.findAll(0, 20)).thenReturn(PageResponse.from(new PageImpl<>(List.of(team))));

        MvcTestResult result = mvc.get().uri("/api/v1/teams").exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.content[0].name").isEqualTo("First team");
    }

    @Test
    void listAcceptsMaxSize() {
        UUID id = UUID.fromString("3f2b8c1e-6d4a-4b7e-9c2f-1a5d8e7b6c40");
        TeamResponse team = new TeamResponse(id, "First team", false,
                Instant.parse("2026-11-01T12:00:00Z"), List.of());
        when(teamService.findAll(0, 100)).thenReturn(PageResponse.from(new PageImpl<>(List.of(team))));

        MvcTestResult result = mvc.get().uri("/api/v1/teams").param("size", "100").exchange();

        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.content[0].name").isEqualTo("First team");
    }
    @ParameterizedTest
    @CsvSource({
            "page, -1,  page: must be at least 0",
            "size, 0,   size: must be at least 1",
            "size, 101, size: must be at most 100"
    })
    void listReturns400WhenPageOrSizeIsInvalid(String param, String value, String detail) {
        MvcTestResult result = mvc.get().uri("/api/v1/teams").param(param, value).exchange();

        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
        assertThat(result).bodyJson().extractingPath("$.detail").isEqualTo(detail);
        verifyNoInteractions(teamService);
    }

    @Test
    void deleteReturns204() {
        UUID id = UUID.fromString("3f2b8c1e-6d4a-4b7e-9c2f-1a5d8e7b6c40");

        MvcTestResult result = mvc.delete().uri("/api/v1/teams/{id}", id).exchange();

        assertThat(result).hasStatus(HttpStatus.NO_CONTENT);
        assertThat(result.getResponse().getContentAsByteArray()).isEmpty();
        verify(teamService).delete(id);
    }

    @Test
    void deleteReturns404WhenTeamDoesNotExist() {
        UUID id = UUID.fromString("00000000-0000-0000-0000-000000000000");
        doThrow(new TeamNotFoundException(id)).when(teamService).delete(id);

        MvcTestResult result = mvc.delete().uri("/api/v1/teams/{id}", id).exchange();

        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
        assertThat(result).bodyJson().extractingPath("$.teamId").isEqualTo(id.toString());
    }



}