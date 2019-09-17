import { Component, OnInit } from '@angular/core';
import { NavigationTab } from '@app/shared/components/navigation-tabs/navigation-tab';
import { NavigationStore } from '@app/core/stores/navigation.store';
import { CompletedStore } from '@app/core/stores/completed.store';
import { CompletedService } from '../services/completed.service';

@Component({
  selector: 'app-completed-component',
  templateUrl: './completed.component.html',
  styleUrls: ['./completed.component.scss']
})
export class CompletedComponent implements OnInit {

  /**
   * Navigation tab configuration.
   */
  tabs: NavigationTab[];

  constructor(
    private navigationStore: NavigationStore,
    public completedStore: CompletedStore,
    private completedService: CompletedService
  ) {
  }

  ngOnInit(): void {
    this.navigationStore.tabs.subscribe(tabs => (this.tabs = tabs));
    this.navigationStore.activateDoneTab();
    this.loadCompleted();
  }

  private loadCompleted(): void {
    this.completedService.getCompletedIssues().subscribe();
  }

}
