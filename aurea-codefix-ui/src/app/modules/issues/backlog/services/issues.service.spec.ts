import { ConfigurationService } from '@app/core/services/configuration.service';
import { of as observableOf } from 'rxjs';
import { LoadingHttpClient } from '@app/core/modules/http/services/loading.http.client';
import { SilentHttpClient } from '@app/core/modules/http/services/silent.http.client';
import { IssuesStore } from '@app/core/stores/issues.store';
import { NavigationStore } from '@app/core/stores/navigation.store';
import { IssueService } from './issue.service';
import { IssueList, IssuePriorityList } from '@app/shared/mocks/issue';
import { HttpResponse } from '@angular/common/http';

describe('IssuesService', () => {
  const backEndUrl: string = 'http://backend';

  let loadingHttpClient: jasmine.SpyObj<LoadingHttpClient>;
  let silentHttpClient: jasmine.SpyObj<SilentHttpClient>;
  let configService: jasmine.SpyObj<ConfigurationService>;
  const issuesStore: IssuesStore = new IssuesStore();
  let navigationStore: jasmine.SpyObj<NavigationStore>;

  let testInstance: IssueService;

  beforeEach(() => {
    loadingHttpClient = jasmine.createSpyObj(['get', 'post', 'patch']);
    silentHttpClient = jasmine.createSpyObj(['post', 'patch']);
    navigationStore = jasmine.createSpyObj(['setBacklogSize']);
    configService = jasmine.createSpyObj(['get']);
    configService.get.and.returnValue(backEndUrl);

    testInstance = new IssueService(loadingHttpClient, silentHttpClient, configService, issuesStore, navigationStore);
  });

  it('#getIssueList', () => {
    const issueObservable = observableOf(IssueList);
    loadingHttpClient.get.and.returnValue(issueObservable);

    testInstance.getBacklogIssues().subscribe(response => {
      expect(loadingHttpClient.get).toHaveBeenCalledWith('http://backend/api/issues/backlog');
      expect(response).toEqual(IssueList);
      expect(issuesStore.issuesList.getValue()).toEqual(IssueList.sort((a, b) => a.order - b.order));
      expect(issuesStore.loaded.getValue()).toBe(true);
      expect(navigationStore.setBacklogSize).toHaveBeenCalledWith(2);
    });
  });

  it('#saveIssuesPriority', () => {
    silentHttpClient.post.and.returnValue(observableOf(new HttpResponse<null>()));
    testInstance.saveIssuesPriority(IssuePriorityList).subscribe(response => {
      expect(silentHttpClient.post).toHaveBeenCalledWith('http://backend/api/issues/priority',
        IssuePriorityList);
    });
  });
});

