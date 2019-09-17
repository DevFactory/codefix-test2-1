import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { TermsGuardService } from './services/terms.guard.service';

const homeRoutes: Routes = [
  {
    path: '',
    component: DashboardComponent,
    children: [
      {
        path: 'repos',
        loadChildren: 'app/modules/repo-list/repo-list.module#RepoListModule',
        canActivate: [TermsGuardService]
      },
      {
        path: 'issues',
        loadChildren: 'app/modules/issues/backlog/issues.module#IssuesModule',
        canActivate: [TermsGuardService]
      },
      {
        path: 'issues/scheduled',
        loadChildren: 'app/modules/issues/process/process.issues.module#ProcessIssuesModule',
        canActivate: [TermsGuardService]
      },
      {
        path: 'issues/completed',
        loadChildren: 'app/modules/issues/completed/completed.module#CompletedModule',
        canActivate: [TermsGuardService]
      },
      {path: 'terms', loadChildren: 'app/modules/terms/terms.module#TermsModule'},
    ],
  },
];

@NgModule({
  imports: [RouterModule.forChild(homeRoutes)],
  exports: [RouterModule],
})
export class DashboardRoutingModule {
}
