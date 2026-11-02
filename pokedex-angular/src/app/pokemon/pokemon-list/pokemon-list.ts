import { Component, computed, effect, inject, input, numberAttribute, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { PokemonService } from '../../api/services/pokemon.service';
import { PokedexNumberPipe } from '../pokedex-number-pipe';
import { Router, RouterLink } from '@angular/router';

@Component({
  imports: [PokedexNumberPipe, RouterLink],
  selector: 'app-pokemon-list',
  styleUrl: './pokemon-list.css',
  templateUrl: './pokemon-list.html',
})
export class PokemonList {
  private readonly pokemonService = inject(PokemonService);
  private readonly pageSize = 20;
  private readonly router = inject(Router);

  readonly page = input(1, {
    transform: (value: unknown) => Math.max(1, numberAttribute(value, 1)),
  });

  protected readonly pokemons = rxResource({
    params: () => this.page() - 1,
    stream: ({ params: page }) => this.pokemonService.getAll({ page, size: this.pageSize }),
  });

  protected readonly totalPages = computed(() =>
    this.pokemons.hasValue() ? this.pokemons.value().totalPages : 0,
  );

  constructor() {
    effect(() => {
      const total = this.totalPages();
      if (total > 0 && this.page() > total) {
        this.router.navigate([], { queryParams: { page: total }, replaceUrl: true });
      }
    });
  }

  protected goToPage(page: number): void {
    this.router.navigate([], { queryParams: { page } });
  }

}
