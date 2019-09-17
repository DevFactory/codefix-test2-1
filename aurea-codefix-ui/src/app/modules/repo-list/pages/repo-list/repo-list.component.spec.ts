import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { RepoListComponent } from './repo-list.component';
import { NavigationStore } from '@app/core/stores/navigation.store';
import { CodeRampService } from '../../services/code-ramp.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, of } from 'rxjs';
import { DelayService } from '@app/core/services/delay.service';
import { RepositoryService } from '../../services/repository.service';
import { PageRequest } from '@app/core/models/page.request';
import {
  EmptyRepoPage,
  EmptySyncResult,
  IgnoredRepoMessage,
  IgnoredSyncResult,
  RepoPage,
  SimpleRepo,
  SimpleRepoId,
  SimpleSyncResult
} from '@app/shared/mocks/repo.page';
import { RepositoriesStore } from '@app/core/stores/repositories.store';
import { DfModalService, DfToasterService } from '@devfactory/ngx-df';
import { MapUtils } from '@app/shared/utilities/base/map.utils';
import { ActivateRequest } from '../../http/activate.request';
import { Page } from '@app/core/models/page';
import { Repo } from '../../model/repo';
import { UserStore } from '@app/core/stores/user.store';
import { AUTH_USER } from '@app/shared/mocks/auth0.tocken';

