package io.github.szambon23.pokedex.team;

import io.github.szambon23.pokedex.shared.PageResponse;
import io.github.szambon23.pokedex.team.dto.TeamRequest;
import io.github.szambon23.pokedex.team.dto.TeamResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/teams")
@Tag(name = "Teams", description = "Create, browse, edit and delete Pokemon teams of up to 6 members.")
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
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422",
                    description = "A Pokemon doesn't exist, or a species is repeated with speciesClause enabled.",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PostMapping
    public ResponseEntity<TeamResponse> create(@Valid @RequestBody TeamRequest request) {
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
            @ApiResponse(responseCode = "400", description = "The id is not a valid UUID.",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "No team exists with that id.",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping("/{id}")
    public TeamResponse getById(@PathVariable UUID id) {
        return teamService.findById(id);
    }

    @Operation(operationId = "updateTeam", summary = "Replace a team",
            description = "Replaces the name, speciesClause and members of an existing team. "
                    + "Members keep the given order. An empty list leaves the team without members.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The team was replaced."),
            @ApiResponse(responseCode = "400", description = "Invalid id or request body.",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "No team exists with that id.",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "422",
                    description = "A Pokemon doesn't exist, or a species is repeated with speciesClause enabled.",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @PutMapping("/{id}")
    public TeamResponse update(@PathVariable UUID id, @Valid @RequestBody TeamRequest request) {
        return teamService.update(id, request);
    }

    @Operation(operationId = "listTeams", summary = "List teams",
            description = "Returns teams in creation order, oldest first.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "A page of teams."),
            @ApiResponse(responseCode = "400", description = "Invalid page or size.",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @GetMapping
    public PageResponse<TeamResponse> list(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "must be at least 0") int page,
            @RequestParam(defaultValue = "20") @Min(value = 1, message = "must be at least 1")
            @Max(value = 100, message = "must be at most 100") int size) {
        return teamService.findAll(page, size);
    }

    @Operation(operationId = "deleteTeam", summary = "Delete a team")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "The team was deleted."),
            @ApiResponse(responseCode = "400", description = "IThe id is not a valid UUID",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "404", description = "No team exists with that id.",
                    content = @Content(mediaType = "application/problem+json",
                            schema = @Schema(implementation = ProblemDetail.class)))
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        teamService.delete(id);
    }
}
