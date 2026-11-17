import { Component, computed, input } from '@angular/core';
import { PokemonSummary } from '../../api/models';
import { effectiveness, multiplier, TYPE_NAMES, TypeName } from '../../pokemon/type-chart';

type MatchupLevel = 'ok' | 'warning' | 'alert';

interface TypeMatchup {
  type: TypeName;
  weak: number;
  resist: number;
  immune: number;
  level: MatchupLevel;
}

function matchupLevel(weak: number, safe: number): MatchupLevel {
  if (weak < 3) {
    return 'ok';
  }
  return safe === 0 ? 'alert' : 'warning';
}

@Component({
  imports: [],
  selector: 'app-team-analysis',
  styleUrl: './team-analysis.css',
  templateUrl: './team-analysis.html',
})
export class TeamAnalysis {
  members = input.required<PokemonSummary[]>();

  readonly defensiveMatchups = computed<TypeMatchup[]>(() =>
    TYPE_NAMES.map((attacking) => {
      const multipliers = this.members().map((member) => effectiveness(attacking, member.types));
      const weak = multipliers.filter((m) => m > 1).length;
      const resist = multipliers.filter((m) => m > 0 && m < 1).length;
      const immune = multipliers.filter((m) => m === 0).length;
      return { type: attacking, weak, resist, immune, level: matchupLevel(weak, resist + immune) };
    }),
  );

  readonly coveredTypes = computed<TypeName[]>(() => {
    const ownTypes = [...new Set(this.members().flatMap((member) => member.types))];
    return TYPE_NAMES.filter((defending) => ownTypes.some((own) => multiplier(own, defending) > 1));
  });

  readonly sharedWeaknesses = computed(() =>
  this.defensiveMatchups().filter((row) => row.level !== 'ok'),
);



}
