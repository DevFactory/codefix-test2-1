import { NgModule } from '@angular/core';

import { SharedModule } from '@app/shared/shared.module';

import { DashboardRoutingModule } from './dashboard-routing.module';
import { DashboardComponent } from './pages/dashboard/dashboard.component';

@NgModule({
  imports: [DashboardRoutingModule, SharedModule],
  declarations: [DashboardComponent],
})
export class DashboardModule {}
