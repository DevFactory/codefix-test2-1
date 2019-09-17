import { Injectable, InjectionToken, Injector } from '@angular/core';
import { HttpBackend, HttpClient, HttpInterceptor } from '@angular/common/http';
import { InterceptorsHandler } from '@app/core/modules/http/common/interceptors.handler';

export const NO_ERRORS_HTTP_INTERCEPTORS: InjectionToken<HttpInterceptor[]>
  = new InjectionToken<HttpInterceptor[]>('NO_ERRORS_HTTP_INTERCEPTORS');

@Injectable()
export class NoErrorsHttpClient extends HttpClient {

  constructor(backend: HttpBackend, injector: Injector) {
    super(new InterceptorsHandler(backend, injector, NO_ERRORS_HTTP_INTERCEPTORS));
  }
}
