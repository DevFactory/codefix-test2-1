import { ConfigurationService } from '@app/core/services/configuration.service';
import { RepositoriesStore } from '@app/core/stores/repositories.store';
import { RepositoryService } from './repository.service';
import { of as observableOf, Observable } from 'rxjs'
import { PageRequest } from '@app/core/models/page.request';
import { RepoPage, SimpleRepo, SimpleSyncResult } from '@app/shared/mocks/repo.page';
import { ActivateRequest } from '../http/activate.request';
import { LoadingHttpClient } from '@app/core/modules/http/services/loading.http.client';
import { SilentHttpClient } from '@app/core/modules/http/services/silent.http.client';

describe('RepositoryService', () => {
  const coverServerUrl: string = 'http://code-server';

  let loadingHttpClient: jasmine.SpyObj<LoadingHttpClient>;
  let silentHttpClient: jasmine.SpyObj<SilentHttpClient>;
  let configService: jasmine.SpyObj<ConfigurationService>;
  let reposStore: RepositoriesStore = new RepositoriesStore();

  let testInstance: RepositoryService;

  beforeEach(() => {
    loadingHttpClient = jasmine.createSpyObj(['get', 'post', 'patch']);
    silentHttpClient = jasmine.createSpyObj(['patch']);
    configService = jasmine.createSpyObj(['get']);
    configService.get.and.returnValue(coverServerUrl);

    testInstance = new RepositoryService(loadingHttpClient, silentHttpClient, configService, reposStore);
  });

  it('#getRepoList', () => {
    const repoObservable = observableOf(RepoPage);
    loadingHttpClient.get.and.returnValue(repoObservable);

    testInstance.getRepoList(new PageRequest(1)).subscribe(response => {
      expect(loadingHttpClient.get).toHaveBeenCalledWith('http://code-server/api/repositories?page=1&limit=20');
      expect(response).toEqual(RepoPage);
      expect(reposStore.repoPage.getValue()).toEqual(RepoPage);
      expect(reposStore.loaded.getValue()).toBe(true);
    });
  });

  it('#syncRepos', () => {
    loadingHttpClient.post.and.returnValue(observableOf(SimpleSyncResult));

    testInstance.syncRepos(new PageRequest(1)).subscribe(response => {
      expect(loadingHttpClient.post).toHaveBeenCalledWith('http://code-server/api/repositories/sync?page=1&limit=20', null);
      expect(response).toEqual(SimpleSyncResult);
      expect(reposStore.repoPage.getValue()).toEqual(RepoPage);
      expect(reposStore.loaded.getValue()).toBe(true);
    });
  });

  it('#activateRepos', () => {
    const activationResult = [SimpleRepo];
    const request = ActivateRequest.fromSingle(50, true);

    silentHttpClient.patch.and.returnValue(observableOf([activationResult]));

    testInstance.activateRepos(request).subscribe(response => {
      expect(silentHttpClient.patch).toHaveBeenCalledWith('http://code-server/api/repositories/activation', request);
      expect(response.get(50)).not.toBeNull();
    });
  });

  it('#analyze', () => {
    loadingHttpClient.post.and.returnValue(new Observable<void>());
    testInstance.analyze().subscribe(response => {
        expect(loadingHttpClient.post).toHaveBeenCalledWith('http://code-server/api/repositories/analyze', null);
    });
  });
})
;
