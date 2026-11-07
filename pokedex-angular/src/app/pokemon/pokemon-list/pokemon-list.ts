import { Component, computed, effect, inject, input, numberAttribute, signal, Type } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { PokemonService } from '../../api/services/pokemon.service';
import { PokedexNumberPipe } from '../pokedex-number-pipe';
import { Router, RouterLink, Params} from '@angular/router';
import { PokemonCard } from '../pokemon-card/pokemon-card';   // ← 1: TypeScript conoce la clase
import { TypesService } from '../../api/services';

@Component({
  imports: [PokemonCard, RouterLink],                          // ← 2: el template puede usar la etiqueta
  selector: 'app-pokemon-list',
  styleUrl: './pokemon-list.css',
  templateUrl: './pokemon-list.html',
})

export class PokemonList {
  private readonly pokemonService = inject(PokemonService);
  private readonly pageSize = 20;
  private readonly router = inject(Router);
  private readonly typesService = inject(TypesService);
  protected readonly sortOptions = [
  { value: 'pokedexNumber', label: 'Number' },
  { value: 'name', label: 'Name' },
  { value: 'hp,desc', label: 'HP (highest first)' },
  { value: 'attack,desc', label: 'Attack (highest first)' },
  { value: 'defense,desc', label: 'Defense (highest first)' },
  { value: 'specialAttack,desc', label: 'Sp. Attack (highest first)' },
  { value: 'specialDefense,desc', label: 'Sp. Defense (highest first)' },
  { value: 'speed,desc', label: 'Speed (highest first)' },
];

  readonly page = input(1, {
    transform: (value: unknown) => Math.max(1, numberAttribute(value, 1)),
  });

  readonly name = input<string>();
  readonly type = input<string>();
  readonly sort = input<string>();

  protected readonly types = rxResource({                  // el resource que usa el template
  stream: () => this.typesService.listTypes(),
});

protected readonly pokemons = rxResource({
  params: () => ({
    page: this.page() - 1,
    name: this.name(),
    type: this.type(),
    sort: this.sort(),
  }),
  stream: ({ params }) =>
    this.pokemonService.listPokemon({
      page: params.page,
      size: this.pageSize,
      name: params.name,
      type: params.type,
      sort: params.sort ? [params.sort] : undefined,
    }),
});
  protected readonly totalPages = computed(() =>
    this.pokemons.hasValue() ? this.pokemons.value().totalPages : 0,
  );

  constructor() {
    effect(() => {
      const total = this.totalPages();
      if (total > 0 && this.page() > total) {
        this.router.navigate([], { queryParams: { page: total }, queryParamsHandling: 'merge', replaceUrl: true });
      }
    });
  }

  protected goToPage(page: number): void {
    this.router.navigate([], { queryParams: { page }, queryParamsHandling: 'merge' });
  }

  protected search(changes: Params): void {
  this.router.navigate([], { queryParams: { ...changes, page: 1 }, queryParamsHandling: 'merge' });
}

}
