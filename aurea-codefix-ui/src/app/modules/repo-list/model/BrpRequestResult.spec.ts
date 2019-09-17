import { BrpRequestResult } from './BrpRequestResult';

describe('BrpRequestResult', () => {

  it('constructor', () => {
    const requestId = 'requestId';
    const status = 'status';

    const request = new BrpRequestResult(requestId, status);
    expect(request.request_id).toBe(requestId);
    expect(request.status).toBe(status);
  });
});
