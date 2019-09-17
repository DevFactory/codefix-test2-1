import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from './auth.service';
import { AppRoutes } from '@app/shared/config/app.routes';
import { map } from 'rxjs/operators';
import { Observable } from 'rxjs';

/**
 * Validates the given route is access only when user is authenticated. Navigate to home page when is not.
 */
@Injectable()
export class AuthenticatedGuardService implements CanActivate {

  constructor(
    private router: Router,
    private authService: AuthService
  ) {
  }

  canActivate(): Observable<boolean> {
    return this.authService.tryToAuthenticate().pipe(map(canAuthenticate => {
      if (!canAuthenticate) {
        this.router.navigate([AppRoutes.Home]);
      }
      return canAuthenticate;
    }));
  }
}
