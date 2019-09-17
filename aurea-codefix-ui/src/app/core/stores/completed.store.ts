import { BehaviorSubject } from 'rxjs';
import { Completed } from 'app/modules/issues/completed/model/completed';

export class CompletedStore {

  completedList: BehaviorSubject<Completed[]> = new BehaviorSubject(null);
  loaded: BehaviorSubject<boolean> = new BehaviorSubject(false);
}
