import { Component, HostBinding, OnInit } from '@angular/core';
import { NavigationTab } from '@app/shared/components/navigation-tabs/navigation-tab';
import { NavigationStore } from '@app/core/stores/navigation.store';
import { OrderStore } from '@app/core/stores/order.store';
import { first } from 'rxjs/operators';
import { OrdersService } from '../services/orders.service';

@Component({
  selector: 'app-issues-component',
  templateUrl: './in-process-issues.component.html',
  styleUrls: ['./in-process-issues.component.scss']
})
export class InProcessIssuesComponent implements OnInit {

  @HostBinding()
  class: string = 'd-flex flex-column col px-0 pt-3';

  /**
   * Navigation tab configuration.
   */
  tabs: NavigationTab[];

  constructor(
    public orderStore: OrderStore,
    private orderService: OrdersService,
    private navigationStore: NavigationStore
  ) {
  }

  ngOnInit(): void {
    this.navigationStore.tabs.subscribe(tabs => (this.tabs = tabs));
    this.navigationStore.activateProcessTab();
    this.loadOrder();
  }

  private loadOrder(): void {
    this.orderService.getActive().pipe(first()).subscribe();
  }
}
