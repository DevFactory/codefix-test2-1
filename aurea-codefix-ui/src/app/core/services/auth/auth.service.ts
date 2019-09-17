import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import * as auth0 from 'auth0-js';
import { WebAuth } from 'auth0-js';
import { UserStore } from '@app/core/stores/user.store';
import { User } from '@app/core/models/user';
import { AppRoutes } from '@app/shared/config/app.routes';
import { Observable } from 'rxjs';
import { ConfigurationService } from '@app/core/services/configuration.service';

@Injectable()
export class AuthService {

  private _accessToken: string;
  private _expiresAt: number;

  auth0: WebAuth = new auth0.WebAuth({
    clientID: this.configService.get('auth0ClientId'),
    domain: 'auth.devfactory.com',
    responseType: 'token id_token',
    audience: 'devfactory',
    redirectUri: `${window.location.origin}/callback`,
    scope: 'openid email profile',
    leeway: 30
  });

  constructor(
    public router: Router,
    private userStore: UserStore,
    private configService: ConfigurationService
  ) {
    this._accessToken = null;
    this._expiresAt = 0;
  }

  getToken(): string {
    return this._accessToken;
  }

  login(): void {
    this.auth0.authorize();
  }

  handleAuthentication(): void {
    this.auth0.parseHash((err, authResult) => {
      if (authResult && authResult.accessToken && authResult.idToken) {
        this.localLogin(authResult);
        this.router.navigate([AppRoutes.RepoList]);
      } else {
        this.router.navigate([AppRoutes.Home]);
      }
    });
  }

  isAuthenticated(): boolean {
    return this._accessToken != null && Date.now() < this._expiresAt;
  }

  tryToAuthenticate(): Observable<boolean> {
    return new Observable(observer => {
      this.auth0.checkSession({}, (err, result) => {
        observer.next(this.tryAuthenticate(err, result));
        observer.complete();
      });
    });
  }

  logout(): void {
    this._accessToken = '';
    this._expiresAt = 0;

    this.auth0.logout({
      returnTo: window.location.origin,
      clientID: this.configService.get('auth0ClientId')
    });
  }

  private tryAuthenticate(err: any, result: any): boolean {
    if (err) {
      return false;
    }

    this.localLogin(result);
    return true;
  }

  private localLogin(authResult: any): void {
    const expiresAt: number = (authResult.expiresIn * 1000) + Date.now();
    this._accessToken = authResult.accessToken;
    this._expiresAt = expiresAt;
    this.userStore.user.next(this.asUser(authResult.idTokenPayload));
    this.userStore.initTerms(this.userStore.user.getValue());
    this.userStore.initIsAnalyzing(this.userStore.user.getValue());
  }

  private asUser(idToken: any): User {
    return new User(idToken.name, idToken.nickname, idToken.picture, idToken.email);
  }
}

