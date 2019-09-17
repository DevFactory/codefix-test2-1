import { BehaviorSubject } from 'rxjs';
import { StorageBehaviorSubject } from '@app/shared/utilities/stored.bolean.subject';
import { Page } from '@app/core/models/page';
import { Repo } from '../../modules/repo-list/model/repo';

export class RepositoriesStore {

  repoPage: BehaviorSubject<Page<Repo>> = new BehaviorSubject(null);
  loaded: BehaviorSubject<boolean> = new BehaviorSubject(false);
  showModal: BehaviorSubject<Boolean> = new StorageBehaviorSubject('showReports', true);
}
