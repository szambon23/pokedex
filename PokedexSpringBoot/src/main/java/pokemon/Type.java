package io.github.szambon23.pokedex.type;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "type")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Type {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    public Type(String name) {
        this.name = name;
    }
}