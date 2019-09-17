import { CanActivate, Router } from '@angular/router';
import { UserStore } from '@app/core/stores/user.store';
import { AppRoutes } from '@app/shared/config/app.routes';
import { Injectable } from '@angular/core';

@Injectable()
export class TermsGuardService implements CanActivate {

  constructor(private userStore: UserStore, private router: Router) {
  }

  canActivate(): boolean {
    if (this.pendingAcceptTerms()) {
      this.router.navigate([AppRoutes.Terms]);
      return false;
    }

    return true;
  }

  private pendingAcceptTerms = () => !this.userStore.acceptTerms.getValue();
}
