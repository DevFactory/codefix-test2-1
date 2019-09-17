import { HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { InterceptHandler } from '@app/core/modules/http/common/intercept.handler';

describe('InterceptHandler', () => {
  let handler: jasmine.SpyObj<HttpHandler>;
  let interceptor: jasmine.SpyObj<HttpInterceptor>;
  let request: HttpRequest<any>;
  let interceptorHandler: InterceptHandler;

  beforeEach(() => {
    handler = jasmine.createSpyObj(['handle']);
    interceptor = jasmine.createSpyObj(['intercept']);
    request = jasmine.createSpyObj(['intercept']);
  });

  beforeEach(() => {
    interceptorHandler = new InterceptHandler(handler, interceptor);
  });

  it('#handle', () => {
    interceptorHandler.handle(request);

    expect(interceptor.intercept).toHaveBeenCalledWith(request, handler);
  });

});
