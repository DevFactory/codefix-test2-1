import { CodeRampService } from './code-ramp.service';
import { ConfigurationService } from '@app/core/services/configuration.service';
import { TestBed } from '@angular/core/testing';

describe('CodeRampService', () => {

  const codeRampUrl: string = 'http://code-ramp-dummy';

  let codeRampService: CodeRampService;
  let location: Location;

  beforeEach(() => {
    const configService: ConfigurationService = new ConfigurationService();
    configService.set('codeRampUrl', codeRampUrl);
    location = <Location>{};

    TestBed.configureTestingModule({
      providers: [
        CodeRampService,
        {provide: ConfigurationService, useValue: configService}
      ]
    });

    codeRampService = TestBed.get(CodeRampService);
  });

  it('#getCodeRampUrl when not port', () => {
    location.protocol = 'https';
    location.hostname = 'test-server-dummy';

    const redirectionLink: string = codeRampService.getCodeRampUrl(location);
    expect(redirectionLink).toBe('http://code-ramp-dummy/?ref_url_domain=' +
      'https//test-server-dummy/dashboard/repos&ref_url_path=/&app_type=Codefix');
  });

  it('#getCodeRampUrl when port', () => {
    location.protocol = 'https';
    location.hostname = 'test-server-dummy';
    location.port = '5596';

    const redirectionLink: string = codeRampService.getCodeRampUrl(location);
    expect(redirectionLink).toBe('http://code-ramp-dummy/?ref_url_domain=' +
      'https//test-server-dummy:5596/dashboard/repos&ref_url_path=/&app_type=Codefix');
  });

});

