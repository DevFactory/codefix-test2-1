import { RepoGridComponent } from './repo-grid.component';
import { BehaviorSubject } from 'rxjs';
import { RepoPage } from '@app/shared/mocks/repo.page';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA, Renderer2 } from '@angular/core';
import { Page } from '@app/core/models/page';
import { Repo } from '../../model/repo';

describe('RepoGridComponent', () => {

  const repoPageSubject: BehaviorSubject<Page<Repo>> = new BehaviorSubject(null);
  const loadedSubject: BehaviorSubject<boolean> = new BehaviorSubject(false);
  let renderer: jasmine.SpyObj<Renderer2>;

  let component: RepoGridComponent;
  let fixture: ComponentFixture<RepoGridComponent>;

  beforeEach(() => {
    renderer = jasmine.createSpyObj(['setStyle']);

    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [RepoGridComponent],
      providers: [
        {provide: Renderer2, useValue: renderer}
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RepoGridComponent);
    component = fixture.componentInstance;
    component.repoPageSubject = repoPageSubject;
    component.loadedSubject = loadedSubject;
  });

  it('#ngOnInit repoPageSubject subscription', () => {
    repoPageSubject.next(RepoPage);
    fixture.detectChanges();

    expect(component.repoPage).toEqual(RepoPage)
  });

  it('#ngOnInit loadedSubject subscription', () => {
    loadedSubject.next(true);
    fixture.detectChanges();

    expect(component.loaded).toEqual(true)
  });

  it('#onPageChange', () => {
    spyOn(component.pageChange, 'emit');

    component.onPageChange(1);
    expect(component.pageChange.emit).toHaveBeenCalled();
  });

  it('#onAddRepoClick', () => {
    spyOn(component.addRepoClick, 'emit');

    component.onAddRepoClick();
    expect(component.addRepoClick.emit).toHaveBeenCalled();
  });

  it('#onActivate', () => {
    spyOn(component.activate, 'emit');

    component.onActivate(true, 50);
    expect(component.activate.emit).toHaveBeenCalled();
  });

  it('#formatRowName', () => {
    expect(component.formatRowName('http://github.com/company/my-dummy.git'))
      .toBe('company / <strong>my-dummy</strong>');
  });

});
