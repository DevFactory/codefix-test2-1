import { OrderStore } from '@app/core/stores/order.store';

describe('OrderStore', () => {

  const testInstance: OrderStore = new OrderStore();

  it('by default not tab should be enable', () => {
    expect(testInstance.order.getValue()).toEqual(null);
    expect(testInstance.loaded.getValue()).toEqual(false);
  });
});
