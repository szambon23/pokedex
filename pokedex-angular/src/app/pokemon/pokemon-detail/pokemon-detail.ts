import { Component, computed, effect, inject, input, numberAttribute } from '@angular/core';
import { Location } from '@angular/common';
import { PokemonService } from '../../api/services';
import { rxResource } from '@angular/core/rxjs-interop';
import { PokedexNumberPipe } from '../pokedex-number-pipe';
import { RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  imports: [PokedexNumberPipe, RouterLink],
  selector: 'app-pokemon-detail',
  styleUrl: './pokemon-detail.css',
  templateUrl: './pokemon-detail.html',
})
export class PokemonDetail {
  private readonly pokemonService = inject(PokemonService);
  private readonly location = inject(Location);

  readonly pokedexNumber = input.required({ transform: numberAttribute });

  protected readonly pokemon = rxResource({
    params: () => this.pokedexNumber(),
    stream: ({ params: pokedexNumber }) =>
      this.pokemonService.getPokemon({ pokedexNumber }),
  });

  protected readonly notFound = computed(() => {
    const error = this.pokemon.error();
    return error instanceof HttpErrorResponse && (error.status === 404 || error.status === 400);
  });

  protected goBack(): void{
    this.location.back();
  }
}
