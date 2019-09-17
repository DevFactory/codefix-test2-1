import { RepositoriesStore } from '@app/core/stores/repositories.store';

describe('RepositoryStore', () => {

  const repositoryStore: RepositoriesStore = new RepositoriesStore();

  it('default values', () => {
    expect(repositoryStore.loaded.getValue()).toBe(false);
    expect(repositoryStore.repoPage.getValue()).toBeNull();
    expect(repositoryStore.showModal.getValue()).toBe(true);
  });

});
