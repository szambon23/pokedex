import { Component, computed, inject, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { PokemonService } from '../../api/services/pokemon.service';
import { PokedexNumberPipe } from '../pokedex-number-pipe';
import { RouterLink } from '@angular/router';

@Component({
  imports: [PokedexNumberPipe, RouterLink],
  selector: 'app-pokemon-list',
  styleUrl: './pokemon-list.css',
  templateUrl: './pokemon-list.html',
})
export class PokemonList {
  private readonly pokemonService = inject(PokemonService);
  private readonly pageSize = 20;

  protected readonly page = signal(0);

  protected readonly pokemons = rxResource({
    params: () => this.page(),
    stream: ({ params: page }) => this.pokemonService.getAll({ page, size: this.pageSize }),
  });

  protected readonly totalPages = computed(() =>
    this.pokemons.hasValue() ? this.pokemons.value().totalPages : 0,
  );
  
  protected previousPage(): void {
    this.page.update(p => p - 1);
  }

  protected nextPage(): void {
    this.page.update(p => p + 1);
  }
}
