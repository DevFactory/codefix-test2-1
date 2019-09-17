import { IssuesStore } from '@app/core/stores/issues.store';

describe('IssuesStore', () => {

  const issuesStore: IssuesStore = new IssuesStore();

  it('default values', () => {
    expect(issuesStore.loaded.getValue()).toBe(false);
    expect(issuesStore.issuesList.getValue()).toEqual([]);
  });
});
