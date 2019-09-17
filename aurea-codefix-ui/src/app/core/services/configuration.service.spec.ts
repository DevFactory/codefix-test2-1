import { environment } from '../../../environments/environment';
import { ConfigurationService } from '@app/core/services/configuration.service';

describe('ConfigurationService', () => {

  let configuration: ConfigurationService;

  beforeEach(() => {
    configuration = new ConfigurationService();
  });

  it('constructor should load environment properties', () => {
    expect(configuration.get('auth0ClientId')).toBe(environment.auth0ClientId);
  });

  it('#set and get environment values', () => {
    const variable: string = 'a_variable';
    const variableValue: string = '55';

    configuration.set(variable, variableValue);
    expect(configuration.get(variable)).toBe(variableValue);
  });
});
