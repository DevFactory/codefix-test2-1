import { Injectable } from '@angular/core';
import { ConfigurationService } from '@app/core/services/configuration.service';

@Injectable()
export class CodeRampService {

  constructor(
    private configService: ConfigurationService) {
  }

  getCodeRampUrl(location: Location): string {
    const codeFixUrl: string = `${this.getCodeFixUrl(location)}/dashboard/repos`;
    const baseRedirectPath: string = `${this.configService.get('codeRampUrl')}/?ref_url_domain=${codeFixUrl}`;
    return `${baseRedirectPath}&ref_url_path=/&app_type=Codefix`;
  }

  private getCodeFixUrl(location: Location): string {
    const codeFixUrl: string = `${location.protocol}//${location.hostname}`;
    return location.port ? `${codeFixUrl}:${location.port}` : codeFixUrl;
  }
}
