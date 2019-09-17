import { NgModule } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { LOADING_HTTP_INTERCEPTORS, LoadingHttpClient } from '@app/core/modules/http/services/loading.http.client';
import { DfHttpErrorInterceptor, DfHttpIEInterceptor, DfHttpLoaderInterceptor } from '@devfactory/ngx-df';
import { TokenInterceptor } from '@app/core/services/auth/token.interceptor';
import { SILENT_HTTP_INTERCEPTORS, SilentHttpClient } from '@app/core/modules/http/services/silent.http.client';
import { NO_ERRORS_HTTP_INTERCEPTORS, NoErrorsHttpClient } from '@app/core/modules/http/services/no.errors.http.client';

@NgModule({
  imports: [HttpClientModule],
  providers: [
    {
      provide: LOADING_HTTP_INTERCEPTORS,
      useClass: DfHttpLoaderInterceptor,
      multi: true,
    },
    {
      provide: LOADING_HTTP_INTERCEPTORS,
      useClass: DfHttpErrorInterceptor,
      multi: true,
    },
    {
      provide: LOADING_HTTP_INTERCEPTORS,
      useClass: DfHttpIEInterceptor,
      multi: true,
    },
    {
      provide: LOADING_HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    },
    {
      provide: SILENT_HTTP_INTERCEPTORS,
      useClass: DfHttpErrorInterceptor,
      multi: true,
    },
    {
      provide: SILENT_HTTP_INTERCEPTORS,
      useClass: DfHttpIEInterceptor,
      multi: true,
    },
    {
      provide: SILENT_HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    },
    {
      provide: NO_ERRORS_HTTP_INTERCEPTORS,
      useClass: DfHttpIEInterceptor,
      multi: true,
    },
    {
      provide: NO_ERRORS_HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    },
    LoadingHttpClient,
    SilentHttpClient,
    NoErrorsHttpClient]
})
export class CustomHttpModule {
}
