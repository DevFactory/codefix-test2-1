import {Component} from '@angular/core';
import {AuthService} from '@app/core/services/auth/auth.service';

@Component({
  selector: 'app-callback',
  templateUrl: './callback.component.html',
  styleUrls: ['./callback.component.scss']
})
export class CallbackComponent {

  constructor(auth: AuthService) {
    auth.handleAuthentication();
  }
}
