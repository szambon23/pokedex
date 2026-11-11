# Pokédex — Roadmap

Blocks are listed in the order they're being built: backend blocks are numbered, frontend (Angular) blocks are lettered.

**Progress: 13 / 17 blocks**

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
| 8 | Search & filters | Backend | Search filters, V2 migration (generation), `GET /types`, sort-field whitelist | ✅ |
| C | Search UI | Frontend | Filters and search in the list | ✅ |
| 10 | Teams | Backend | Teams of up to 6 Pokémon with an optional Species Clause. CRUD: `POST` (201 + `Location`), `GET` by id, paginated `GET` in creation order, `PUT` (full replace), `DELETE` (204) | ✅ |
| D | Teams UI | Frontend | Team builder: 6 slots, local draft with a single save, reuses the search bar and cards | 🔜 |
| 11 | Performance | Backend | N+1 in the list, indexes, caching | ⬜ |
| 12 | Security | Backend | Security, CORS, protect Swagger in production | ⬜ |
| E | Visual design | Frontend | Visual polish across all screens, stat bars | ⬜ |
| 13 | Delivery | DevOps | Docker image, GitHub Actions, README | ⬜ |

## Known issues

Pending items, each linked to the block that fixes it.

| Issue | Fixed in |
|---|---|
|Search name doesn’t escape SQL wildcards (%, _)| 11 |
|Generation filter postponed until more generations are imported| 11 |
| N+1 queries when loading the list | 11 |
| Swagger UI is public | 12 |
|An invalid sort in the URL shows a generic error instead of the API’s message| D |
| No frontend tests | — |
| N+1 queries when listing teams | 11 |
| Teams have no owner: anyone can edit or delete any team | 12 |
| Concurrent saves of the same team: the last one wins (no optimistic locking) | — |
| `createdAt` uses `Instant.now()`: ordering tests depend on real time passing | — |
