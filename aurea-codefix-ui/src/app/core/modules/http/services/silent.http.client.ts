import { Injectable, InjectionToken, Injector } from '@angular/core';
import { HttpBackend, HttpClient, HttpInterceptor } from '@angular/common/http';
import { InterceptorsHandler } from '@app/core/modules/http/common/interceptors.handler';

export const SILENT_HTTP_INTERCEPTORS: InjectionToken<HttpInterceptor[]>
  = new InjectionToken<HttpInterceptor[]>('SILENT_HTTP_INTERCEPTORS');

@Injectable()
export class SilentHttpClient extends HttpClient {

  constructor(backend: HttpBackend, injector: Injector) {
    super(new InterceptorsHandler(backend, injector, SILENT_HTTP_INTERCEPTORS));
  }
}
