# Pokédex — Roadmap

Blocks are listed in the order they're being built: backend blocks are numbered, frontend (Angular) blocks are lettered.

**Progress: 9 / 17 blocks**

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
| B | Pagination | Frontend | Pagination controls for the full list | 🔜 |
| 8 | Search & filters | Backend | Search filters, V2 migration (generation), `GET /types`, sort-field whitelist | ⬜ |
| C | Search UI | Frontend | Filters and search in the list | ⬜ |
| 10 | Teams | Backend | Teams of up to 6 Pokémon, `POST` returns 201 + `Location` | ⬜ |
| D | Teams UI | Frontend | Create and view teams | ⬜ |
| 11 | Performance | Backend | N+1 in the list, indexes, caching | ⬜ |
| 12 | Security | Backend | Security, CORS, protect Swagger in production | ⬜ |
| 13 | Delivery | DevOps | Docker image, GitHub Actions, README | ⬜ |

## Known issues

Pending items, each linked to the block that fixes it.

| Issue | Fixed in |
|---|---|
| Sort accepts any entity field | 8 |
| N+1 queries when loading the list | 11 |
| Swagger UI is public | 12 |
| Pokémon card is not extracted into its own component | B |
| Type colors cover only 6 of the 18 types | B |
| Broken sprite URLs (image fails to load) are not handled | B |
| Base stats are a plain list (bars planned) | C |
| No frontend tests | — |
