import { BehaviorSubject } from 'rxjs';

export class StorageBehaviorSubject extends BehaviorSubject<Boolean> {

  /* istanbul ignore next */
  constructor(private storageKey: string, defaultValue: boolean) {
    super(StorageBehaviorSubject.getOrDefault(storageKey, defaultValue));
  }

  private static getOrDefault(storageKey: string, defaultValue: boolean): boolean {
    const storedValue: string = localStorage.getItem(storageKey);
    return storedValue == null ? defaultValue : Boolean(JSON.parse(storedValue));
  }

  next(value: boolean): void {
    localStorage.setItem(this.storageKey, String(value));
    super.next(value);
  }
}
