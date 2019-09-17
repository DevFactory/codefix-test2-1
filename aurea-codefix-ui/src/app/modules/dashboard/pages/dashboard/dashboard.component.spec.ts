import { DashboardComponent } from './dashboard.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AuthService } from '@app/core/services/auth/auth.service';
import { UserStore } from '@app/core/stores/user.store';
import { DfUserProfileModule } from '@devfactory/ngx-df/user-profile';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { NgbDropdownModule } from '@ng-bootstrap/ng-bootstrap';
import { CompletedService } from 'app/modules/issues/completed/services/completed.service';
import { IssueService } from 'app/modules/issues/backlog/services/issue.service';
import { IssuesStore } from '@app/core/stores/issues.store';
import { CompletedStore } from '@app/core/stores/completed.store';
import { NavigationStore } from '@app/core/stores/navigation.store';
import { CompletedResult } from '@app/shared/mocks/completed';
import { of } from 'rxjs';
import { IssueList } from '@app/shared/mocks/issue';
import { OrdersService } from '../../../issues/process/services/orders.service';
import { OrderStore } from '@app/core/stores/order.store';
import { Issue } from '../../../issues/backlog/model/issue';
import { Completed } from '../../../issues/completed/model/completed';

describe('DashboardComponent', () => {

  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let issueService: jasmine.SpyObj<IssueService>;
  let completedService: jasmine.SpyObj<CompletedService>;
  let ordersService: jasmine.SpyObj<OrdersService>;
  let navigationStore: jasmine.SpyObj<NavigationStore>;

  const userStorage: UserStore = new UserStore();
  const issuesStore: IssuesStore = new IssuesStore();
  const completedStore: CompletedStore = new CompletedStore();
  const orderStore: OrderStore = new OrderStore();

  beforeEach(() => {
    authService = jasmine.createSpyObj(['logout']);
    issueService = jasmine.createSpyObj(['getBacklogIssues']);
    completedService = jasmine.createSpyObj(['getCompletedIssues']);
    ordersService = jasmine.createSpyObj(['checkActive']);
    navigationStore = jasmine.createSpyObj(['enableBacklogTab', 'enableInProcessTab', 'enableDoneTab']);

    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [DashboardComponent],
      imports: [
        DfUserProfileModule.forRoot(),
        NgbDropdownModule.forRoot()],
      providers: [
        {provide: AuthService, useValue: authService},
        {provide: IssueService, useValue: issueService},
        {provide: CompletedService, useValue: completedService},
        {provide: OrdersService, useValue: ordersService},
        {provide: UserStore, useValue: userStorage},
        {provide: OrderStore, useValue: orderStore},
        {provide: IssuesStore, useValue: issuesStore},
        {provide: CompletedStore, useValue: completedStore},
        {provide: NavigationStore, useValue: navigationStore}
      ]
    }).compileComponents();
    issueService.getBacklogIssues.and.returnValue(of(IssueList));
    issuesStore.issuesList.next(IssueList);
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  });

  afterEach(() => {
    TestBed.resetTestingModule();
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('it should call auth0.logout on logout', () => {
    component.logout();

    expect(authService.logout).toHaveBeenCalled();
  });

  it('#ngOnInit when enable', () => {
    mockBacklogIssues(IssueList);
    mockCompletedList(CompletedResult);
    mockCheckActiveOrder(true);

    component.ngOnInit();

    expect(navigationStore.enableInProcessTab).toHaveBeenCalled();
    expect(navigationStore.enableDoneTab).toHaveBeenCalled();
    expect(navigationStore.enableBacklogTab).toHaveBeenCalled();
  });

  it('#ngOnInit when not enable', () => {
    mockBacklogIssues([]);
    mockCompletedList([]);
    mockCheckActiveOrder(false);

    component.ngOnInit();

    expect(navigationStore.enableInProcessTab).not.toHaveBeenCalled();
    expect(navigationStore.enableDoneTab).not.toHaveBeenCalled();
    expect(navigationStore.enableBacklogTab).not.toHaveBeenCalled();
  });

  function mockBacklogIssues(issues: Issue[]): void {
    issueService.getBacklogIssues.and.returnValue(of(issues));
    issuesStore.issuesList.next(issues);
  }

  function mockCompletedList(issues: Completed[]): void {
    completedService.getCompletedIssues.and.returnValue(of(issues));
    completedStore.completedList.next(issues);
  }

  function mockCheckActiveOrder(value: boolean): void {
    ordersService.checkActive.and.returnValue(of(value));
    orderStore.loaded.next(value);
  }
});
