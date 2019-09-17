import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { NavigationStore } from '@app/core/stores/navigation.store';
import { IssuesStore } from '@app/core/stores/issues.store';
import { IssuesComponent } from './issues.component';
import { IssueService } from '../services/issue.service';
import { IssueList, SimpleIssue } from '@app/shared/mocks/issue';
import { of } from 'rxjs';
import { UserStore } from '@app/core/stores/user.store';
import { IssuePriority } from '../model/issuePriority';
import { HttpResponse } from '@angular/common/http';
import { DfModalService, DfToasterService } from '@devfactory/ngx-df';

describe('BacklogIssuesComponent', () => {

  let component: IssuesComponent;
  let fixture: ComponentFixture<IssuesComponent>;
  let issueService: jasmine.SpyObj<IssueService>;
  let paramMap: any;
  let toaster: jasmine.SpyObj<DfToasterService>;
  let modalService: jasmine.SpyObj<DfModalService>;

  const navigationStore: NavigationStore = new NavigationStore();
  const issuesStore: IssuesStore = new IssuesStore();
  const userStore: UserStore = new UserStore();

  beforeEach(() => {
    paramMap = {};
    issueService = jasmine.createSpyObj(['getBacklogIssues', 'saveIssuesPriority']);
    modalService = jasmine.createSpyObj(['open']);
    toaster = jasmine.createSpyObj(['popSuccess']);

    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [IssuesComponent],
      providers: [
        {provide: NavigationStore, useValue: navigationStore},
        {provide: IssueService, useValue: issueService},
        {provide: IssuesStore, useValue: issuesStore},
        {provide: UserStore, useValue: userStore},
        {provide: DfToasterService, useValue: toaster},
        {provide: DfModalService, useValue: modalService},
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IssuesComponent);
    component = fixture.componentInstance;
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('#ngOnInit', () => {
    issueService.getBacklogIssues.and.returnValue(of(IssueList));

    component.ngOnInit();

    expect(component.tabs).toBe(navigationStore.tabs.getValue());
    expect(issueService.getBacklogIssues).toHaveBeenCalled();
  });

  it('#updatedIssuesOrder list', () => {
    issueService.saveIssuesPriority.and.returnValue(of(new HttpResponse<null>()));

    component.updatedIssuesOrder(IssueList);

    expect(issueService.saveIssuesPriority)
      .toHaveBeenCalledWith(IssueList.map(issue => new IssuePriority(issue.order, issue.id)));
  });

  it('#updatedIssuesOrder one issue', () => {
    issueService.saveIssuesPriority.and.returnValue(of(new HttpResponse<null>()));

    component.updatedIssuesOrder([SimpleIssue]);

    expect(issueService.saveIssuesPriority)
      .toHaveBeenCalledWith([new IssuePriority(SimpleIssue.order, SimpleIssue.id)]);
  });

  it('#submitOrder', () => {
    component.submitOrder();

    expect(modalService.open).toHaveBeenCalled();
  });
});


