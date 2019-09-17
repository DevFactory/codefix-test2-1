import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';

import { SharedModule } from '@app/shared/shared.module';
import { AuthRoutingModule } from './auth-routing.module';
import { CallbackComponent } from './pages/callback/callback.component';

@NgModule({
  imports: [CommonModule, AuthRoutingModule, SharedModule],
  declarations: [CallbackComponent],
})
export class AuthModule {}
