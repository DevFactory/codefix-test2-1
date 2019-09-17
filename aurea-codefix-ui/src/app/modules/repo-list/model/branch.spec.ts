import { Branch } from './branch';

describe('Branch', () => {

  it('constructor', () => {
    const branchName = 'branchName';

    expect(new Branch(branchName).name).toBe(branchName);
  })
});
