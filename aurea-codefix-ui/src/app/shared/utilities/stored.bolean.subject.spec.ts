import { StorageBehaviorSubject } from '@app/shared/utilities/stored.bolean.subject';

describe('StorageBehaviorSubject', () => {

  it('When default value and not stored variable', () => {
    spyOn(localStorage, 'getItem').and.returnValue(null);
    const subject: StorageBehaviorSubject = new StorageBehaviorSubject('a key', true);

    expect(subject.getValue()).toBe(true);
  });

  it('When default value and but stored variable', () => {
    spyOn(localStorage, 'getItem').and.returnValue(true);
    const subject: StorageBehaviorSubject = new StorageBehaviorSubject('a key', false);

    expect(subject.getValue()).toBe(true);
  });

});
