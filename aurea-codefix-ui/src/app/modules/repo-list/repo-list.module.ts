import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SharedModule } from '@app/shared/shared.module';
import { RepoListRoutingModule } from './repo-list-routing.module';
import { RepoListComponent } from './pages/repo-list/repo-list.component';
import { RepoGridComponent } from './components/repos-grid/repo-grid.component';
import { DescriptionComponent } from './components/description/description.component';

@NgModule({
  imports: [RepoListRoutingModule, SharedModule, CommonModule],
  declarations: [RepoListComponent, RepoGridComponent, DescriptionComponent],
})
export class RepoListModule {
}
