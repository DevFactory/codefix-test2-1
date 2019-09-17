import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ButtonComponent } from '@app/shared/components/button/button.component';
import { Router } from '@angular/router';

describe('ButtonComponent', () => {
  let component: ButtonComponent;
  let fixture: ComponentFixture<ButtonComponent>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    router = jasmine.createSpyObj(['navigateByUrl']);

    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [ButtonComponent],
      providers: [
        {provide: Router, useValue: router},
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ButtonComponent);
    component = fixture.componentInstance;
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('assert defaults', () => {
    expect(component.color).toBe('#03a9f4');
    expect(component.loading).toBe(false);
  });

  it('navigate when url', () => {
    component.url = 'an_url';
    component.navigateToUrl();

    expect(router.navigateByUrl).toHaveBeenCalledWith(component.url);
  });

  it('navigate when no url', () => {
    component.navigateToUrl();

    expect(router.navigateByUrl).toHaveBeenCalledTimes(0);
  });
});
