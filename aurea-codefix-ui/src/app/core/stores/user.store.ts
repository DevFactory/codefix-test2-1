import { User } from '@app/core/models/user';
import { StorageBehaviorSubject } from '@app/shared/utilities/stored.bolean.subject';
import { BehaviorSubject } from 'rxjs';

export class UserStore {

  user: BehaviorSubject<User> = new BehaviorSubject(null);
  acceptTerms: BehaviorSubject<Boolean> = null;

  isAnalyzing: BehaviorSubject<Boolean>;

  initIsAnalyzing(user: User): void {
    this.isAnalyzing = new StorageBehaviorSubject(user.email + '.isAnalyzing', false);
  }

  initTerms(user: User): void {
    this.acceptTerms = new StorageBehaviorSubject(user.email + '.acceptTerms', false);
  }
}
