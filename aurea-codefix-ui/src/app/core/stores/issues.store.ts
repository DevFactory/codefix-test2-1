import { BehaviorSubject } from 'rxjs';
import { Issue } from 'app/modules/issues/backlog/model/issue';

export class IssuesStore {

  issuesList: BehaviorSubject<Issue[]> = new BehaviorSubject([]);
  loaded: BehaviorSubject<boolean> = new BehaviorSubject(false);
}
