import { Component, Input } from '@angular/core';
import { OrdersService } from '../../../process/services/orders.service';
import { first } from 'rxjs/operators';

@Component({
  selector: 'app-subscribe-modal',
  templateUrl: './subscribe.component.html'
})
export class SubscribeComponent {

  @Input() close: Function;

  constructor(private ordersService: OrdersService) {
  }

  submitOrder(): void {
    this.ordersService.submitOrder().pipe(first()).subscribe(() => {
      this.close();
    });
  }

}
