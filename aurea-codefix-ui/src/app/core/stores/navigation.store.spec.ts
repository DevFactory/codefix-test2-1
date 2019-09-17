import {
  BACKLOG_TAB_ID,
  IN_PROCESS_TAB_ID,
  NavigationStore,
  REPOSITORIES_TAB_ID,
  DONE_TAB_ID
} from '@app/core/stores/navigation.store';
import { NavigationTab } from '@app/shared/components/navigation-tabs/navigation-tab';

describe('NavigationStore', () => {
  const navigationStore: NavigationStore = new NavigationStore();

  beforeEach(() => {
    navigationStore.reset();
  });

  it('by default not tab should be enable', () => {
    const tabs: NavigationTab[] = navigationStore.tabs.getValue();
    expect(tabs.filter((element) => element.active)).toEqual([]);
  });

  it('#activateRepositoriesTab', () => {
    navigationStore.activateRepositoriesTab();

    expect(activeTabs().find(tab => tab.id == REPOSITORIES_TAB_ID)).not.toBeNull();
  });

  it('#activateBacklogTab', () => {
    navigationStore.activateBacklogTab();

    expect(activeTabs().find(tab => tab.id == BACKLOG_TAB_ID)).not.toBeNull();
  });

  it('#activateProcessTab', () => {
    navigationStore.activateProcessTab();

    expect(activeTabs().find(tab => tab.id == IN_PROCESS_TAB_ID)).not.toBeNull();
  });

  it('#activateDoneTab', () => {
    navigationStore.activateDoneTab();

    expect(activeTabs().find(tab => tab.id == DONE_TAB_ID)).not.toBeNull();
  });

  it('#enableBacklogTab', () => {
    navigationStore.enableBacklogTab();

    expect(enableTabs().find(tab => tab.id == BACKLOG_TAB_ID)).not.toBeNull();
  });

  it('#enableInProcessTab', () => {
    navigationStore.enableInProcessTab();

    expect(enableTabs().find(tab => tab.id == IN_PROCESS_TAB_ID)).not.toBeNull();
  });

  it('#enableDoneTab', () => {
    navigationStore.enableDoneTab();

    expect(enableTabs().find(tab => tab.id == DONE_TAB_ID)).not.toBeNull();
  });

  it('#setBacklogSize', () => {
    navigationStore.setBacklogSize(2);

    expect(navigationStore.tabs.getValue().find(tab => tab.id === BACKLOG_TAB_ID).value).toBe(2);
  });
 
  function activeTabs(): NavigationTab[] {
    return navigationStore.tabs.getValue().filter((element) => element.active);
  }

  function enableTabs(): NavigationTab[] {
    return navigationStore.tabs.getValue().filter((element) => !element.disabled);
  }
});
