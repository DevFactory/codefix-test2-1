import { Overlay, OverlayRef, PositionStrategy } from '@angular/cdk/overlay';
import { TemplatePortal } from '@angular/cdk/portal';
import {
  AfterViewChecked, Component, ElementRef, HostListener, Input, OnInit, Renderer2,
  TemplateRef, ViewChild, ViewContainerRef, Output, EventEmitter
} from '@angular/core';
import { FormControl } from '@angular/forms';
import { RepositoryUtils } from '@app/shared/utilities/base/repository.utils';
import { DfGrid } from '@devfactory/ngx-df';
import { BehaviorSubject } from 'rxjs';
import { take } from 'rxjs/operators';
import { Issue } from '../../model/issue';

@Component({
  selector: 'app-issues-grid',
  templateUrl: './issues-grid.component.html',
  styleUrls: ['./issues-grid.component.scss']
})
export class IssuesGridComponent implements OnInit, AfterViewChecked {

  @Input() loadedSubject: BehaviorSubject<boolean>;
  @Input() showAnalyzingSubject: BehaviorSubject<boolean>;
  @Input() issueListSubject: BehaviorSubject<Issue[]>;
  @Output() orderChange: EventEmitter<Issue[]> = new EventEmitter();

  @ViewChild('grid') grid: DfGrid;
  @ViewChild('drag') drag: ElementRef;
  @ViewChild('menu') menu: TemplateRef<null>;

  issuesList: Issue[] = [];
  loaded: boolean;
  showAnalyzing: boolean;

  position: FormControl = new FormControl();
  issuesTotal: number;
  menuOverlayRef: OverlayRef;
  dragging: boolean = false;
  draggingIssue: Issue | undefined;
  draggingOffsetY: number;
  draggingOffsetX: number;
  issuesSelected: Issue[] = [];
  renderer: Renderer2;

  private listeners: Function[] = [];

  constructor(private renderer2: Renderer2,
    private overlay: Overlay,
    private viewContainerRef: ViewContainerRef) {
    this.renderer = this.renderer2;
  }

  /* istanbul ignore next */
  @HostListener('click')
  onHostClick(): void {
    this.closeContextMenu();
  }

  /* istanbul ignore next */
  @HostListener('window:keydown.escape')
  onHostEscapePressed(): void {
    this.closeContextMenu();
  }

  ngOnInit(): void {
    this.issueListSubject.subscribe(page => {
      this.issuesList = page;
      this.loadListeners();
    });
    this.loadedSubject.subscribe(loaded => this.loaded = loaded);
    this.showAnalyzingSubject.subscribe(showAnalyzing => this.showAnalyzing = showAnalyzing);
    this.position = new FormControl(null);
  }

  ngAfterViewChecked(): void {
    this.updateGridLayout();
  }

  formatRepositoryName(name: string): string {
    return RepositoryUtils.formatRowName(name);
  }

  getRowClass(row: Issue): string {
    return row.justMoved ? 'grid__row--moved' : '';
  }

  /* istanbul ignore next */
  onRowClick(event: MouseEvent, issue: Issue): void {
    if (event.shiftKey || this.issuesSelected.length === -1) {
      this.updateIssueSelection(event, issue);
      return;
    }

    this.multiSelect(issue);
  }

  /* istanbul ignore next */
  onRowSelect(event: MouseEvent, issue: Issue): void {
    this.updateIssueSelection(event, issue, 'select');
  }

  /* istanbul ignore next */
  onRowUnselect(event: MouseEvent, issue: Issue): void {
    this.updateIssueSelection(event, issue);
  }

  /* istanbul ignore next */
  onMoveToBottomClick(issue: Issue): void {
    this.closeContextMenu();
    const index: number = this.updateIssuesOrder(this.issuesList[this.issuesList.length - 1], issue);
    this.updateTable(index);
  }

  /* istanbul ignore next */
  onMoveToTopClick(issue: Issue): void {
    this.closeContextMenu();
    const index: number = this.updateIssuesOrder(this.issuesList[0], issue);
    this.updateTable(index);
  }

  /* istanbul ignore next */
  onPositionConfirm(issue: Issue): void {
    if (this.position.value < 0) {
      this.position.setValue(0);
    }

    if (this.position.value > this.issuesTotal) {
      this.position.setValue(this.issuesTotal);
    }

    const target: Issue = this.issuesList.find(v => v.order === this.position.value);

    if (target) {
      const index: number = this.updateIssuesOrder(target, issue);
      this.updateTable(index);
      this.closeContextMenu();
    }
  }

  getInvolvedIssuesTotal(issue: Issue): number {
    const issues: Issue[] = this.getInvolvedIssues(issue);
    return issues.length;
  }

