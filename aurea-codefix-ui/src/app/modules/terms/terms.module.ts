import { NgModule } from '@angular/core';
import { SharedModule } from '@app/shared/shared.module';
import { TermsRoutingModule } from './terms-routing.module';
import { TermsComponent } from './pages/terms.component';

@NgModule({
  imports: [TermsRoutingModule, SharedModule],
  declarations: [TermsComponent],
})
export class TermsModule {
}
