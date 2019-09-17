import { Injectable } from '@angular/core';

@Injectable()
export class DelayService {

  execute(delayedFunction: () => void, seconds: number): void {
    setTimeout(delayedFunction, seconds * 1000);
  }
}
