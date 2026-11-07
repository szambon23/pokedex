import { Component, input, signal } from '@angular/core';
import { PokemonSummary } from '../../api/models/pokemon-summary';
import { PokedexNumberPipe } from '../pokedex-number-pipe';
import { StatsResponse } from '../../api/models';

@Component({
  selector: 'app-pokemon-card',
  imports: [PokedexNumberPipe],
  templateUrl: './pokemon-card.html',
  styleUrl: './pokemon-card.css',
})
export class PokemonCard {
  readonly pokemon = input.required<PokemonSummary>();

  protected readonly imageFailed = signal(false);
  readonly highlightStat = input<keyof StatsResponse>();

    protected readonly statLabels: Record<keyof StatsResponse, string> = {
  hp: 'HP',
  attack: 'Attack',
  defense: 'Defense',
  specialAttack: 'Sp. Atk',
  specialDefense: 'Sp. Def',
  speed: 'Speed',
};

}