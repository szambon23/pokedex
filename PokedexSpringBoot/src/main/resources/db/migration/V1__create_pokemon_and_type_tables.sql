CREATE TABLE type (
                    id   BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    name VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE pokemon (
                    id                BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                    pokedex_number    INTEGER     NOT NULL UNIQUE,
                    name              VARCHAR(50) NOT NULL UNIQUE,
                    height_decimeters INTEGER     NOT NULL,
                    weight_hectograms INTEGER     NOT NULL,
                    sprite_url        VARCHAR(255),
                    hp                INTEGER     NOT NULL,
                    attack            INTEGER     NOT NULL,
                    defense           INTEGER     NOT NULL,
                    special_attack    INTEGER     NOT NULL,
                    special_defense   INTEGER     NOT NULL,
                    speed             INTEGER     NOT NULL
);

CREATE TABLE pokemon_type (
                    pokemon_id BIGINT  NOT NULL REFERENCES pokemon (id) ON DELETE CASCADE,
                    type_id    BIGINT  NOT NULL REFERENCES type (id),
                    slot_index INTEGER NOT NULL,
                    PRIMARY KEY (pokemon_id, slot_index),
                    CONSTRAINT chk_pokemon_type_max_types CHECK (slot_index BETWEEN 0 AND 1)
);

-- PostgreSQL no indexa las foreign keys automáticamente
CREATE INDEX idx_pokemon_type_type_id ON pokemon_type (type_id);

