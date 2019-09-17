import { UserStore } from '@app/core/stores/user.store';
import { AUTH_USER } from '@app/shared/mocks/auth0.tocken';

describe('UserStore', () => {

  const userStore = new UserStore();

  beforeEach(() => {
  });

  it('default value', () => {
    expect(userStore.acceptTerms).toBe(null);
    expect(userStore.user.getValue()).toBeNull();
  });

  it('#initTerms', () => {
    userStore.initTerms(AUTH_USER);

    expect(userStore.acceptTerms.getValue()).toBe(false);
    expect(userStore.user.getValue()).toBe(null);
  });

  it('#initIsAnalyzing', () => {
    userStore.initIsAnalyzing(AUTH_USER);

    expect(userStore.isAnalyzing.getValue()).toBe(false);
  });
});
