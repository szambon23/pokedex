import { Component, computed, inject, input, linkedSignal, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { TeamsService } from '../../api/services';
import { PokemonSummary, TeamRequest } from '../../api/models';
import { PokemonList } from '../../pokemon/pokemon-list/pokemon-list';
import { MAX_TEAM_MEMBERS } from '../team-rules';

@Component({
  imports: [PokemonList, RouterLink],
  selector: 'app-team-builder',
  styleUrl: './team-builder.css',
  templateUrl: './team-builder.html',
})
export class TeamBuilder {
  private readonly teamsService = inject(TeamsService);
  private readonly router = inject(Router);

  readonly id = input<string>();
  readonly page = input<string>();
  readonly name = input<string>();
  readonly type = input<string>();
  readonly sort = input<string>();

    readonly team = rxResource({
    params: () => this.id(),
    stream: ({ params: id }) => this.teamsService.getTeam({ id }),
  });

  readonly teamName = linkedSignal(() => (this.team.hasValue() ? this.team.value().name : ''));

  readonly speciesClause = linkedSignal(() =>
    this.team.hasValue() ? this.team.value().speciesClause : false,
  );

  readonly members = linkedSignal<PokemonSummary[]>(() =>
    this.team.hasValue() ? this.team.value().members : [],
  );

  readonly saving = signal(false);
  readonly saveError = signal<string | null>(null);

  readonly slots = computed(() =>
    Array.from({ length: MAX_TEAM_MEMBERS }, (_, i) => this.members()[i] ?? null),
  );
  readonly isFull = computed(() => this.members().length >= MAX_TEAM_MEMBERS);
  readonly canSave = computed(() => this.teamName().trim() !== '' && !this.saving());

  addMember(pokemon: PokemonSummary) {
    if (this.isFull()) {
      return;
    }
    this.members.update((list) => [...list, pokemon]);
  }

  removeMember(index: number) {
    this.members.update((list) => list.filter((_, i) => i !== index));
  }

  save() {
    const body: TeamRequest = {
      name: this.teamName().trim(),
      speciesClause: this.speciesClause(),
      pokedexNumbers: this.members().map((member) => member.pokedexNumber),
    };
    const id = this.id();
    const request$ = id
      ? this.teamsService.updateTeam({ id, body })
      : this.teamsService.createTeam({ body });

    this.saving.set(true);
    this.saveError.set(null);
    request$.subscribe({
      next: () => this.router.navigate(['/teams']),
      error: (err: HttpErrorResponse) => {
        this.saving.set(false);
        this.saveError.set(err.error?.detail ?? 'Could not save the team.');
      },
    });
  }
}
