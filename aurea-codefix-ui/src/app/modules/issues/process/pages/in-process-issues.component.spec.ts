import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { NavigationStore } from '@app/core/stores/navigation.store';
import { of } from 'rxjs';
import { InProcessIssuesComponent } from './in-process-issues.component';
import { OrderStore } from '@app/core/stores/order.store';
import { OrdersService } from '../services/orders.service';
import { SimpleOrder } from '@app/shared/mocks/order';

describe('InProcessIssuesComponent', () => {

  let component: InProcessIssuesComponent;
  let fixture: ComponentFixture<InProcessIssuesComponent>;
  let ordersService: jasmine.SpyObj<OrdersService>;

  const navigationStore: NavigationStore = new NavigationStore();
  const orderStore: OrderStore = new OrderStore();

  beforeEach(() => {
    ordersService = jasmine.createSpyObj(['getActive']);

    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [InProcessIssuesComponent],
      providers: [
        {provide: NavigationStore, useValue: navigationStore},
        {provide: OrderStore, useValue: orderStore},
        {provide: OrdersService, useValue: ordersService}
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InProcessIssuesComponent);
    component = fixture.componentInstance;
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('#ngOnInit', () => {
    ordersService.getActive.and.returnValue(of(SimpleOrder));

    component.ngOnInit();

    expect(component.tabs).toBe(navigationStore.tabs.getValue());
    expect(ordersService.getActive).toHaveBeenCalled();
  });
});

