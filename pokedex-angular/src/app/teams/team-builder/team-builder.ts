import { Component, computed, inject, input, linkedSignal, signal } from '@angular/core';
import { rxResource } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';
import { Router, RouterLink } from '@angular/router';
import { TeamsService } from '../../api/services';
import { PokemonSummary, TeamRequest } from '../../api/models';
import { PokemonList } from '../../pokemon/pokemon-list/pokemon-list';
import { MAX_TEAM_MEMBERS } from '../team-rules';
import { TitleCasePipe } from '@angular/common';
import { TeamAnalysis } from '../team-analysis/team-analysis';

@Component({
  imports: [PokemonList, RouterLink, TitleCasePipe, TeamAnalysis],
  selector: 'app-team-builder',
  styleUrl: './team-builder.css',
  templateUrl: './team-builder.html',
})
export class TeamBuilder {
  private readonly teamsService = inject(TeamsService);
  private readonly router = inject(Router);

  // From the URL: the team being edited and the collection filters
  readonly id = input<string>();
  readonly page = input<string>();
  readonly name = input<string>();
  readonly type = input<string>();
  readonly sort = input<string>();

  // Server state
  readonly team = rxResource({
    params: () => this.id(),
    stream: ({ params: id }) => this.teamsService.getTeam({ id }),
  });

  // Draft: starts from the loaded team and is edited locally
  readonly teamName = linkedSignal(() => (this.team.hasValue() ? this.team.value().name : ''));
  readonly speciesClause = linkedSignal(() =>
    this.team.hasValue() ? this.team.value().speciesClause : false,
  );
  readonly members = linkedSignal<PokemonSummary[]>(() =>
    this.team.hasValue() ? this.team.value().members : [],
  );

  // Save state
  readonly saving = signal(false);
  readonly saveError = signal<string | null>(null);

  // Derived state
  readonly slots = computed(() =>
    Array.from({ length: MAX_TEAM_MEMBERS }, (_, i) => this.members()[i] ?? null),
  );
  readonly isFull = computed(() => this.members().length >= MAX_TEAM_MEMBERS);

  readonly duplicatedNumbers = computed(() => {
    const seen = new Set<number>();
    const duplicated = new Set<number>();
    for (const member of this.members()) {
      if (seen.has(member.pokedexNumber)) {
        duplicated.add(member.pokedexNumber);
      }
      seen.add(member.pokedexNumber);
    }
    return duplicated;
  });

  readonly numbersBreakingClause = computed(() =>
    this.speciesClause() ? this.duplicatedNumbers() : new Set<number>(),
  );

  readonly breaksSpeciesClause = computed(() => this.numbersBreakingClause().size > 0);

  readonly duplicatedNames = computed(() => {
    const duplicated = this.duplicatedNumbers();
    const names = new Set<string>();
    for (const member of this.members()) {
      if (duplicated.has(member.pokedexNumber)) {
        names.add(member.name);
      }
    }
    return [...names];
  });

  readonly canSave = computed(
    () => this.teamName().trim() !== '' && !this.saving() && !this.breaksSpeciesClause(),
  );

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
        this.saveError.set(this.saveErrorMessage(err));
      },
    });
  }

  private saveErrorMessage(err: HttpErrorResponse): string {
    switch (err.status) {
      case 0:
        return 'Could not reach the server. Check that the backend is running.';
      case 400:
      case 422:
        return err.error?.detail ?? 'The team could not be saved because it is not valid.';
      case 404:
        return 'This team no longer exists. It may have been deleted in another tab.';
      default:
        return 'Something went wrong while saving. Please try again.';
    }
  }
}
