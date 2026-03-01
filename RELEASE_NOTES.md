# Release Notes — v2.2

## Highlights

This release upgrades the project to **Spring Boot 4.0.0** and **Java 25**, migrates all entity primary keys from sequential `Long` to `UUID`, and improves the base module architecture by centralizing the `@Id` field in `BaseEntity`.

---

## Breaking Changes

### UUID Primary Keys
All entity IDs have been migrated from `Long` to `java.util.UUID`.

- **REST API endpoints** now expect and return UUIDs instead of numeric IDs (e.g., `/users/550e8400-e29b-41d4-a716-446655440001` instead of `/users/123`)
- **Database columns** are now `UUID` type instead of `BIGINT`. Existing databases require manual data migration.
- **JSON payloads** represent IDs as strings (Jackson handles UUID serialization/deserialization automatically)

### Centralized `@Id` in `BaseEntity`
The `@Id` field with `@GeneratedValue` and `@UuidGenerator` is now defined once in `BaseEntity`. Subclass entities (`User`, `TokenEntity`) no longer declare their own ID field or sequence generators.

### Spring Boot 4.0.0 & Java 25
Upgraded from Spring Boot 3.2.3 / Java 21 to Spring Boot 4.0.0 / Java 25.

---

## What Changed

### Core Module (`spring-boot-template-core`)
| File | Change |
|---|---|
| `Entity.java` | `Long getId()` → `UUID getId()` |
| `BaseEntity.java` | Added `@Id @GeneratedValue @UuidGenerator private UUID id` |
| `DTO.java` | `Long getId()` → `UUID getId()` |
| `BaseDTO.java` | `protected Long id` → `protected UUID id` |
| `BaseService.java` | `JpaRepository<E, Long>` → `JpaRepository<E, UUID>`, updated `findById`/`deleteById` signatures |
| `BaseException.java` | Minor cleanup |
| `BaseMapper.java` | Minor cleanup |
| `StringUtils.java` | Moved to `base.util` package |

### API Module (`spring-boot-template-api`)
| File | Change |
|---|---|
| `User.java` | Removed local `@Id`/`@SequenceGenerator` — ID inherited from `BaseEntity` |
| `TokenEntity.java` | Removed local `@Id`/`@SequenceGenerator` — ID inherited from `BaseEntity` |
| `UserDTO.java` | `private Long id` → `private UUID id` |
| `UserRepository.java` | `JpaRepository<User, Long>` → `JpaRepository<User, UUID>` |
| `TokenRepository.java` | `JpaRepository<TokenEntity, Long>` → `UUID`, query parameter `Long id` → `UUID id` |
| `UserService.java` | `Long id` → `UUID id` in `updateCurrentUser` and `updateUser` |
| `UserController.java` | `@PathVariable Long id` → `@PathVariable UUID id` on all endpoints |
| `SecurityServices.java` | Minor cleanup |
| `JwtService.java` | Minor cleanup |
| `AuthenticationService.java` | Minor cleanup |

### Tests
- `UserServiceTest.java` — Replaced `1L`/`2L` literals with `UUID.fromString(...)` constants
- Added new test classes: `NotFoundExceptionTest`, `OperationFailedExceptionTest`, `UnauthorizedExceptionTest`, `BaseResponseTest`
- Configured inline mock maker for Mockito

### Infrastructure
| File | Change |
|---|---|
| `pom.xml` | Upgraded to Spring Boot 4.0.0, Java 25 |
| `docker-compose.yml` | Cleanup |
| `Dockerfile` | Updated for Java 25 |

---

## Migration Guide

### For existing databases
If you have an existing database with `BIGINT` primary keys, you'll need to run a migration:

```sql
-- Example for PostgreSQL
ALTER TABLE _user ALTER COLUMN id TYPE UUID USING gen_random_uuid();
ALTER TABLE token ALTER COLUMN id TYPE UUID USING gen_random_uuid();
ALTER TABLE token ALTER COLUMN user_id TYPE UUID;
```

> **Warning:** This will generate new UUIDs, breaking any externally stored references to old numeric IDs. Plan accordingly.

### For API consumers
- Update all client code to send/receive UUID strings instead of numeric IDs
- URL patterns change from `/users/123` to `/users/550e8400-e29b-41d4-a716-446655440001`

---

## Verification
- All 24 tests pass (`mvn test` — 12 core + 12 API)
- Clean compilation with no warnings (`mvn clean compile`)
