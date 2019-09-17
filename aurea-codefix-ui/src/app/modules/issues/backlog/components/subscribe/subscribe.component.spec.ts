import { ComponentFixture, TestBed } from '@angular/core/testing';
import { OrdersService } from '../../../process/services/orders.service';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { SubscribeComponent } from './subscribe.component';
import { of } from 'rxjs';
import { SimpleOrder } from '@app/shared/mocks/order';

describe('SubscribeComponent', () => {

  let component: SubscribeComponent;
  let fixture: ComponentFixture<SubscribeComponent>;
  let ordersService: jasmine.SpyObj<OrdersService>;
  let closeFunction: jasmine.SpyObj<Function>;

  beforeEach(() => {
    ordersService = jasmine.createSpyObj(['submitOrder']);
    closeFunction = jasmine.createSpyObj(['call']);

    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [SubscribeComponent],
      providers: [
        {provide: OrdersService, useValue: ordersService}
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SubscribeComponent);
    component = fixture.componentInstance;
    component.close = () => closeFunction.call();
  });

  afterEach(() => {
    TestBed.resetTestingModule();
  });

  it('#submitOrder', () => {
    ordersService.submitOrder.and.returnValue(of(SimpleOrder));

    component.submitOrder();
    fixture.detectChanges();

    expect(ordersService.submitOrder).toHaveBeenCalled();
    expect(closeFunction.call).toHaveBeenCalled();
  });

});
