import { LoadingHttpClient } from '@app/core/modules/http/services/loading.http.client';
import { ConfigurationService } from '@app/core/services/configuration.service';
import { OrderStore } from '@app/core/stores/order.store';
import { OrdersService } from './orders.service';
import { of as observableOf, throwError } from 'rxjs';
import { SimpleOrder } from '@app/shared/mocks/order';
import { NoErrorsHttpClient } from '@app/core/modules/http/services/no.errors.http.client';
import { Order } from '../model/order';

describe('OrderService', () => {
  const coverFixUrl: string = 'http://code-fix';
  let loadingHttpClient: jasmine.SpyObj<LoadingHttpClient>;
  let noErrorsHttpClient: jasmine.SpyObj<NoErrorsHttpClient>;
  let configService: jasmine.SpyObj<ConfigurationService>;
  let orderStore: OrderStore = new OrderStore();
  let order: Order = SimpleOrder();

  let testInstance: OrdersService;

  beforeEach(() => {
    noErrorsHttpClient = jasmine.createSpyObj(['get']);
    loadingHttpClient = jasmine.createSpyObj(['get', 'post']);
    configService = jasmine.createSpyObj(['get']);
    configService.get.and.returnValue(coverFixUrl);

    testInstance = new OrdersService(loadingHttpClient, noErrorsHttpClient, configService, orderStore);
  });

  it('submitOrder', () => {
    const orderObservable = observableOf(order);
    loadingHttpClient.post.and.returnValue(orderObservable);

    testInstance.submitOrder().subscribe(response => {
      expect(loadingHttpClient.post).toHaveBeenCalledWith('http://code-fix/api/orders/submit', {});
      expect(response).toEqual(order);
      expect(orderStore.order.getValue()).toEqual(order);
      expect(orderStore.loaded.getValue()).toBe(true);
    });
  });

  it('getActive', () => {
    const orderObservable = observableOf(order);
    loadingHttpClient.get.and.returnValue(orderObservable);

    testInstance.getActive().subscribe(response => {
      expect(loadingHttpClient.get).toHaveBeenCalledWith('http://code-fix/api/orders/active');
      expect(response).toEqual(order);
      expect(orderStore.order.getValue()).toEqual(order);
      expect(orderStore.loaded.getValue()).toBe(true);
    });
  });

  it('checkActive when ok', () => {
    const orderObservable = observableOf(order);
    noErrorsHttpClient.get.and.returnValue(orderObservable);

    testInstance.checkActive().subscribe(response => {
      expect(noErrorsHttpClient.get).toHaveBeenCalledWith('http://code-fix/api/orders/active');
      expect(response).toEqual(true);
      expect(orderStore.loaded.getValue()).toBe(true);
    });
  });

  it('checkActive when errors', () => {
    const errorObservable = throwError('a request error');
    noErrorsHttpClient.get.and.returnValue(errorObservable);

    testInstance.checkActive().subscribe(response => {
      expect(noErrorsHttpClient.get).toHaveBeenCalledWith('http://code-fix/api/orders/active');
      expect(response).toEqual(false);
      expect(orderStore.loaded.getValue()).toBe(false);
    });
  });
});
