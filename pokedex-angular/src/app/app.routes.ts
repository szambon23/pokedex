import { Routes } from '@angular/router';
import { PokemonList } from './pokemon/pokemon-list/pokemon-list';
import { PokemonDetail } from './pokemon/pokemon-detail/pokemon-detail';
import { TeamList } from './teams/team-list/team-list';
import { TeamBuilder } from './teams/team-builder/team-builder';

export const routes: Routes = [
  { path: '', component: PokemonList },
  { path: 'pokemon/:pokedexNumber', component: PokemonDetail },
  { path: 'teams', component: TeamList },
  { path: 'teams/new', component: TeamBuilder },   
  { path: 'teams/:id', component: TeamBuilder },
  { path: '**', redirectTo: '' },                 
];