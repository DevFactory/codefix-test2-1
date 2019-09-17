import { HttpBackend, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { InjectionToken, Injector } from '@angular/core';
import { InterceptorsHandler } from '@app/core/modules/http/common/interceptors.handler';

describe('InterceptorsHandler', () => {

  let httpBackend: jasmine.SpyObj<HttpBackend>;
  let injector: jasmine.SpyObj<Injector>;
  let interceptors: InjectionToken<HttpInterceptor[]>;
  let interceptor: jasmine.SpyObj<HttpInterceptor>;
  let handler: jasmine.SpyObj<HttpHandler>;

  let interceptorHandler: InterceptorsHandler;

  beforeEach(() => {
    httpBackend = jasmine.createSpyObj(['handle']);
    injector = jasmine.createSpyObj(['get']);
    interceptors = <InjectionToken<HttpInterceptor[]>>{};
    interceptor = jasmine.createSpyObj(['intercept']);
    handler = jasmine.createSpyObj(['handle']);
  });

  beforeEach(() => {
    interceptorHandler = new InterceptorsHandler(httpBackend, injector, interceptors);
  });

  it('#handle when chain is null', () => {
    injector.get.and.returnValue([interceptor]);
    const request: HttpRequest<any> = <HttpRequest<any>>{};

    interceptorHandler.handle(request);

    expect(injector.get).toHaveBeenCalledWith(interceptors, []);
    expect(interceptor.intercept).toHaveBeenCalledWith(request, httpBackend)
  });

  it('#handle when chain is not null', () => {
    const request: HttpRequest<any> = <HttpRequest<any>>{};

    interceptorHandler = new InterceptorsHandler(httpBackend, injector, interceptors, handler);
    interceptorHandler.handle(request);

    expect(handler.handle).toHaveBeenCalledWith(request);
  });
});
