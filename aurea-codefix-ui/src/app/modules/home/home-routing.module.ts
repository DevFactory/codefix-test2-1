import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { HomeComponent } from './pages/home/home.component';
import { LandingComponent } from './pages/landing/landing.component';
import { NotAuthenticatedGuardService } from '@app/core/services/auth/not-authenticated-guard.service';

const routes: Routes = [
  {
    path: '',
    component: HomeComponent,
    canActivate: [NotAuthenticatedGuardService],
    children: [{path: '', component: LandingComponent}]
  }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class HomeRoutingModule {}
