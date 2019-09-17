import { Component, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { IssuesStore } from '@app/core/stores/issues.store';
import { NavigationStore } from '@app/core/stores/navigation.store';
import { UserStore } from '@app/core/stores/user.store';
import { NavigationTab } from '@app/shared/components/navigation-tabs/navigation-tab';
import { first } from 'rxjs/operators';
import { Issue } from '../model/issue';
import { IssuePriority } from '../model/issuePriority';
import { IssueService } from '../services/issue.service';
import { DfModalService, DfToasterService } from '@devfactory/ngx-df';

@Component({
  selector: 'app-issues-component',
  templateUrl: './issues.component.html',
  styleUrls: ['./issues.component.scss']
})
export class IssuesComponent implements OnInit {

  @ViewChild('subscribe') subscribe: TemplateRef<null>;

  /**
   * Navigation tab configuration.
   */
  tabs: NavigationTab[];

  constructor(
    private navigationStore: NavigationStore,
    public issuesStore: IssuesStore,
    public userStore: UserStore,
    private issueService: IssueService,
    private toaster: DfToasterService,
    private modalService: DfModalService,
  ) {
  }

  ngOnInit(): void {
    this.navigationStore.tabs.subscribe(tabs => (this.tabs = tabs));
    this.navigationStore.activateBacklogTab();
    this.loadIssues();
  }

  updatedIssuesOrder(issues: Issue[]): void {
    this.issueService.saveIssuesPriority(issues.map(issue => new IssuePriority(issue.order, issue.id)))
      .pipe(first()).subscribe(() => {
      this.toaster.popSuccess(`Issu${issues.length > 1 ? 'es' : 'e'} updated`);
    });
  }

  submitOrder(): void {
    this.modalService.open(this.subscribe);
  }

  private loadIssues(): void {
    this.issueService.getBacklogIssues().pipe(first()).subscribe();
  }
}
