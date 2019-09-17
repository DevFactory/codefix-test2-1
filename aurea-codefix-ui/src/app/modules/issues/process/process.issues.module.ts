import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SharedModule } from '@app/shared/shared.module';
import { ProcessIssuesRoutingModule } from './process.issues-routing.module';
import { InProcessIssuesComponent } from './pages/in-process-issues.component';
import { IssuesGridComponent } from './components/issues-grid/issues-grid.component';

@NgModule({
  imports: [CommonModule, ProcessIssuesRoutingModule, SharedModule],
  declarations: [InProcessIssuesComponent, IssuesGridComponent]
})
export class ProcessIssuesModule {
}
