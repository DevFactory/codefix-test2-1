import { IgnoredRepo } from './ignored-repo';
import { Page } from '@app/core/models/page';
import { Repo } from './repo';

export class SyncResult {

  constructor(public repositories: Page<Repo>, public ignoredItems: IgnoredRepo[]) {
  }
}
