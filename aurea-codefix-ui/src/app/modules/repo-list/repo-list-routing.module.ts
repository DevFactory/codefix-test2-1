import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { RepoListComponent } from './pages/repo-list/repo-list.component';

const homeRoutes: Routes = [
  {path: '', component: RepoListComponent},
];

@NgModule({
  imports: [RouterModule.forChild(homeRoutes)],
  exports: [RouterModule],
})
export class RepoListRoutingModule {
}
