import { Routes } from '@angular/router';
import { PokemonList } from './pokemon/pokemon-list/pokemon-list';
import { PokemonDetail } from './pokemon/pokemon-detail/pokemon-detail';

export const routes: Routes = [
  { path: '', component: PokemonList },
  { path: 'pokemon/:pokedexNumber', component: PokemonDetail },
];
