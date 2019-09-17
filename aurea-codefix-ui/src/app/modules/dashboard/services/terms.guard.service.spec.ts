import { Router } from '@angular/router';
import { UserStore } from '@app/core/stores/user.store';
import { TestBed } from '@angular/core/testing';
import { TermsGuardService } from './terms.guard.service';
import { AppRoutes } from '@app/shared/config/app.routes';
import { AUTH_USER } from '@app/shared/mocks/auth0.tocken';

describe('TermsGuardService', () => {
  let termsService: TermsGuardService;
  let router: jasmine.SpyObj<Router>;
  const userStorage: UserStore = new UserStore();

  beforeEach(() => {
    router = jasmine.createSpyObj(['navigate']);

    TestBed.configureTestingModule({
      providers: [
        TermsGuardService,
        {provide: UserStore, useValue: userStorage},
        {provide: Router, useValue: router}
      ]
    });

    termsService = TestBed.get(TermsGuardService);
    userStorage.user.next(AUTH_USER);
    userStorage.initTerms(AUTH_USER);
  });

  it('#canActivate when pending accept terms', () => {
    userStorage.acceptTerms.next(false);

    expect(termsService.canActivate()).toBe(false);
    expect(router.navigate).toHaveBeenCalledWith([AppRoutes.Terms]);
  });

  it('#canActivate when terms accepted', () => {
    userStorage.acceptTerms.next(true);

    expect(termsService.canActivate()).toBe(true);
    expect(router.navigate).not.toHaveBeenCalled();
  });
});