describe('RepoListComponent', () => {

  let component: RepoListComponent;
  let fixture: ComponentFixture<RepoListComponent>;

  let codeRampService: jasmine.SpyObj<CodeRampService>;
  let toasterService: jasmine.SpyObj<DfToasterService>;
  let delayService: jasmine.SpyObj<DelayService>;
  let repositoryService: jasmine.SpyObj<RepositoryService>;
  let modalService: jasmine.SpyObj<DfModalService>;
  let router: jasmine.SpyObj<Router>;
  let paramMap: any;

  const navigationStore: NavigationStore = new NavigationStore();
  const repositoryStore: RepositoriesStore = new RepositoriesStore();
  const userStore: UserStore = new UserStore();

  beforeEach(() => {
    paramMap = {};
    codeRampService = jasmine.createSpyObj(['getCodeRampUrl']);
    delayService = jasmine.createSpyObj(['execute']);
    repositoryService = jasmine.createSpyObj(['syncRepos', 'getRepoList', 'activateRepos', 'analyze']);
    modalService = jasmine.createSpyObj(['open']);
    toasterService = jasmine.createSpyObj(['popWarning']);
    router = jasmine.createSpyObj(['navigate']);

    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [RepoListComponent],
      providers: [
        {provide: NavigationStore, useValue: navigationStore},
        {provide: RepositoriesStore, useValue: repositoryStore},
        {provide: UserStore, useValue: userStore},
        {provide: CodeRampService, useValue: codeRampService},
        {provide: RepositoryService, useValue: repositoryService},
        {provide: DelayService, useValue: delayService},
        {provide: DfModalService, useValue: modalService},
        {provide: DfToasterService, useValue: toasterService},
        {provide: ActivatedRoute, useFactory: () => new Object({queryParams: of(paramMap)})},
        {provide: Router, useValue: router}
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RepoListComponent);
    component = fixture.componentInstance;
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('#ngOnInit when update param', () => {
    repositoryService.syncRepos.and.returnValue(of(SimpleSyncResult));
    paramMap['submitted'] = 1;

    component.ngOnInit();

    expect(component.tabs).toBe(navigationStore.tabs.getValue());
    expect(repositoryService.syncRepos).toHaveBeenCalledWith(new PageRequest(1));
    expect(delayService.execute).not.toHaveBeenCalled();
    expect(modalService.open).toHaveBeenCalled();
  });

  it('#ngOnInit when update param and ignored repos', () => {
    repositoryService.syncRepos.and.returnValue(of(IgnoredSyncResult));
    toasterService.popWarning.and.returnValue(of(1));
    paramMap['submitted'] = 1;

    component.ngOnInit();

    expect(component.tabs).toBe(navigationStore.tabs.getValue());
    expect(repositoryService.syncRepos).toHaveBeenCalledWith(new PageRequest(1));
    expect(toasterService.popWarning).toHaveBeenCalledWith(IgnoredRepoMessage);
    expect(delayService.execute).not.toHaveBeenCalled();
  });

  it('#ngOnInit when no update param and repos and modal should be displayed', () => {
    paramMap['submitted'] = 0;
    repositoryService.getRepoList.and.returnValue(of(RepoPage));
    repositoryStore.showModal.next(true);

    component.ngOnInit();

    expect(component.tabs).toBe(navigationStore.tabs.getValue());
    expect(repositoryService.getRepoList).toHaveBeenCalledWith(new PageRequest(1));
    expect(delayService.execute).not.toHaveBeenCalled();
    expect(modalService.open).toHaveBeenCalled();
  });

  it('#ngOnInit when no update param and repos and modal should not be displayed', () => {
    paramMap['submitted'] = 0;
    repositoryService.getRepoList.and.returnValue(of(RepoPage));
    repositoryStore.showModal.next(false);

    component.ngOnInit();

    expect(component.tabs).toBe(navigationStore.tabs.getValue());
    expect(repositoryService.getRepoList).toHaveBeenCalledWith(new PageRequest(1));
    expect(delayService.execute).not.toHaveBeenCalled();
    expect(modalService.open).not.toHaveBeenCalled();
  });

  it('#ngOnInit when no update param and no repos retrieved not synced', () => {
    paramMap['submitted'] = 0;
    repositoryService.getRepoList.and.returnValue(of(EmptyRepoPage));
    repositoryService.syncRepos.and.returnValue(of(EmptySyncResult));

    component.ngOnInit();

    expect(component.tabs).toBe(navigationStore.tabs.getValue());
    expect(repositoryService.getRepoList).toHaveBeenCalledWith(new PageRequest(1));
    expect(repositoryService.syncRepos).toHaveBeenCalledWith(new PageRequest(1));
    expect(delayService.execute).toHaveBeenCalled();
  });

  it('#ngOnInit when no update param and no repos retrieved but synced', () => {
    paramMap['submitted'] = 0;
    repositoryService.getRepoList.and.returnValue(of(EmptyRepoPage));
    repositoryService.syncRepos.and.returnValue(of(SimpleSyncResult));

    component.ngOnInit();

    expect(component.tabs).toBe(navigationStore.tabs.getValue());
    expect(repositoryService.getRepoList).toHaveBeenCalledWith(new PageRequest(1));
    expect(repositoryService.syncRepos).toHaveBeenCalledWith(new PageRequest(1));
    expect(delayService.execute).not.toHaveBeenCalled();
  });

  it('#redirectToCodeRamp', () => {
    codeRampService.getCodeRampUrl.and.returnValue('coderampUrl');

    component.redirectToCodeRamp();

    expect(router.navigate).toHaveBeenCalledWith(
      ['/externalRedirect', {externalUrl: 'coderampUrl'}], {skipLocationChange: true});
  });

  it('#onPageChange', () => {
    repositoryService.getRepoList.and.returnValue(of(EmptyRepoPage));

    const pageRequest = new PageRequest(10);
    component.onPageChange(pageRequest);

    expect(repositoryService.getRepoList).toHaveBeenCalledWith(pageRequest);
  });

  it('#analyze', () => {
    userStore.initIsAnalyzing(AUTH_USER);
    repositoryService.analyze.and.returnValue(new Observable<void>());

    component.analyze();

    expect(repositoryService.analyze).toHaveBeenCalled();
  });

  describe('activateRepos', () => {

    let request: ActivateRequest;
    let currentPage: Page<Repo>;

    beforeEach(() => {
      request = ActivateRequest.fromSingle(SimpleRepoId, true);
      currentPage = new Page<Repo>(1, 1, 1, [SimpleRepo.withActive(true)]);
    });

    it('#activateRepos when should be updated', () => {
      repositoryService.activateRepos.and.returnValue(of(MapUtils.singletonMap(SimpleRepoId, SimpleRepo.withActive(false))));
      repositoryStore.repoPage.next(currentPage);

      component.activateRepos(request);

      let updatedPage = repositoryStore.repoPage.getValue();
      expect(updatedPage.content.length).toBe(1);
      expect(updatedPage.content[0].active).toBe(false);
    });

    it('#activateRepos when it should not be changed', () => {
      repositoryService.activateRepos.and.returnValue(of(MapUtils.singletonMap(7, SimpleRepo.withActive(false))));
      repositoryStore.repoPage.next(currentPage);

      component.activateRepos(request);

      let updatedPage = repositoryStore.repoPage.getValue();
      expect(updatedPage.content.length).toBe(1);
      expect(updatedPage.content[0].active).toBe(true);
    });
  });

  describe('hasActiveRepo', () => {
    it('#hasActiveRepo when one active', () => {
      const page = new Page<Repo>(1, 1, 1, [SimpleRepo.withActive(false), SimpleRepo.withActive(true)]);
      repositoryStore.repoPage.next(page);

      expect(component.hasActiveRepo()).toBe(true);
    });

    it('#hasActiveRepo when no active', () => {
      const page = new Page<Repo>(1, 1, 1, [SimpleRepo.withActive(false), SimpleRepo.withActive(false)]);
      repositoryStore.repoPage.next(page);

      expect(component.hasActiveRepo()).toBe(false);
    });
  });
});


