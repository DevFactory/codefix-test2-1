import { Component, Input, OnInit, AfterViewChecked, ViewChild, Renderer2 } from '@angular/core';
import { Completed } from '../../model/completed';
import { BehaviorSubject } from 'rxjs';
import { DfGrid } from '@devfactory/ngx-df';
import { RepositoryUtils } from '@app/shared/utilities/base/repository.utils';

@Component({
  selector: 'app-completed-grid',
  templateUrl: './completed-grid.component.html',
  styleUrls: ['./completed-grid.component.scss']
})
export class CompletedGridComponent implements OnInit, AfterViewChecked {

  @Input() loadedSubject: BehaviorSubject<boolean>;
  @Input() completedListSubject: BehaviorSubject<Completed[]>;

  @ViewChild('grid') grid: DfGrid;

  completedList: Completed[];
  loaded: boolean;

  constructor(private renderer: Renderer2) {
  }

  ngOnInit(): void {
    this.completedListSubject.subscribe(completedList => this.completedList = completedList);
    this.loadedSubject.subscribe(loaded => this.loaded = loaded);
  }

  ngAfterViewChecked(): void {
    this.updateGridLayout();
  }

  formatRepositoryName(name: string): string {
    return RepositoryUtils.formatRowName(name);
  }

  /* istanbul ignore next */
  private updateGridLayout(): void {
    const scroll: HTMLElement = document.querySelector('.df-grid-scrollable-view__body');
    const header: HTMLElement = document.querySelector('.df-grid-scrollable-view__header');

    if (scroll && header && this.grid) {
      const height: number = this.grid.el.nativeElement.clientHeight;
      this.renderer.setStyle(scroll, 'height', `${height}px`);
    }
  }
}
