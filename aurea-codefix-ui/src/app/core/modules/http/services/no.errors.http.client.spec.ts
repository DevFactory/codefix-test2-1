import { HttpBackend } from '@angular/common/http';
import { Injector } from '@angular/core';
import { NoErrorsHttpClient } from '@app/core/modules/http/services/no.errors.http.client';

describe('NoErrorsHttpClient', () => {

  it('constructor', () => {
    new NoErrorsHttpClient(<HttpBackend>{}, <Injector>{});
  });
});
