import { NotAuthenticatedGuardService } from 'app/core/services/auth/not-authenticated-guard.service';
import { Router } from '@angular/router';
import { AuthService } from './auth.service';
import { TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { AppRoutes } from '@app/shared/config/app.routes';

describe('NotAuthenticatedGuardService', () => {
  let guardService: NotAuthenticatedGuardService;
  let router: jasmine.SpyObj<Router>;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    router = jasmine.createSpyObj(['navigate']);
    authService = jasmine.createSpyObj(['tryToAuthenticate']);

    TestBed.configureTestingModule({
      providers: [
        NotAuthenticatedGuardService,
        {provide: AuthService, useValue: authService},
        {provide: Router, useValue: router}
      ]
    });

    guardService = TestBed.get(NotAuthenticatedGuardService);
  });

  it('#canActivate is false and navigate to repo page when user is authenticated', () => {
    authService.tryToAuthenticate.and.returnValue(of(true));

    guardService.canActivate().subscribe(result => expect(result).toBe(false));
    expect(router.navigate).toHaveBeenCalledWith([AppRoutes.RepoList]);
  });

  it('#canActivate return true when user is not authenticated', () => {
    authService.tryToAuthenticate.and.returnValue(of(false));

    guardService.canActivate().subscribe(result => expect(result).toBe(true));
  });

});