  updateIssuesOrder(destination: Issue, interacted: Issue): number {
    const issues: Issue[] = this.getInvolvedIssues(interacted).sort((a, b) => a.order - b.order);
    const indexDestination: number = this.issuesList.findIndex(v => v.id === destination.id);
    this.issuesList = this.issuesList.filter(v1 => !issues.some(v2 => v1.id === v2.id));
    this.issuesList = [...this.issuesList.slice(0, indexDestination), ...issues,
      ...this.issuesList.slice(indexDestination)];
    this.issuesList = this.issuesList.map((v, i) => Object.assign(v, { order: i + 1 }));

    this.issuesList = this.issuesList.map(v1 =>
      issues.some(v2 => v2.id === v1.id) ? Object.assign(v1, { justMoved: true }) : v1,
    );

    this.issuesSelected = [];
    return indexDestination;
  }

  updateIssueSelection(event: MouseEvent, issue: Issue, type: string = 'click'): void {
    if (event.shiftKey && this.issuesSelected.length) {
      const lastSelected: Issue = this.issuesSelected[this.issuesSelected.length
        - (type === 'select' && this.issuesSelected.length > 1 ? 2 : 1)];
      const lastSelectedIndex: number = this.issuesList.findIndex(v => v.id === lastSelected.id);
      const clickedIndex: number = this.issuesList.findIndex(v => v.id === issue.id);

      if (lastSelectedIndex < clickedIndex) {
        this.issuesSelected = [...this.issuesList.slice(lastSelectedIndex + 1, clickedIndex + 1), lastSelected];
      } else if (lastSelectedIndex > clickedIndex) {
        this.issuesSelected = [...this.issuesList.slice(clickedIndex, lastSelectedIndex), lastSelected];
      } else {
        this.issuesSelected = [issue];
      }
    }
  }

  /* istanbul ignore next */
  private updateGridLayout(): void {
    const scroll: HTMLElement = document.querySelector('.df-grid-scrollable-view__body');
    const header: HTMLElement = document.querySelector('.df-grid-scrollable-view__header');

    if (scroll && header && this.grid) {
      const grid: number = this.grid.el.nativeElement.clientHeight;
      const height: number = grid - header.clientHeight;
      this.renderer.setStyle(scroll, 'height', `${height}px`);
    }
  }

  /* istanbul ignore next */
  private getInvolvedIssues(issue: Issue): Issue[] {
    if (issue && issue.id) {
      return this.issuesSelected.some(i => i.id === issue.id) ? this.issuesSelected : [...this.issuesSelected, issue];
    } else {
      return [];
    }
  }

  /* istanbul ignore next */
  private multiSelect(issue: Issue): void {
    const index: number = this.issuesSelected.findIndex(v => v.id === issue.id);

    if (index > -1) {
      this.issuesSelected = [...this.issuesSelected.slice(0, index), ...this.issuesSelected.slice(index + 1)];
    } else {
      this.issuesSelected = [...this.issuesSelected, issue];
    }
  }

  /* istanbul ignore next */
  private updateTable(index: number): void {
    this.loadListeners();
    this.scrollToMovedIssues(index);

    this.orderChange.emit(this.issuesList);
  }

  /* istanbul ignore next */
  private scrollToMovedIssues(index: number): void {
    const wrapper: HTMLElement = document.querySelector('.df-grid-scrollable-view__body');
    const rows: NodeListOf<HTMLElement> = document.querySelectorAll('.grid tbody tr');
    wrapper.scroll(0, rows[index].offsetTop - rows[index].clientHeight);
  }

  /* istanbul ignore next */
  private closeContextMenu(): void {
    this.position.reset();

    if (this.menuOverlayRef && this.menuOverlayRef.hasAttached()) {
      this.menuOverlayRef.detach();
    }
  }

  /*
    TABLE ROW EVENT LISTENERS
  */
  /* istanbul ignore next */
  private addEventListeners(): void {
    const rows: NodeListOf<Element> = document.querySelectorAll('.grid tbody tr');
    for (let i: number = 0; i < rows.length; i++) {
      if (this.issuesList[i]) {
        this.listeners.push(
          this.renderer.listen(rows[i], 'mousedown',
            (event: MouseEvent) => this.onMouseDown(event, this.issuesList[i])),
          this.renderer.listen(rows[i], 'mouseup',
            (event: MouseEvent) => this.onMouseUp(event, rows[i], i)),
          this.renderer.listen(rows[i], 'mouseenter',
            (event: MouseEvent) => this.onMouseEnter(event, this.issuesList[i])),
          this.renderer.listen(rows[i], 'mouseleave',
            (event: MouseEvent) => this.onMouseLeave(event)),
          this.renderer.listen(rows[i], 'mousemove',
            (event: MouseEvent) => this.onMouseMove(event)),
          this.renderer.listen(rows[i], 'contextmenu',
            event => this.onRowRightClick(event, this.issuesList[i]))
        );
      }
    }
  }

  /* istanbul ignore next */
  private onMouseDown(event: MouseEvent, issue: Issue): void {
    if (event.button === 0) {
      this.dragging = true;
      this.draggingIssue = issue;
      const dragDimensions: { width: number; height: number; } = this.drag.nativeElement.getBoundingClientRect();
      this.draggingOffsetY = document.querySelector('df-topbar').clientHeight + dragDimensions.height;
      this.draggingOffsetX = dragDimensions.width;
      this.resetJustMovedIssues();
    }
  }

