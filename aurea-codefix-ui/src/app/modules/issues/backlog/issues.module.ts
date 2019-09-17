import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { SharedModule } from '@app/shared/shared.module';
import { IssuesRoutingModule } from './issues-routing.module';
import { IssuesComponent } from './pages/issues.component';
import { IssuesGridComponent } from './components/issues-grid/issues-grid.component';
import { SubscribeComponent } from './components/subscribe/subscribe.component';

@NgModule({
  imports: [CommonModule, IssuesRoutingModule, SharedModule],
  declarations: [IssuesComponent, IssuesGridComponent, SubscribeComponent]
})
export class IssuesModule {
}
