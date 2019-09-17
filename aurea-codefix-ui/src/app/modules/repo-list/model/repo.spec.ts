import { Repo } from './repo';
import { Branch } from './branch';
import { SimpleRepo } from '@app/shared/mocks/repo.page';

describe('Repo', () => {

  const url = 'an url';
  const branchName = 'master';
  let genericRepo: Repo;

  beforeEach(() => {
    genericRepo = <Repo>{url: url, branch: branchName};
  });

  it('constructor', () => {
    const repo = new Repo(genericRepo.id, genericRepo.branch, genericRepo.url, genericRepo.active);

    expect(repo.url).toBe(url);
    expect(repo.branches).toEqual([new Branch(branchName)]);
    expect(repo.branch).toBe(branchName);
  });

  it('#withActive when false', () => {
    const repo = SimpleRepo.withActive(false);

    expect(repo.active).toBe(false);
    expect(repo).not.toBe(SimpleRepo);
  });

  it('#withActive when true', () => {
    const repo = SimpleRepo.withActive(true);

    expect(repo.active).toBe(true);
    expect(repo).not.toBe(SimpleRepo);
    expect(repo).toEqual(SimpleRepo);
  });
});
