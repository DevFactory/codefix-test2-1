import { Component, HostBinding, OnInit, TemplateRef, ViewChild } from '@angular/core';
import { NavigationTab } from '@app/shared/components/navigation-tabs/navigation-tab';
import { NavigationStore } from '@app/core/stores/navigation.store';
import { ActivatedRoute, Router } from '@angular/router';
import { CodeRampService } from '../../services/code-ramp.service';
import { DelayService } from '@app/core/services/delay.service';
import { RepositoryService } from '../../services/repository.service';
import { RepositoriesStore } from '@app/core/stores/repositories.store';
import { PageRequest } from '@app/core/models/page.request';
import { DfModalService, DfToasterService } from '@devfactory/ngx-df';
import { first, map, tap } from 'rxjs/operators';
import { ActivateRequest } from '../../http/activate.request';
import { Observable } from 'rxjs';
import { Page } from '@app/core/models/page';
import { Repo } from '../../model/repo';
import { UserStore } from '@app/core/stores/user.store';

@Component({
  selector: 'app-repo-list',
  templateUrl: './repo-list.component.html',
  styleUrls: ['./repo-list.component.scss']
})
export class RepoListComponent implements OnInit {

  /**
   * Binding of class on host element.
   */
  @HostBinding('class') hostClasses: string = 'd-block';

  @ViewChild('description') description: TemplateRef<null>;

  /**
   * Navigation tab configuration.
   */
  tabs: NavigationTab[];

  constructor(
    private codeRampService: CodeRampService,
    private repositoryService: RepositoryService,
    private delayService: DelayService,
    private modelService: DfModalService,
    private navigationStore: NavigationStore,
    public repositoryStore: RepositoriesStore,
    public userStore: UserStore,
    private toaster: DfToasterService,
    private activeRouter: ActivatedRoute,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    this.navigationStore.tabs.subscribe(tabs => (this.tabs = tabs));
    this.navigationStore.activateRepositoriesTab();
    this.checkRepositories();
  }

  redirectToCodeRamp(): void {
    const url: string = this.codeRampService.getCodeRampUrl(location);
    this.router.navigate(['/externalRedirect', {externalUrl: url}], {skipLocationChange: true});
  }

  onPageChange(pageRequest: PageRequest): void {
    this.repositoryService.getRepoList(pageRequest).pipe(first()).subscribe();
  }

  analyze(): void {
    this.repositoryService.analyze().pipe(first()).subscribe();
    this.router.navigate(['dashboard', 'issues']);
    this.userStore.isAnalyzing.next(true);
  }

  activateRepos(activateRequest: ActivateRequest): void {
    this.repositoryService.activateRepos(activateRequest).pipe(first()).subscribe(updates => {
      this.repositoryStore.repoPage.getValue().content
        .filter(repo => updates.has(repo.id))
        .forEach(repo => repo.active = updates.get(repo.id).active);
    });
  }

  hasActiveRepo(): boolean {
    return this.repositoryStore.repoPage.getValue() && this.repositoryStore.repoPage.getValue().content
        .some(repo => repo.active);
  }

  private checkRepositories(): void {
    this.activeRouter.queryParams.subscribe(params => {
      const submitted: boolean = Boolean(params['submitted'] || false);
      if (submitted) {
        this.syncRepositories().pipe(first()).subscribe(() => this.showDescriptionModal());
      } else {
        this.loadRepositories();
      }
    });
  }

  private loadRepositories(): void {
    this.repositoryService.getRepoList(new PageRequest(1)).subscribe(repoPage => {
      if (repoPage.isEmpty()) {
        this.syncAndRedirectIfEmpty();
      } else {
        this.showDescriptionModal();
      }
    });
  }

  private syncAndRedirectIfEmpty(): void {
    this.syncRepositories().subscribe(repos => {
      if (repos.isEmpty()) {
        this.delayService.execute(/* istanbul ignore next */() => this.redirectToCodeRamp(), 3);
      }
    });
  }

  private syncRepositories(): Observable<Page<Repo>> {
    return this.repositoryService.syncRepos(new PageRequest(1))
      .pipe(tap(result => result.ignoredItems.forEach(repo => this.showRepoMessage(repo.reason))))
      .pipe(map(syncResult => syncResult.repositories));
  }

  private showRepoMessage(message: string): void {
    this.toaster.popWarning(message).pipe(first()).subscribe();
  }

  private showDescriptionModal(): void {
    if (this.repositoryStore.showModal.getValue()) {
      this.modelService.open(this.description, {customClass: 'repo-list-description-modal'});
    }
  }
}
