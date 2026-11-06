package io.github.szambon23.pokedex.pokemon;

import org.springframework.data.jpa.domain.Specification;

final class PokemonSpecifications {

    private PokemonSpecifications() {
    }

    static Specification<Pokemon> nameContains(String name) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    static Specification<Pokemon> hasType(String type) {
        return (root, query, cb) ->
                cb.equal(root.join("types").get("name"), type.toLowerCase());
    }
}
