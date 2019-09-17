import { TokenInterceptor } from '@app/core/services/auth/token.interceptor';
import { AuthService } from '@app/core/services/auth/auth.service';
import { HttpHandler, HttpRequest } from '@angular/common/http';

describe('TokenInterceptor', () => {

  let tokenInterceptor: TokenInterceptor;

  let authService: jasmine.SpyObj<AuthService>;
  let request: jasmine.SpyObj<HttpRequest<any>>;
  let httpHandler: jasmine.SpyObj<HttpHandler>;
  const token: string = 'a_token';

  beforeEach(() => {
    authService = jasmine.createSpyObj(['getToken']);
    request = jasmine.createSpyObj(['clone']);
    httpHandler = jasmine.createSpyObj(['handle']);

    tokenInterceptor = new TokenInterceptor(authService);
  });

  it('#intercept', () => {
    request.clone.and.returnValue(request);
    authService.getToken.and.returnValue(token);

    tokenInterceptor.intercept(request, httpHandler);

    expect(request.clone).toHaveBeenCalledWith({setHeaders: {Authorization: 'Bearer a_token'}});
    expect(httpHandler.handle).toHaveBeenCalledWith(request);
  });
});
