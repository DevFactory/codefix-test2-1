import { ConfigurationService } from '@app/core/services/configuration.service';
import { Injectable } from '@angular/core';
import { map, tap } from 'rxjs/operators';
import { RepositoriesStore } from '@app/core/stores/repositories.store';
import { Observable } from 'rxjs';
import { Repo } from '../model/repo';
import { PageRequest } from '@app/core/models/page.request';
import { Page } from '@app/core/models/page';
import { ActivateRequest } from '../http/activate.request';
import { SilentHttpClient } from '@app/core/modules/http/services/silent.http.client';
import { LoadingHttpClient } from '@app/core/modules/http/services/loading.http.client';
import { SyncResult } from '../model/sync.resutl';
import { IgnoredRepo } from '../model/ignored-repo';

@Injectable()
export class RepositoryService {

  private static defaultLimit: number = 20;

  constructor(
    private loadingHttpClient: LoadingHttpClient,
    private silentHttpClient: SilentHttpClient,
    private configService: ConfigurationService,
    private reposStore: RepositoriesStore) {
  }

  getRepoList(pageRequest: PageRequest): Observable<Page<Repo>> {
    this.reposStore.loaded.next(false);
    return this.loadingHttpClient.get<RepoPage<WebRepo>>(this.reposUrl(pageRequest.page))
      .pipe(map(page => this.asRepoPage(page)))
      .pipe(tap(result => this.updateRepoStorage(result)));
  }

  syncRepos(pageRequest: PageRequest): Observable<SyncResult> {
    this.reposStore.loaded.next(false);
    return this.loadingHttpClient.post<WebSyncResult>(this.syncUrl(pageRequest.page), null)
      .pipe(map(result => new SyncResult(this.asRepoPage(result.repositories), result.ignoredItems)))
      .pipe(tap(result => this.updateRepoStorage(result.repositories)));
  }

  activateRepos(activateRequest: ActivateRequest): Observable<Map<number, Repo>> {
    return this.silentHttpClient.patch<WebRepo[]>(`${this.repoUrl()}/activation`, activateRequest)
      .pipe(map(repos => new Map(repos.map(repo => [repo.id, this.asRepo(repo)] as [number, Repo]))));
  }

  private asRepoPage(page: RepoPage<WebRepo>): Page<Repo> {
    return new Page<Repo>(page.currentPage, page.pages, page.total, page.content.map(rep => this.asRepo(rep)));
  }

  private asRepo(repo: WebRepo): Repo {
    return new Repo(repo.id, repo.branch, repo.url, repo.active);
  }

  private updateRepoStorage(repositories: Page<Repo>): void {
    this.reposStore.repoPage.next(repositories);
    this.reposStore.loaded.next(true);
  }

  private reposUrl(page: number): string {
    return `${this.repoUrl()}?page=${page}&limit=${RepositoryService.defaultLimit}`;
  }

  private syncUrl(page: number): string {
    return `${this.repoUrl()}/sync?page=${page}&limit=${RepositoryService.defaultLimit}`;
  }

  analyze(): Observable<void> {
    return this.loadingHttpClient.post<void>(`${this.repoUrl()}/analyze`, null);
  }

  private repoUrl(): string {
    return `${this.configService.get('codefixBackend')}/api/repositories`;
  }
}

interface WebRepo {
  id: number;
  url: string;
  branch: string;
  active: boolean;
}

interface RepoPage<T> {
  currentPage: number;
  pages: number;
  total: number;
  content: T[];
}

interface WebSyncResult {
  repositories: RepoPage<WebRepo>;
  ignoredItems: IgnoredRepo[];
}
