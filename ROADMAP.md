# Pokédex — Roadmap

Blocks are listed in the order they're being built: backend blocks are numbered, frontend (Angular) blocks are lettered.

**Progress: 14 / 20 blocks**

✅ Done · 🔜 Next · ⬜ Pending

| # | Block | Area | Scope | Status |
|---|---|---|---|---|
| 1 | Project setup | Backend | Spring Boot 4 + Java 25, Gradle, PostgreSQL 18 with Docker Compose, Flyway, Testcontainers | ✅ |
| 2 | Domain model & persistence | Backend | `Pokemon`, `Type`, `BaseStats`, V1 migration, invariants (1–2 distinct types), unit and repository tests | ✅ |
| 3 | Read API | Backend | DTOs, mapper, service, `GET /api/v1/pokemon` (paginated) and `GET /api/v1/pokemon/{pokedexNumber}`, dev seeder | ✅ |
| 9 | API documentation | Backend | springdoc, Swagger UI, documented responses | ✅ |
| 4 | Error handling | Backend | ProblemDetail (RFC 9457), global exception handler, 400 / 404 / 500 | ✅ |
| A | List & detail pages | Frontend | Angular setup, dev proxy, generated API client, Pokémon grid, detail page, not-found handling | ✅ |
| 5 | Backend tests | Backend | Service tests with Mockito, controller tests with `@WebMvcTest` including error cases | ✅ |
| 6 | PokéAPI client | Backend | HTTP client with `@HttpExchange` | ✅ |
| 7 | Gen 1 importer | Backend | Idempotent import of the 151 Gen 1 Pokémon, `changeTypes` | ✅ |
| B | Pagination | Frontend | Pagination controls for the full list | ✅ |
| 8 | Search & filters | Backend | Name and type filters, `GET /types`, sort-field whitelist with a tie-breaker | ✅ |
| C | Search UI | Frontend | Filters and search in the list | ✅ |
| 10 | Teams | Backend | Teams of up to 6 Pokémon with an optional Species Clause. CRUD: `POST` (201 + `Location`), `GET` by id, paginated `GET` in creation order, `PUT` (full replace), `DELETE` (204) | ✅ |
| D | Teams UI | Frontend | Team list and team builder: 6 slots, local draft with a single save, Pokémon list reused in selection mode, Species Clause with duplicate highlighting, type analysis | ✅ |
| 11 | Performance | Backend | N+1 in the Pokémon and team lists, indexes, caching | 🔜 |
| 12 | Security | Backend | Security, CORS, protect Swagger in production, team ownership | ⬜ |
| 14 | Encounter locations | Backend | Import where each Pokémon can be found in each game and expose it per Pokémon | ⬜ |
| F | Encounter locations UI | Frontend | "Where to find" section on the detail page | ⬜ |
| E | Visual design | Frontend | Visual polish across all screens, stat radar chart (hexagon) on the detail page | ⬜ |
| 13 | Delivery | DevOps | Docker image, GitHub Actions, README | ⬜ |

## Known issues

Pending items, each linked to the block that fixes it. A dash means no block is planned yet.

| Issue | Fixed in |
|---|---|
| The name search doesn't escape SQL wildcards (`%`, `_`) | 11 |
| Generation filter postponed until more generations are imported | 11 |
| N+1 queries when loading the Pokémon list | 11 |
| N+1 queries when listing teams | 11 |
| Creating or updating a team runs one query per member to find the Pokémon | 11 |
| Swagger UI is public | 12 |
| Teams have no owner: anyone can edit or delete any team | 12 |
| Base stats on the detail page are a plain list | E |
| An invalid sort in the URL shows a generic error instead of the API's message | E |
| No frontend tests | — |
| Concurrent saves of the same team: the last one wins (no optimistic locking) | — |
| `createdAt` uses `Instant.now()`: ordering tests depend on real time passing | — |
| The team list ignores pagination: it loads the first 100 teams | — |
| Leaving the team builder discards unsaved changes without warning | — |
| The type chart lives only in the frontend | — |
| The team analysis uses types only: coverage is approximate and there's no speed ladder | — |