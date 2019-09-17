import { ConfigurationService } from '@app/core/services/configuration.service';

export function createConfigService(
  configuration: ConfigurationService = new ConfigurationService(),
  _window: any = window): ConfigurationService {

  const browserWindow: any = _window || {};
  // @ts-ignore: windows is a plain js object
  const browserWindowEnv: any = browserWindow['__env'] || {};
  Object.entries(browserWindowEnv).forEach(([key, value]) => configuration.set(key, String(value)));
  return configuration;
}
