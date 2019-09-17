import { AfterViewChecked, Component, EventEmitter, Input, OnInit, Output, Renderer2, ViewChild } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { PageRequest } from '@app/core/models/page.request';
import { DfGrid } from '@devfactory/ngx-df';
import { ActivateRequest } from '../../http/activate.request';
import { Page } from '@app/core/models/page';
import { Repo } from '../../model/repo';
import { RepositoryUtils } from '@app/shared/utilities/base/repository.utils';

@Component({
  selector: 'app-repo-grid',
  templateUrl: './repo-grid.component.html',
  styleUrls: ['./repo-grid.component.scss']
})
export class RepoGridComponent implements OnInit, AfterViewChecked {

  @Input() repoPageSubject: BehaviorSubject<Page<Repo>>;
  @Input() loadedSubject: BehaviorSubject<boolean>;

  @Output() pageChange: EventEmitter<PageRequest> = new EventEmitter<PageRequest>();
  @Output() addRepoClick: EventEmitter<void> = new EventEmitter<void>();
  @Output() activate: EventEmitter<ActivateRequest> = new EventEmitter<ActivateRequest>();

  @ViewChild('grid') grid: DfGrid;

  repoPage: Page<Repo>;
  loaded: boolean;

  constructor(private renderer: Renderer2) {
  }

  ngOnInit(): void {
    this.repoPageSubject.subscribe(page => this.repoPage = page);
    this.loadedSubject.subscribe(loaded => this.loaded = loaded);
  }

  ngAfterViewChecked(): void {
    this.updateGridLayout();
  }

  onPageChange(page: number): void {
    this.pageChange.emit(new PageRequest(page));
  }

  onAddRepoClick(): void {
    this.addRepoClick.emit();
  }

  onActivate(activate: boolean, repoId: number): void {
    this.activate.emit(ActivateRequest.fromSingle(repoId, activate));
  }

  formatRowName(name: string): string {
    return RepositoryUtils.formatRowName(name);
  }

  /* istanbul ignore next */
  private updateGridLayout(): void {
    const scroll: HTMLElement = document.querySelector('.df-grid-scrollable-view__body');
    const header: HTMLElement = document.querySelector('.df-grid-scrollable-view__header');

    if (scroll && header && this.grid) {
      const grid: number = this.grid.el.nativeElement.clientHeight;
      const height: number = grid - header.clientHeight - (this.repoPage.total <= this.repoPage.content.length ? 15 : 0);
      this.renderer.setStyle(scroll, 'height', `${height}px`);
    }
  }
}
