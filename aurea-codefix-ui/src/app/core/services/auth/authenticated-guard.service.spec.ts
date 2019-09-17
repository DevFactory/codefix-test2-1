import { AuthenticatedGuardService } from 'app/core/services/auth/authenticated-guard.service';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { TestBed } from '@angular/core/testing';
import { AppRoutes } from '@app/shared/config/app.routes';
import { of } from 'rxjs';

describe('AuthenticatedGuardService', () => {
  let guardService: AuthenticatedGuardService;
  let router: jasmine.SpyObj<Router>;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    router = jasmine.createSpyObj(['navigate']);
    authService = jasmine.createSpyObj(['tryToAuthenticate']);

    TestBed.configureTestingModule({
      providers: [
        AuthenticatedGuardService,
        {provide: AuthService, useValue: authService},
        {provide: Router, useValue: router}
      ]
    });

    guardService = TestBed.get(AuthenticatedGuardService);
  });


  it('#canActivate is true when user is authenticated', () => {
    authService.tryToAuthenticate.and.returnValue(of(true));

    guardService.canActivate().subscribe(result => expect(result).toBe(true));
  });

  it('#canActivate return to home page when user is not authenticated', () => {
    authService.tryToAuthenticate.and.returnValue(of(false));

    guardService.canActivate().subscribe(result => expect(result).toBe(false));
    expect(router.navigate).toHaveBeenCalledWith([AppRoutes.Home]);
  });

});
