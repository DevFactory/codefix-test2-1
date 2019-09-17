import { LoadingHttpClient } from '@app/core/modules/http/services/loading.http.client';
import { HttpBackend } from '@angular/common/http';
import { Injector } from '@angular/core';

describe('LoadingHttpClient', () => {

  it('constructor', () => {
    new LoadingHttpClient(<HttpBackend>{}, <Injector>{});
  });
});
