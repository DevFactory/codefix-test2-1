import { Component, HostBinding } from '@angular/core';
import { AuthService } from '@app/core/services/auth/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss'],
})
export class HomeComponent {
  @HostBinding() class: string = 'd-flex flex-column col p-0 overflow-y-auto';

  constructor(private authService: AuthService) {
  }

  login(): void {
    this.authService.login();
  }
}
