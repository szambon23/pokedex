package io.github.szambon23.pokedex.shared;

import java.util.Collection;
import java.util.List;

public class InvalidSortPropertyException extends RuntimeException {

    private final String property;
    private final List<String> allowedProperties;

    public InvalidSortPropertyException(String property, Collection<String> allowedProperties) {
        super("Cannot sort by '" + property + "'");
        this.property = property;
        this.allowedProperties = allowedProperties.stream().sorted().toList();
    }

    public String getProperty() {
        return property;
    }

    public List<String> getAllowedProperties() {
        return allowedProperties;
    }
}