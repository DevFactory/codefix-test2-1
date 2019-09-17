import { BehaviorSubject } from 'rxjs';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA, Renderer2, ViewContainerRef } from '@angular/core';
import { Issue } from '../../model/issue';
import { IssuesGridComponent } from './issues-grid.component';
import {
  IssueList,
  SimpleIssue,
  SimpleIssueTwo,
  IssueListFive,
  SimpleIssueThree,
  SimpleIssueFour,
  SimpleIssueFive } from '@app/shared/mocks/issue';
import { Overlay } from '@angular/cdk/overlay';

describe('IssuesGridComponent', () => {

  const issueListSubject: BehaviorSubject<Issue[]> = new BehaviorSubject(null);
  const loadedSubject: BehaviorSubject<boolean> = new BehaviorSubject(false);
  const showAnalyzingSubject: BehaviorSubject<boolean> = new BehaviorSubject(false);

  let mouseEvent: jasmine.SpyObj<MouseEvent>;
  let renderer: jasmine.SpyObj<Renderer2>;
  let component: IssuesGridComponent;
  let fixture: ComponentFixture<IssuesGridComponent>;

  beforeEach(() => {
    mouseEvent = jasmine.createSpyObj(['shiftKey']);
    renderer = jasmine.createSpyObj(['setStyle']);
    const overlay = jasmine.createSpy();
    const viewContainerRef = jasmine.createSpy();

    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [IssuesGridComponent],
      providers: [
        {provide: Renderer2, useValue: renderer},
        {provide: Overlay, useValue: overlay},
        {provide: ViewContainerRef, useValue: viewContainerRef},
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IssuesGridComponent);
    component = fixture.componentInstance;

    component.issueListSubject = issueListSubject;
    component.loadedSubject = loadedSubject;
    component.showAnalyzingSubject = showAnalyzingSubject;
    component.renderer = renderer;
  });

  afterEach(() => {
    TestBed.resetTestingModule();
  });

  it('#ngOnInit issueListSubject subscription', () => {
    issueListSubject.next(IssueList);

    fixture.detectChanges();

    expect(component.issuesList).toEqual(IssueList);
  });

  it('#ngOnInit loadedSubject subscription', () => {
    loadedSubject.next(true);

    fixture.detectChanges();

    expect(component.loaded).toEqual(true);
  });

  it('#formatRepositoryName', () => {
    expect(component.formatRepositoryName('http://github.com/company/my-dummy.git'))
      .toBe('company / <strong>my-dummy</strong>');
  });

  it('#getRowClass moved', () => {
    const issue: Issue = SimpleIssue;
    issue.justMoved = true;
    expect(component.getRowClass(issue)).toBe('grid__row--moved');
  });

  it('#getRowClass not moved', () => {
    const issue: Issue = SimpleIssue;
    issue.justMoved = false;
    expect(component.getRowClass(issue)).toBe('');
  });

  it('#updateIssueOrder when moved one', () => {
    component.issuesList = [SimpleIssue, SimpleIssueTwo];
    component.updateIssuesOrder(SimpleIssue, SimpleIssueTwo);

    expect(component.issuesList[0].id).toEqual(2);
    expect(component.issuesList[0].order).toEqual(1);
    expect(component.issuesList[1].id).toEqual(1);
    expect(component.issuesList[1].order).toEqual(2);
  });

  it('#updateIssueOrder when moved selection', () => {
    component.issuesList = IssueListFive;
    component.issuesSelected = [SimpleIssueThree, SimpleIssueFour, SimpleIssueFive];
    component.updateIssuesOrder(SimpleIssue, SimpleIssueThree);

    expect(component.issuesList[0].id).toEqual(3);
    expect(component.issuesList[0].order).toEqual(1);
    expect(component.issuesList[1].id).toEqual(4);
    expect(component.issuesList[1].order).toEqual(2);
    expect(component.issuesList[2].id).toEqual(5);
    expect(component.issuesList[2].order).toEqual(3);
    expect(component.issuesList[3].id).toEqual(1);
    expect(component.issuesList[3].order).toEqual(4);
    expect(component.issuesList[4].id).toEqual(2);
    expect(component.issuesList[4].order).toEqual(5);
  });

  it('#updateIssueSelection do nothing', () => {
    component.issuesSelected = [];
    component.updateIssueSelection(mouseEvent, SimpleIssue);

    expect(component.issuesSelected.length).toEqual(0);
  });

  it('#updateIssueSelection from three to one as row click', () => {
    mouseEvent.shiftKey.and.returnValue('true');
    component.issuesSelected = [SimpleIssueThree];
    component.issuesList = IssueListFive;

    component.updateIssueSelection(mouseEvent, SimpleIssue);

    expect(component.issuesSelected.length).toEqual(3);
    expect(component.issuesSelected[0].id).toEqual(1);
    expect(component.issuesSelected[1].id).toEqual(2);
    expect(component.issuesSelected[2].id).toEqual(3);
  });

  it('#updateIssueSelection from three to last as row click', () => {
    mouseEvent.shiftKey.and.returnValue('true');
    component.issuesSelected = [SimpleIssueThree];
    component.issuesList = IssueListFive;

    component.updateIssueSelection(mouseEvent, SimpleIssueFive);

    const selectionOrdered: Issue[] = component.issuesSelected.sort((a, b) => a.order - b.order);
    expect(selectionOrdered.length).toEqual(3);
    expect(selectionOrdered[0].id).toEqual(3);
    expect(selectionOrdered[1].id).toEqual(4);
    expect(selectionOrdered[2].id).toEqual(5);
  });

  it('#updateIssueSelection same position as row click', () => {
    mouseEvent.shiftKey.and.returnValue('true');
    component.issuesSelected = [SimpleIssueThree];
    component.issuesList = IssueListFive;

    component.updateIssueSelection(mouseEvent, SimpleIssueThree);

    expect(component.issuesSelected.length).toEqual(1);
    expect(component.issuesSelected[0].id).toEqual(3);
  });

  it('#updateIssueSelection from three to one as select', () => {
    mouseEvent.shiftKey.and.returnValue('true');
    component.issuesSelected = [SimpleIssueThree, SimpleIssue];
    component.issuesList = IssueListFive;

    component.updateIssueSelection(mouseEvent, SimpleIssue, 'select');

    expect(component.issuesSelected.length).toEqual(3);
    expect(component.issuesSelected[0].id).toEqual(1);
    expect(component.issuesSelected[1].id).toEqual(2);
    expect(component.issuesSelected[2].id).toEqual(3);
  });

  it('#updateIssueSelection from three to last as select', () => {
    mouseEvent.shiftKey.and.returnValue('true');
    component.issuesSelected = [SimpleIssueThree, SimpleIssueFive];
    component.issuesList = IssueListFive;

    component.updateIssueSelection(mouseEvent, SimpleIssueFive, 'select');

    const selectionOrdered: Issue[] = component.issuesSelected.sort((a, b) => a.order - b.order);
    expect(selectionOrdered.length).toEqual(3);
    expect(selectionOrdered[0].id).toEqual(3);
    expect(selectionOrdered[1].id).toEqual(4);
    expect(selectionOrdered[2].id).toEqual(5);
  });

  it('should not show dragged issues counter if dragging stopped', () => {
    component.dragging = true;
    component.onMouseMove(mouseEvent);
    component.dragging = false;

    // check after requestAnimationFrame callback is executed
    setTimeout(() => {
      expect(renderer.setStyle).not.toHaveBeenCalledWith(component.drag.nativeElement, 'transform',
        jasmine.any(String));
    });
  });
});
