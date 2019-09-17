import { PageRequest } from '@app/core/models/page.request';

describe('PageRequest', () => {

  it('constructor', () => {
    const pageRequest = new PageRequest(1);

    expect(pageRequest.page).toBe(1);
  })
});
