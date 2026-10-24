import { Component, inject } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { PokemonService } from '../../api/services/pokemon.service';
import { PokedexNumberPipe } from '../pokedex-number-pipe'; // ← 1
import { RouterLink } from '@angular/router';

@Component({
  imports: [PokedexNumberPipe, RouterLink], // ← 2
  selector: 'app-pokemon-list',
  styleUrl: './pokemon-list.css',
  templateUrl: './pokemon-list.html',
})

export class PokemonList {
  private readonly pokemonService = inject(PokemonService);

  protected readonly pokemons = rxResource({
    stream: () => this.pokemonService.getAll({ page: 0, size: 20 }),
  });
}
