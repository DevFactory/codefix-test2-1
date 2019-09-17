import { AuthService } from '@app/core/services/auth/auth.service';
import { TestBed } from '@angular/core/testing';
import { Auth0Callback, Auth0DecodedHash, WebAuth } from 'auth0-js';
import { UserStore } from '@app/core/stores/user.store';
import { Router } from '@angular/router';
import { AUTH_TOKEN, AUTH_USER } from '@app/shared/mocks/auth0.tocken';
import { AppRoutes } from '@app/shared/config/app.routes';
import { ConfigurationService } from '@app/core/services/configuration.service';

describe('AuthService', () => {
  let authService: AuthService;
  let auth0: jasmine.SpyObj<WebAuth>;
  let router: jasmine.SpyObj<Router>;
  const userStorage: UserStore = new UserStore();

  beforeEach(() => {
    auth0 = jasmine.createSpyObj(['authorize', 'parseHash', 'logout', 'checkSession']);
    router = jasmine.createSpyObj(['navigate']);

    TestBed.configureTestingModule({
      providers: [
        AuthService,
        {provide: WebAuth, useValue: auth0},
        {provide: UserStore, useValue: userStorage},
        {provide: Router, useValue: router},
        {provide: ConfigurationService, useValue: new ConfigurationService()}
      ]
    });

    authService = TestBed.get(AuthService);
    authService.auth0 = auth0;
  });

  it('#getToken when is not set', () => {
    expect(authService.getToken()).toBeNull();
  });

  it('#login call auth0 authorize', () => {
    authService.login();

    expect(auth0.authorize).toHaveBeenCalled();
  });

  it('#handleAuthentication when success', () => {
    auth0.parseHash.and.callFake(function () {
      const callback: Auth0Callback<Auth0DecodedHash> = arguments[0];
      callback.call(callback, null, AUTH_TOKEN);
    });

    authService.handleAuthentication();
    expect(router.navigate).toHaveBeenCalledWith([AppRoutes.RepoList]);
    expect(userStorage.user.getValue()).toEqual(AUTH_USER);
    expect(userStorage.isAnalyzing.getValue()).toBeFalsy();
    expect(authService.isAuthenticated()).toBe(true);
    expect(authService.getToken()).toBe(AUTH_TOKEN.accessToken);
  });

  it('#handleAuthentication when fail', () => {
    auth0.parseHash.and.callFake(function () {
      const callback: Auth0Callback<Auth0DecodedHash> = arguments[0];
      callback.call(callback, null, null);
    });

    authService.handleAuthentication();
    expect(router.navigate).toHaveBeenCalledWith([AppRoutes.Home]);
    expect(authService.isAuthenticated()).toBe(false);
  });

  it('#tryAuthenticate when success', () => {
    auth0.checkSession.and.callFake(function () {
      const callback: Auth0Callback<Auth0DecodedHash> = arguments[1];
      callback.call(callback, null, AUTH_TOKEN);
    });

    authService.tryToAuthenticate().subscribe(value => expect(value).toBe(true));
  });

  it('#tryAuthenticate when fail', () => {
    auth0.checkSession.and.callFake(function () {
      const callback: Auth0Callback<Auth0DecodedHash> = arguments[1];
      callback.call(callback, {error: 'an error'}, null);
    });

    authService.tryToAuthenticate().subscribe(value => expect(value).toBe(false));
  });

  it('#logout', () => {
    authService.logout();

    expect(auth0.logout).toHaveBeenCalledWith({
      returnTo: window.location.origin,
      clientID: 'grPQGoT2OYLkOHJuj8BX0rQLToY9T9Fm'
    });
  });

});
