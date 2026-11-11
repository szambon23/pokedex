CREATE TABLE team (
    id UUID PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    species_clause BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);
CREATE TABLE team_pokemon (
    team_id UUID NOT NULL REFERENCES team (id) ON DELETE CASCADE,
    pokemon_id BIGINT NOT NULL REFERENCES pokemon (id),
    slot_index INTEGER NOT NULL,
    PRIMARY KEY (team_id, slot_index),
    CONSTRAINT chk_team_pokemon_slot_index CHECK (slot_index BETWEEN 0 AND 5)
);

CREATE INDEX idx_team_pokemon_pokemon_id ON team_pokemon (pokemon_id);