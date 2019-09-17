import { BehaviorSubject } from 'rxjs';
import { Order } from '../../modules/issues/process/model/order';

export class OrderStore {

  order: BehaviorSubject<Order> = new BehaviorSubject(null);
  loaded: BehaviorSubject<boolean> = new BehaviorSubject(false);
}
