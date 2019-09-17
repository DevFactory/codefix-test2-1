import { NavigationTab } from '@app/shared/components/navigation-tabs/navigation-tab';
import { BehaviorSubject } from 'rxjs';

export const REPOSITORIES_TAB_ID: number = 1;
export const BACKLOG_TAB_ID: number = 2;
export const IN_PROCESS_TAB_ID: number = 3;
export const DONE_TAB_ID: number = 4;

const initialTabs: NavigationTab[] = [
  {id: REPOSITORIES_TAB_ID, title: 'My Repositories', route: '/dashboard/repos', active: false, disabled: false},
  {id: BACKLOG_TAB_ID, title: 'Backlog', route: '/dashboard/issues', active: false, disabled: true},
  {id: IN_PROCESS_TAB_ID, title: 'In Process', route: '/dashboard/issues/scheduled', active: false, disabled: true},
  {id: DONE_TAB_ID, title: 'Done', route: '/dashboard/issues/completed', active: false, disabled: true},
];

export class NavigationStore {
  private _tabs: NavigationTab[] = [];

  tabs: BehaviorSubject<NavigationTab[]> = new BehaviorSubject(initialTabs);

  constructor() {
    this._tabs = initialTabs;
  }

  activateRepositoriesTab(): void {
    this.updateActiveTab(REPOSITORIES_TAB_ID);
  }

  activateBacklogTab(): void {
    this.updateActiveTab(BACKLOG_TAB_ID);
  }

  activateProcessTab(): void {
    this.updateActiveTab(IN_PROCESS_TAB_ID);
  }

  activateDoneTab(): void {
    this.updateActiveTab(DONE_TAB_ID);
  }

  enableInProcessTab(): void {
    this.enableTab(IN_PROCESS_TAB_ID);
  }

  enableDoneTab(): void {
    this.enableTab(DONE_TAB_ID);
  }

  enableBacklogTab(): void {
    this.enableTab(BACKLOG_TAB_ID);
  }

  setBacklogSize(size: number): void {
    this.setTabListSize(BACKLOG_TAB_ID, size);
  }

  reset(): void {
    this._tabs = initialTabs;
    this.tabs.next(this._tabs);
  }

  private updateActiveTab(id: number): void {
    this._tabs = this._tabs.map(v => (v.id === id ? {...v, active: true, disabled: false} : {...v, active: false}));
    this.tabs.next(this._tabs);
  }

  private enableTab(id: number): void {
    this._tabs = this._tabs.map(v => (v.id === id ? {...v, disabled: false} : {...v}));
    this.tabs.next(this._tabs);
  }

  private setTabListSize(id: number, size: number): void {
    this._tabs = this._tabs.map(v => (v.id === id ? {...v, value: size} : {...v}));
    this.tabs.next(this._tabs);
  }
}
