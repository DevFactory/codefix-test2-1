import { Component, HostBinding, OnInit } from '@angular/core';
import { User } from '@app/core/models/user';
import { UserStore } from '@app/core/stores/user.store';
import { AuthService } from '@app/core/services/auth/auth.service';
import { CompletedStore } from '@app/core/stores/completed.store';
import { CompletedService } from 'app/modules/issues/completed/services/completed.service';
import { NavigationStore } from '@app/core/stores/navigation.store';
import { IssueService } from 'app/modules/issues/backlog/services/issue.service';
import { first } from 'rxjs/operators';
import { Issue } from '../../../issues/backlog/model/issue';
import { IssuesStore } from '@app/core/stores/issues.store';
import { OrdersService } from 'app/modules/issues/process/services/orders.service';
import { OrderStore } from '@app/core/stores/order.store';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss'],
})
export class DashboardComponent implements OnInit {

  user: User;

  @HostBinding() class: string = 'd-flex flex-column col p-0';

  constructor(public authService: AuthService,
              private ordersService: OrdersService,
              public completedService: CompletedService,
              private orderStore: OrderStore,
              private completedStore: CompletedStore,
              public issueService: IssueService,
              private userStore: UserStore,
              private issuesStore: IssuesStore,
              private navigationStore: NavigationStore) {
  }

  ngOnInit(): void {
    this.userStore.user.subscribe(user => this.user = user);

    this.checkCompletedIssues();
    this.checkActiveOrder();
    this.checkBackLogIssues();
  }

  private checkBackLogIssues(): void {
    this.issueService.getBacklogIssues().pipe(first()).subscribe(() => {
      this.issuesStore.issuesList.subscribe(it => this.enableBacklogTab(it));
    });
  }

  private checkActiveOrder(): void {
    this.ordersService.checkActive().pipe(first()).subscribe(() => {
      this.orderStore.loaded.subscribe(it => this.enableInProcessTab(it));
    });
  }

  private checkCompletedIssues(): void {
    this.completedService.getCompletedIssues().pipe(first()).subscribe(() => {
      this.completedStore.completedList.subscribe(it => this.enableDoneTab(it));
    });
  }

  enableBacklogTab(issues: Issue[]): void {
    if (issues.length > 0) {
      this.navigationStore.enableBacklogTab();
    }
  }

  enableInProcessTab(order: boolean): void {
    if (order) {
      this.navigationStore.enableInProcessTab();
    }
  }

  enableDoneTab(issues: Issue[]): void {
    if (issues.length > 0) {
      this.navigationStore.enableDoneTab();
    }
  }

  logout(): void {
    this.authService.logout();
  }
}
