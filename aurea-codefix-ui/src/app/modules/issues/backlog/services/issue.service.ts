import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Issue } from '../model/issue';
import { LoadingHttpClient } from '@app/core/modules/http/services/loading.http.client';
import { ConfigurationService } from '@app/core/services/configuration.service';
import { NavigationStore } from '@app/core/stores/navigation.store';
import { IssuesStore } from '@app/core/stores/issues.store';
import { tap } from 'rxjs/operators';
import { SilentHttpClient } from '@app/core/modules/http/services/silent.http.client';
import { IssuePriority } from '../model/issuePriority';
import { HttpResponse } from '@angular/common/http';

@Injectable()
export class IssueService {

  constructor(
    private loadingHttpClient: LoadingHttpClient,
    private silentHttpClient: SilentHttpClient,
    private configService: ConfigurationService,
    private issueStore: IssuesStore,
    private navigationStore: NavigationStore) {
  }

  getBacklogIssues(): Observable<Issue[]> {
    this.issueStore.loaded.next(false);
    return this.loadingHttpClient.get<WebIssue[]>(this.issueUrlBacklog())
      .pipe(tap(issueList => this.updateIssueStorage(issueList)));
  }

  saveIssuesPriority(issueList: IssuePriority[]): Observable<HttpResponse<null>> {
    return this.silentHttpClient.post<HttpResponse<null>>(this.issueUrlSaveOrder(), issueList);
  }

  private issueUrlBacklog(): string {
    return `${this.configService.get('codefixBackend')}/api/issues/backlog`;
  }

  private issueUrlSaveOrder(): string {
    return `${this.configService.get('codefixBackend')}/api/issues/priority`;
  }

  private updateIssueStorage(issues: Issue[]): void {
    this.issueStore.issuesList.next(issues.sort((a, b) => a.order - b.order));
    this.issueStore.loaded.next(true);
    this.navigationStore.setBacklogSize(this.issueStore.issuesList.getValue().length);
  }
}

interface WebIssue {
  id?: number;
  type?: string;
  description?: string;
  repository?: string;
  branch?: string;
  issueUrl?: string;
}
