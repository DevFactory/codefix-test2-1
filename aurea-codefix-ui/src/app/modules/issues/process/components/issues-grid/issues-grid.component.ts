import { AfterViewChecked, Component, Input, OnInit, Renderer2, ViewChild } from '@angular/core';
import { Observable } from 'rxjs';
import { DfGrid } from '@devfactory/ngx-df';
import { RepositoryUtils } from '@app/shared/utilities/base/repository.utils';
import { Order } from '../../model/order';

@Component({
  selector: 'app-issues-grid',
  templateUrl: './issues-grid.component.html',
  styleUrls: ['./issues-grid.component.scss']
})
export class IssuesGridComponent implements OnInit, AfterViewChecked {

  @Input() orderObservable: Observable<Order>;
  @Input() loadedObservable: Observable<boolean>;

  @ViewChild('grid') grid: DfGrid;

  order: Order = null;
  loaded: boolean;
  showAnalyzing: boolean;

  constructor(private renderer: Renderer2) {
  }

  ngOnInit(): void {
    this.orderObservable.subscribe(order => this.order = order);
    this.loadedObservable.subscribe(loaded => this.loaded = loaded);
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

    if (scroll && this.grid) {
      const height: number = this.grid.el.nativeElement.clientHeight;
      this.renderer.setStyle(scroll, 'height', `${height}px`);
    }
  }
}
