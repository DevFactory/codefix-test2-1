import { ActivateRequest } from './activate.request';

describe('ActivationStatus', () => {

  let activationRequest: ActivateRequest;

  it('constructor', () => {
    activationRequest = new ActivateRequest([], true)
    expect(activationRequest.repositoryIds).toEqual([]);
    expect(activationRequest.active).toEqual(true);
  });

  it('ActivationStatus#fromSingle', () => {
    const request = ActivateRequest.fromSingle(5, false);

    expect(request.repositoryIds).toContain(5);
    expect(request.active).toBe(false);
  });

});
