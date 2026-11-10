package io.github.szambon23.pokedex.team;

import io.github.szambon23.pokedex.team.dto.CreateTeamRequest;
import io.github.szambon23.pokedex.team.dto.TeamResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teams")
@Tag(name = "Teams", description = "Create and browse Pokemon teams of up to 6 members.")
public class TeamController {
    private final TeamService teamService;

    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    @Operation(operationId = "createTeam", summary = "Create a team",
            description = "Creates a team with up to 6 Pokemon, referenced by Pokedex number. "
                    + "Members keep the given order. With speciesClause enabled, the same species can't appear twice.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "The team was created. The Location header points to it."),
            @ApiResponse(responseCode = "400", description = "Invalid request.",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    public ResponseEntity<TeamResponse> create(@Valid @RequestBody CreateTeamRequest request) {
        TeamResponse created = teamService.create(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.id())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }

    @Operation(operationId = "getTeam", summary = "Get a team by its id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The team was found."),
            @ApiResponse(responseCode = "404", description = "No team exists with that id.",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    public TeamResponse getById(@PathVariable UUID id) {
        return teamService.findById(id);
    }
}
