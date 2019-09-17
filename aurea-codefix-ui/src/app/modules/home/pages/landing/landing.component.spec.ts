import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AuthService } from '@app/core/services/auth/auth.service';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { LandingComponent } from './landing.component';

describe('LandingComponent', () => {

  let component: LandingComponent;
  let fixture: ComponentFixture<LandingComponent>;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(() => {
    authService = jasmine.createSpyObj(['login']);

    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [LandingComponent],
      providers: [
        {provide: AuthService, useValue: authService}
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LandingComponent);
    component = fixture.componentInstance;
  });

  it('it should call auth0.logout on logout', () => {
    component.login();

    expect(authService.login).toHaveBeenCalled();
  });

});
