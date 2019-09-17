import { HttpBackend, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { InjectionToken, Injector } from '@angular/core';
import { Observable } from 'rxjs';
import { InterceptHandler } from '@app/core/modules/http/common/intercept.handler';

/**
 * Allow to inject customer Http Interceptors per instance.
 */
export class InterceptorsHandler implements HttpHandler {

  constructor(private backend: HttpBackend,
              private injector: Injector,
              private interceptors: InjectionToken<HttpInterceptor[]>,
              private chain: HttpHandler = null) {
  }

  handle(req: HttpRequest<any>): Observable<HttpEvent<any>> {
    if (this.chain === null) {
      const interceptors: HttpInterceptor[] = this.injector.get(this.interceptors, Array<HttpInterceptor>());
      this.chain = interceptors.reduceRight((next, interceptor) => new InterceptHandler(next, interceptor), this.backend);
    }

    return this.chain.handle(req);
  }
}
