import { BehaviorSubject } from 'rxjs';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA, Renderer2 } from '@angular/core';
import { IssuesGridComponent } from './issues-grid.component';
import { Order } from '../../model/order';
import { SimpleOrder } from '@app/shared/mocks/order';

describe('IssuesGridComponent', () => {

  const orderListObservable: BehaviorSubject<Order> = new BehaviorSubject(null);
  const loadedSubject: BehaviorSubject<boolean> = new BehaviorSubject(false);

  let renderer: jasmine.SpyObj<Renderer2>;
  let component: IssuesGridComponent;
  let fixture: ComponentFixture<IssuesGridComponent>;

  beforeEach(() => {
    renderer = jasmine.createSpyObj(['setStyle']);

    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [IssuesGridComponent],
      providers: [
        {provide: Renderer2, useValue: renderer}
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IssuesGridComponent);
    component = fixture.componentInstance;
    component.orderObservable = orderListObservable;
    component.loadedObservable = loadedSubject;
  });

  afterEach(() => {
    TestBed.resetTestingModule();
  });

  it('#ngOnInit issuePageSubject subscription', () => {
    let order: Order = SimpleOrder();
    orderListObservable.next(order);

    fixture.detectChanges();

    expect(component.order).toEqual(order);
  });

  it('#ngOnInit loadedSubject subscription', () => {
    loadedSubject.next(true);

    fixture.detectChanges();

    expect(component.loaded).toEqual(true);
  });

  it('#formatRepositoryName', () => {
    expect(component.formatRepositoryName('http://github.com/company/my-dummy.git'))
      .toBe('company / <strong>my-dummy</strong>');
  });
});
