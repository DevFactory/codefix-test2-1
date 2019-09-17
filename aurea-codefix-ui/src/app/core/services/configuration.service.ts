import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

@Injectable()
export class ConfigurationService {

  private readonly env: Map<string, string>;

  constructor() {
    this.env = new Map<string, string>();
    Object.entries(environment).forEach(([key, value]) => this.env.set(key, String(value)));
  }

  set(key: string, value: string): void {
    this.env.set(key, value);
  }

  get(key: string): string {
    return this.env.get(key);
  }
}
