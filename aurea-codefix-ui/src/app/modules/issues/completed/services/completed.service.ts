import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Completed } from '../model/completed';
import { LoadingHttpClient } from '@app/core/modules/http/services/loading.http.client';
import { ConfigurationService } from '@app/core/services/configuration.service';
import { CompletedStore } from '@app/core/stores/completed.store';
import { tap } from 'rxjs/operators';

@Injectable()
export class CompletedService {

  constructor(
    private loadingHttpClient: LoadingHttpClient,
    private configService: ConfigurationService,
    private completedStore: CompletedStore) {
  }

  getCompletedIssues(): Observable<Completed[]> {
    this.completedStore.loaded.next(false);
    return this.loadingHttpClient.get<Completed[]>(this.completedUrl())
      .pipe(tap(list => this.updateCompletedStorage(list)));
  }

  private completedUrl(): string {
    return `${this.configService.get('codefixBackend')}/api/issues/completed`;
  }

  private updateCompletedStorage(completedList: any): void {
    this.completedStore.completedList.next(completedList);
    this.completedStore.loaded.next(true);
  }
}
