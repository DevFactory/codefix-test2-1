import { LayoutModule } from '@angular/cdk/layout';
import { OverlayModule } from '@angular/cdk/overlay';
import { NgModule, Optional, SkipSelf } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { UserStore } from '@app/core/stores/user.store';
import { NgxDfCustom } from '@app/shared/ngx-custom.module';
import { NgbDropdownModule, NgbTooltipModule } from '@ng-bootstrap/ng-bootstrap';
import { AuthService } from '@app/core/services/auth/auth.service';
import { NavigationStore } from '@app/core/stores/navigation.store';
import { AuthenticatedGuardService } from '@app/core/services/auth/authenticated-guard.service';
import { NotAuthenticatedGuardService } from '@app/core/services/auth/not-authenticated-guard.service';
import { createConfigService } from '@app/shared/utilities/config/configuration-service.factory';
import { ConfigurationService } from '@app/core/services/configuration.service';
import { CodeRampService } from '../modules/repo-list/services/code-ramp.service';
import { DelayService } from '@app/core/services/delay.service';
import { TermsGuardService } from '../modules/dashboard/services/terms.guard.service';
import { RepositoryService } from '../modules/repo-list/services/repository.service';
import { RepositoriesStore } from '@app/core/stores/repositories.store';
import { CustomHttpModule } from '@app/core/modules/http/custom-http.module';
import { IssuesStore } from './stores/issues.store';
import { CompletedStore } from './stores/completed.store';
import { CompletedService } from 'app/modules/issues/completed/services/completed.service';
import { IssueService } from 'app/modules/issues/backlog/services/issue.service';
import { OrdersService } from '../modules/issues/process/services/orders.service';
import { OrderStore } from '@app/core/stores/order.store';

/**
 * The Core module is used to hold all root-level providers. It should only be imported in the AppModule.
 */
@NgModule({
  /** Place all forRoot() imports here */
  imports: [
    CustomHttpModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserModule,
    LayoutModule,
    OverlayModule,
    NgxDfCustom.forRoot(),
    NgbDropdownModule.forRoot(),
    NgbTooltipModule.forRoot(),
    BrowserAnimationsModule,
  ],
  /** Place all services/providers/injection tokens here here */
  providers: [
    /** Provide your app wide services here */
    AuthService,
    AuthenticatedGuardService,
    TermsGuardService,
    NotAuthenticatedGuardService,
    {
      provide: ConfigurationService,
      useFactory: () => createConfigService()
    },
    CodeRampService,
    RepositoryService,
    IssueService,
    OrdersService,
    DelayService,
    /** Add your stores here */
    NavigationStore,
    OrderStore,
    UserStore,
    RepositoriesStore,
    IssuesStore,
    CompletedStore,
    CompletedService
  ],
})
export class CoreModule {
  constructor(@Optional() @SkipSelf() parentModule: CoreModule) {
    if (parentModule) {
      throw new Error('CoreModule is already loaded. Import it in the AppModule only');
    }
  }
}
