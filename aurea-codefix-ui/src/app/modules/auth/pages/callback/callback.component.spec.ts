import { AuthService } from '@app/core/services/auth/auth.service';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CallbackComponent } from './callback.component';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('CallbackComponent', () => {
  let authService: jasmine.SpyObj<AuthService>;
  let component: CallbackComponent;
  let fixture: ComponentFixture<CallbackComponent>;

  beforeEach(() => {
    authService = jasmine.createSpyObj(['handleAuthentication']);

    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [CallbackComponent],
      providers: [
        {provide: AuthService, useValue: authService},
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CallbackComponent);
    component = fixture.componentInstance;
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
    expect(authService.handleAuthentication).toHaveBeenCalled();
  });
});
