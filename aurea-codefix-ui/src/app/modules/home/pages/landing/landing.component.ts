import { Component, HostBinding } from '@angular/core';
import { AuthService } from '@app/core/services/auth/auth.service';

@Component({
  selector: 'app-landing',
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.scss'],
})
export class LandingComponent {
  @HostBinding() class: string = 'd-block';

  constructor(
    private authService: AuthService) {
  }

  login(): void {
    this.authService.login();
  }
}
