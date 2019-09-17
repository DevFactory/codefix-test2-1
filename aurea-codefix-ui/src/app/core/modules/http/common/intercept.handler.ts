import { HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * Call the given Http interceptor over the request.
 */
export class InterceptHandler implements HttpHandler {
  constructor(private next: HttpHandler, private interceptor: HttpInterceptor) {
  }

  handle(req: HttpRequest<any>): Observable<HttpEvent<any>> {
    return this.interceptor.intercept(req, this.next);
  }
}
