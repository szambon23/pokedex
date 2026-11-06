package io.github.szambon23.pokedex.pokemon;

import io.github.szambon23.pokedex.shared.InvalidSortPropertyException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

final class PokemonSortProperties {
    private static final Map<String, String> ALLOWED = Map.ofEntries(
            Map.entry("pokedexNumber", "pokedexNumber"),
            Map.entry("name", "name"),
            Map.entry("heightMeters", "heightDecimeters"),
            Map.entry("weightKg", "weightHectograms"),
            Map.entry("hp", "stats.hp"),
            Map.entry("attack", "stats.attack"),
            Map.entry("defense", "stats.defense"),
            Map.entry("specialAttack", "stats.specialAttack"),
            Map.entry("specialDefense", "stats.specialDefense"),
            Map.entry("speed", "stats.speed"));

    private PokemonSortProperties() {}

    static Pageable toEntityPageable(Pageable pageable) {
        List<Sort.Order> orders = pageable.getSort().stream()
                .map(order -> {
                    String entityProperty = ALLOWED.get(order.getProperty());
                    if (entityProperty == null) {
                        throw new InvalidSortPropertyException(order.getProperty(), ALLOWED.keySet());
                    }
                    return order.withProperty(entityProperty);
                })
                .toList();

        Sort sort = Sort.by(orders);
        if (sort.getOrderFor("pokedexNumber") == null) {
            sort = sort.and(Sort.by("pokedexNumber"));
        }

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
    }
}
