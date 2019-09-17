import { BehaviorSubject } from 'rxjs';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA, Renderer2 } from '@angular/core';
import { Completed } from '../../model/completed';
import { CompletedGridComponent } from './completed-grid.component';
import { CompletedResult } from '@app/shared/mocks/completed';


describe('CompletedGridComponent', () => {

  const completedListSubject: BehaviorSubject<Completed[]> = new BehaviorSubject(null);
  const loadedSubject: BehaviorSubject<boolean> = new BehaviorSubject(false);
  let renderer: jasmine.SpyObj<Renderer2>;

  let component: CompletedGridComponent;
  let fixture: ComponentFixture<CompletedGridComponent>;

  beforeEach(() => {
    renderer = jasmine.createSpyObj(['setStyle']);

    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [CompletedGridComponent],
      providers: [
        {provide: Renderer2, useValue: renderer}
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CompletedGridComponent);
    component = fixture.componentInstance;
    component.completedListSubject = completedListSubject;
    component.loadedSubject = loadedSubject;
  });

  it('#ngOnInit completedListSubject subscription', () => {
    completedListSubject.next(CompletedResult);
    fixture.detectChanges();

    expect(component.completedListSubject.getValue()).toEqual(CompletedResult);
  });

  it('#ngOnInit loadedSubject subscription', () => {
    loadedSubject.next(true);
    fixture.detectChanges();

    expect(component.loaded).toEqual(true);
    expect(component.completedList.length).toEqual(1);
  });

  it('#formatRepositoryName', () => {
    expect(component.formatRepositoryName('http://github.com/company/my-dummy.git'))
      .toBe('company / <strong>my-dummy</strong>');
  });
});
