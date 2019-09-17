import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { NavigationStore } from '@app/core/stores/navigation.store';
import { CompletedStore } from '@app/core/stores/completed.store';
import { CompletedComponent } from './completed.component';
import { CompletedService } from '../services/completed.service';
import { CompletedResult } from '@app/shared/mocks/completed';
import { of } from 'rxjs';

describe('CompletedComponent', () => {

  let component: CompletedComponent;
  let fixture: ComponentFixture<CompletedComponent>;
  let completedService: jasmine.SpyObj<CompletedService>;

  const navigationStore: NavigationStore = new NavigationStore();
  const completedStore: CompletedStore = new CompletedStore();

  beforeEach(() => {
    completedService = jasmine.createSpyObj(['getCompletedIssues']);

    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [CompletedComponent],
      providers: [
        {provide: NavigationStore, useValue: navigationStore},
        {provide: CompletedService, useValue: completedService},
        {provide: CompletedStore, useValue: completedStore}
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CompletedComponent);
    component = fixture.componentInstance;
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('#ngOnInit', () => {
    completedService.getCompletedIssues.and.returnValue(of(CompletedResult));

    component.ngOnInit();

    expect(component.tabs).toBe(navigationStore.tabs.getValue());
    expect(completedService.getCompletedIssues).toHaveBeenCalledWith();
  });

});
