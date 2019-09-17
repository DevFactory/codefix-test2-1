import { HttpBackend } from '@angular/common/http';
import { Injector } from '@angular/core';
import { SilentHttpClient } from '@app/core/modules/http/services/silent.http.client';

describe('SilentHttpClient', () => {

  it('constructor', () => {
    new SilentHttpClient(<HttpBackend>{}, <Injector>{});
  });
});
