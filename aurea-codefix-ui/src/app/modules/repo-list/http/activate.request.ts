export class ActivateRequest {

  constructor(public repositoryIds: number[], public active: boolean) {
  }

  static fromSingle(repoId: number, activate: boolean): ActivateRequest {
    return new ActivateRequest([repoId], activate);
  }
}

