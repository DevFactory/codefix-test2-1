import { InjectionToken, NgModule } from '@angular/core';
import { ActivatedRouteSnapshot, RouterModule, Routes } from '@angular/router';
import { AuthenticatedGuardService } from '@app/core/services/auth/authenticated-guard.service';
import { AppRoutes } from '@app/shared/config/app.routes';
import { NotFoundComponent } from '@app/shared/components/not-found/not-found-component';

const externalUrlProvider: InjectionToken<any> = new InjectionToken('externalUrlRedirectResolver');

const routes: Routes = [
  {path: '', loadChildren: 'app/modules/home/home.module#HomeModule'},
  {path: 'callback', loadChildren: 'app/modules/auth/auth.module#AuthModule'},
  {path: 'terms', loadChildren: 'app/modules/terms/terms.module#TermsModule'},
  {
    path: 'dashboard',
    canActivate: [AuthenticatedGuardService],
    loadChildren: 'app/modules/dashboard/dashboard.module#DashboardModule'
  },
  {
    path: 'externalRedirect',
    canActivate: [externalUrlProvider],
    component: NotFoundComponent,
  },
  {path: '**', redirectTo: AppRoutes.Home},
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
  providers: [
    {
      provide: externalUrlProvider,
      useValue: (route: ActivatedRouteSnapshot) => {
        const externalUrl: string = route.paramMap.get('externalUrl');
        window.open(externalUrl, '_self');
      }
    }
  ]
})
export class AppRoutingModule {
}
