package pokemon;

import jakarta.persistence.Embeddable;

@Embeddable
public record BaseStats(
        int hp,
        int attack,
        int defense,
        int specialAttack,
        int specialDefense,
        int speed) {
}