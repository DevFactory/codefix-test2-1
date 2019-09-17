import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { LoadingHttpClient } from '@app/core/modules/http/services/loading.http.client';
import { ConfigurationService } from '@app/core/services/configuration.service';
import { catchError, map, tap } from 'rxjs/operators';
import { OrderStore } from '@app/core/stores/order.store';
import { Order } from '../model/order';
import { NoErrorsHttpClient } from '@app/core/modules/http/services/no.errors.http.client';
import { OrderIssue } from '../model/order.issue';

@Injectable()
export class OrdersService {

  constructor(
    private loadingHttpClient: LoadingHttpClient,
    private noErrorsHttpClient: NoErrorsHttpClient,
    private configService: ConfigurationService,
    private orderStore: OrderStore) {
  }

  submitOrder(): Observable<Order> {
    this.orderStore.loaded.next(false);
    return this.loadingHttpClient
      .post<WebOrder>(`${this.ordersUrl()}/submit`, {})
      .pipe(map(order => this.asOrder(order)))
      .pipe(tap(order => this.updateStorage(order, true)));
  }

  getActive(): Observable<Order> {
    this.orderStore.loaded.next(false);
    return this.loadingHttpClient
      .get<WebOrder>(`${this.ordersUrl()}/active`)
      .pipe(map(order => this.asOrder(order)))
      .pipe(tap(order => this.updateStorage(order, true)));
  }

  checkActive(): Observable<boolean> {
    this.orderStore.loaded.next(false);
    return this.noErrorsHttpClient
      .get<WebOrder>(`${this.ordersUrl()}/active`)
      .pipe(tap(order => this.updateStorage(this.asOrder(order))))
      .pipe(map(order => order && true))
      .pipe(catchError(() => of(false)));
  }

  private asOrder(order: WebOrder): Order {
    return new Order(order.startDate, order.dueDate, order.issues.map(issue => this.asIssue(issue)));
  }

  private asIssue(issue: WebOrderIssue): OrderIssue {
    return new OrderIssue(issue.id, issue.type, issue.status, issue.description, issue.repository, issue.branch);
  }

  private updateStorage(order: Order, loaded: boolean = true): void {
    this.orderStore.order.next(order);
    this.orderStore.loaded.next(loaded);
  }

  private ordersUrl(): string {
    return `${this.configService.get('codefixBackend')}/api/orders`;
  }
}

interface WebOrder {
  startDate: Date;
  dueDate: Date;
  issues: WebOrderIssue[];
}

interface WebOrderIssue {
  id: number;
  type: string;
  status: IssueStatus;
  description: string;
  repository: string;
  branch: string;
}

export enum IssueStatus {
  IN_PROCESS = 'IN_PROCESS',
  COMPLETED = 'COMPLETED'
}
