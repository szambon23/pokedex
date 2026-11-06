import { Component, input, signal } from '@angular/core';
import { PokemonSummary } from '../../api/models/pokemon-summary';
import { PokedexNumberPipe } from '../pokedex-number-pipe';

@Component({
  selector: 'app-pokemon-card',
  imports: [PokedexNumberPipe],
  templateUrl: './pokemon-card.html',
  styleUrl: './pokemon-card.css',
})
export class PokemonCard {
  readonly pokemon = input.required<PokemonSummary>();

  protected readonly imageFailed = signal(false);
}