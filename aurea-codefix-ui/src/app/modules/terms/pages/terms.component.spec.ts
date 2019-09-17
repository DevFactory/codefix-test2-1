import { Router } from '@angular/router';
import { AuthService } from '@app/core/services/auth/auth.service';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { TermsComponent } from './terms.component';
import { UserStore } from '@app/core/stores/user.store';
import { AppRoutes } from '@app/shared/config/app.routes';
import { DfCardModule } from '@devfactory/ngx-df';
import { AUTH_USER } from '@app/shared/mocks/auth0.tocken';

describe('TermsComponent', () => {
  let router: jasmine.SpyObj<Router>;
  let authService: jasmine.SpyObj<AuthService>;
  let component: TermsComponent;
  let fixture: ComponentFixture<TermsComponent>;
  let userStore: UserStore;

  beforeEach(() => {
    router = jasmine.createSpyObj(['navigate']);
    authService = jasmine.createSpyObj(['logout']);
    userStore = new UserStore();

    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [TermsComponent],
      imports: [DfCardModule.forRoot()],
      providers: [
        {provide: Router, useValue: router},
        {provide: AuthService, useValue: authService},
        {provide: UserStore, useValue: userStore},
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TermsComponent);
    component = fixture.componentInstance;
    userStore.user.next(AUTH_USER);
    userStore.initTerms(AUTH_USER);
  });

  it('#onCancelClick should logout', () => {
    component.onCancelClick();

    expect(userStore.acceptTerms.getValue()).toBe(false);
    expect(authService.logout).toHaveBeenCalled();
  });

  it('#onAcceptClick should logout', () => {
    component.onAcceptClick();

    expect(userStore.acceptTerms.getValue()).toBe(true);
    expect(router.navigate).toHaveBeenCalledWith([AppRoutes.RepoList]);
  });
});
