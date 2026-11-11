import { Component, inject, signal } from '@angular/core';
import { TeamsService } from '../../api/services';
import { rxResource } from '@angular/core/rxjs-interop';
import { TeamResponse } from '../../api/models';
import { RouterLink } from '@angular/router';
import { MAX_TEAM_MEMBERS } from '../team-rules';

@Component({
  imports: [RouterLink],
  selector: 'app-team-list',
  styleUrl: './team-list.css',
  templateUrl: './team-list.html',
})

export class TeamList {
  private readonly teamsService = inject(TeamsService);
  readonly maxMembers = MAX_TEAM_MEMBERS;

  readonly teams = rxResource({
    stream: () => this.teamsService.listTeams({ page: 0, size: 100 }),
  });

  readonly deleteError = signal<string | null>(null);

  deleteTeam(team: TeamResponse) {
    if (!confirm(`Delete "${team.name}"?`)) {
      return;
    }
    this.deleteError.set(null);
    this.teamsService.deleteTeam({ id: team.id }).subscribe({
      next: () => this.teams.reload(),
      error: () => this.deleteError.set(`Could not delete "${team.name}".`),
    });
  }
}
