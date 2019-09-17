import { ConfigurationService } from '@app/core/services/configuration.service';
import { of as observableOf } from 'rxjs';
import { LoadingHttpClient } from '@app/core/modules/http/services/loading.http.client';
import { SilentHttpClient } from '@app/core/modules/http/services/silent.http.client';
import { CompletedStore } from '@app/core/stores/completed.store';
import { CompletedService } from './completed.service';
import { CompletedResult } from '@app/shared/mocks/completed';

describe('IssuesService', () => {
  const backEndUrl: string = 'http://backend';

  let loadingHttpClient: jasmine.SpyObj<LoadingHttpClient>;
  let silentHttpClient: jasmine.SpyObj<SilentHttpClient>;
  let configService: jasmine.SpyObj<ConfigurationService>;
  let completedStore: CompletedStore = new CompletedStore();

  let testInstance: CompletedService;

  beforeEach(() => {
    loadingHttpClient = jasmine.createSpyObj(['get']);
    silentHttpClient = jasmine.createSpyObj(['patch']);
    configService = jasmine.createSpyObj(['get']);
    configService.get.and.returnValue(backEndUrl);

    testInstance = new CompletedService(loadingHttpClient, configService, completedStore);
  });

  it('#getCompletedIssues', () => {
    const completedObservable = observableOf(CompletedResult);
    loadingHttpClient.get.and.returnValue(completedObservable);

    testInstance.getCompletedIssues().subscribe(response => {
      expect(loadingHttpClient.get).toHaveBeenCalledWith('http://backend/api/issues/completed');
      expect(response).toEqual(CompletedResult);
      expect(completedStore.loaded.getValue()).toBe(true);
      expect(completedStore.completedList.getValue().length).toBe(1);
    });
  });
});
