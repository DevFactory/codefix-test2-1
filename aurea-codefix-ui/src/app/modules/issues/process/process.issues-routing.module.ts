import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InProcessIssuesComponent } from './pages/in-process-issues.component';

const routes: Routes = [
  {path: '', component: InProcessIssuesComponent}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class ProcessIssuesRoutingModule {
}
