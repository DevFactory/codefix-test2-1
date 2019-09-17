import { ConfigurationService } from '@app/core/services/configuration.service';
import { createConfigService } from '@app/shared/utilities/config/configuration-service.factory';

describe('ConfigurationServiceFactory', () => {

  const configService: jasmine.SpyObj<ConfigurationService> = jasmine.createSpyObj('ConfigurationService', ['get', 'set']);

  it('should load windows env variables when config is provided', () => {
    createConfigService(configService, {'__env': {'pro': 55}});

    expect(configService.set).toHaveBeenCalledWith('pro', '55');
  });

  it('no errors when not config not windows', () => {
    createConfigService();
  });

  it('no errors when not windows object', () => {
    createConfigService(configService, null);
  });

  it('no errors when not windows __env property', () => {
    createConfigService(configService, {'pro': 55});
  });
});