  /* istanbul ignore next */
  private onMouseEnter(event: MouseEvent, issue: Issue): void {
    if (this.dragging && this.draggingIssue.id !== issue.id) {
      this.renderer.addClass(event.target, 'df-grid__data--over');
    }
  }

  /* istanbul ignore next */
  private onMouseLeave(event: MouseEvent): void {
    if (this.dragging) {
      this.renderer.removeClass(event.target, 'df-grid__data--over');
    }
  }

  /* istanbul ignore next */
  onMouseMove(event: MouseEvent): void {
    if (this.dragging) {
      requestAnimationFrame(() => {
        if (this.dragging) {
          this.renderer.setStyle(
            this.drag.nativeElement,
            'transform',
            `translate(${event.clientX - this.draggingOffsetX}px, ${event.clientY - this.draggingOffsetY}px)`,
          );
        }
      });
    }
  }

  /* istanbul ignore next */
  private onMouseUp(event: MouseEvent, row: Element, index: number): void {
    if (event.button === 0) {
      this.renderer.removeClass(row, 'df-grid__data--over');
      this.renderer.removeStyle(this.drag.nativeElement, 'transform');

      if (this.draggingIssue && this.issuesList[index].id !== this.draggingIssue.id) {
        const orderIndex: number = this.updateIssuesOrder(this.issuesList[index], this.draggingIssue);
        this.updateTable(orderIndex);
      }

      this.dragging = false;
      this.draggingIssue = undefined;
    }
  }

  /* istanbul ignore next */
  private onRowRightClick(event: MouseEvent, issue: Issue): boolean {
    this.closeContextMenu();
    this.resetJustMovedIssues();
    this.menuOverlayRef = this.overlay.create();

    this.menuOverlayRef
      .backdropClick()
      .pipe(take(1))
      .subscribe(() => {
        this.menuOverlayRef.detach();
        this.position.reset();
      });

    const portal: TemplatePortal = new TemplatePortal(this.menu, this.viewContainerRef, { $implicit: issue });
    this.menuOverlayRef.attach(portal);
    this.updateOverlayPosition(event);
    return false;
  }

  /* istanbul ignore next */
  private updateOverlayPosition(event: MouseEvent): void {
    const vpw: number = document.documentElement.clientWidth;
    const vph: number = document.documentElement.clientHeight;
    const pw: number = this.menuOverlayRef.overlayElement.clientWidth;
    const ph: number = this.menuOverlayRef.overlayElement.clientHeight;
    const left: number = event.clientX + pw > vpw ? event.clientX - pw : event.clientX;
    const top: number = event.clientY + ph > vph ? event.clientY - ph : event.clientY;

    this.menuOverlayRef.updatePositionStrategy(
      this.overlay
        .position()
        .global()
        .left(`${left}px`)
        .top(`${top}px`),
    );
  }

  /* istanbul ignore next */
  private resetJustMovedIssues(): void {
    this.issuesList = this.issuesList.map(v => Object.assign(v, { justMoved: false }));
  }

  /* istanbul ignore next */
  private removeListeners(): void {
    for (let i: number = 0; i < this.listeners.length; i++) {
      this.listeners[i]();
    }

    this.listeners = [];
  }

  /**
   * Called when the user clicks the ellipsis in a row.
   * Should open a menu to prioritize the selected issue.
   *
   * @param {Issue} issue
   */
  onMenuClick(event: MouseEvent, issue: Issue, origin: HTMLElement, menu: TemplateRef<null>): void {
    event.stopPropagation();
    this.closeContextMenu();
    const positionStrategy: PositionStrategy = this.overlay
      .position()
      .flexibleConnectedTo(origin)
      .withPositions([
        {
          offsetX: 0,
          offsetY: 0,
          originX: 'end',
          originY: 'bottom',
          overlayX: 'end',
          overlayY: 'top',
        },
        {
          offsetX: 0,
          offsetY: 0,
          originX: 'end',
          originY: 'top',
          overlayX: 'end',
          overlayY: 'bottom',
        },
      ]);
    this.menuOverlayRef = this.overlay.create({ positionStrategy });
    this.menuOverlayRef
      .backdropClick()
      .pipe(take(1))
      .subscribe(() => {
        this.renderer.removeClass(origin, 'menu__toggle--active');
        this.menuOverlayRef.detach();
        this.position.reset();
      });
    const portal: TemplatePortal = new TemplatePortal(menu, this.viewContainerRef, { $implicit: issue, origin });
    this.menuOverlayRef.attach(portal);
  }
  /* istanbul ignore next */
  private loadListeners(): void {
    // timeout needed to apply listerners
    setTimeout(() => {
      this.removeListeners();
      this.addEventListeners();
    });
  }

}
