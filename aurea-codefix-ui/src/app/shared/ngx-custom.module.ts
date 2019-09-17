import { ModuleWithProviders, NgModule } from '@angular/core';
import { DfAlertModule } from '@devfactory/ngx-df/alert';
import { DfBreadcrumbsModule } from '@devfactory/ngx-df/breadcrumbs';
import { DfButtonModule } from '@devfactory/ngx-df/button';
import { DfCardModule } from '@devfactory/ngx-df/card';
import { DfCodePreviewModule } from '@devfactory/ngx-df/code-preview';
import { DfCodePreviewMinimapModule } from '@devfactory/ngx-df/code-preview-minimap';
import { DfCoreModule } from '@devfactory/ngx-df/core';
import { DfDropdownModule } from '@devfactory/ngx-df/dropdown';
import { DfGridModule } from '@devfactory/ngx-df/grid';
import { DfInputModule } from '@devfactory/ngx-df/input';
import { DfLabelModule } from '@devfactory/ngx-df/label';
import { DfLoadingSpinnerModule, DfLoadingSpinnerTypes } from '@devfactory/ngx-df/loading-spinner';
import { DfModalModule } from '@devfactory/ngx-df/modal';
import { DfProgressBarModule } from '@devfactory/ngx-df/progress-bar';
import { DfSelectModule } from '@devfactory/ngx-df/select';
import { DfSidebarModule } from '@devfactory/ngx-df/sidebar';
import { DfSlideToggleModule } from '@devfactory/ngx-df/slide-toggle';
import { DfTableModule } from '@devfactory/ngx-df/table';
import { DfTablePaginatorModule } from '@devfactory/ngx-df/table-paginator';
import { DfTabsModule } from '@devfactory/ngx-df/tabs';
import { DfToasterModule } from '@devfactory/ngx-df/toaster';
import { DfToolTipModule } from '@devfactory/ngx-df/tooltip';
import { DfTopbarModule } from '@devfactory/ngx-df/topbar';
import { DfUserProfileModule } from '@devfactory/ngx-df/user-profile';
import { DfWhatsNewModule } from '@devfactory/ngx-df/whats-new';
import { DfContextMenuModule } from '@devfactory/ngx-df';

const NGX_DF_MODULES: any[] = [
  DfAlertModule,
  DfButtonModule,
  DfBreadcrumbsModule,
  DfCardModule,
  DfCodePreviewModule,
  DfCodePreviewMinimapModule,
  DfCoreModule,
  DfDropdownModule,
  DfGridModule,
  DfInputModule,
  DfLabelModule,
  DfLoadingSpinnerModule,
  DfModalModule,
  DfProgressBarModule,
  DfSelectModule,
  DfSidebarModule,
  DfSlideToggleModule,
  DfTabsModule,
  DfTableModule,
  DfTablePaginatorModule,
  DfToasterModule,
  DfToolTipModule,
  DfTopbarModule,
  DfUserProfileModule,
  DfWhatsNewModule,
  DfContextMenuModule
];

@NgModule({
  imports: [
    DfAlertModule.forRoot(),
    DfButtonModule.forRoot(),
    DfBreadcrumbsModule.forRoot(),
    DfCardModule.forRoot(),
    DfCodePreviewModule.forRoot(),
    DfCodePreviewMinimapModule.forRoot(),
    DfCoreModule.forRoot(),
    DfDropdownModule.forRoot(),
    DfGridModule.forRoot(),
    DfContextMenuModule.forRoot(),
    DfInputModule.forRoot(),
    DfLabelModule.forRoot(),
    DfLoadingSpinnerModule.forRoot({
      type: DfLoadingSpinnerTypes.SLIM,
    }),
    DfModalModule.forRoot(),
    DfProgressBarModule.forRoot(),
    DfSelectModule.forRoot(),
    DfSidebarModule.forRoot(),
    DfSlideToggleModule.forRoot(),
    DfTabsModule.forRoot(),
    DfTableModule.forRoot(),
    DfTablePaginatorModule.forRoot(),
    DfToasterModule.forRoot(),
    DfToolTipModule.forRoot(),
    DfTopbarModule.forRoot(),
    DfUserProfileModule.forRoot(),
    DfWhatsNewModule.forRoot(),
  ],
  exports: NGX_DF_MODULES,
})
export class NgxDfRootModule {}

@NgModule({
  imports: NGX_DF_MODULES,
  exports: NGX_DF_MODULES,
})
export class NgxDfCustom {
  static forRoot(): ModuleWithProviders {
    return { ngModule: NgxDfRootModule };
  }
}
