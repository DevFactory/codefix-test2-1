import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AppRoutes } from '@app/shared/config/app.routes';
import { AuthService } from '@app/core/services/auth/auth.service';
import { UserStore } from '@app/core/stores/user.store';

@Component({
  selector: 'app-terms',
  templateUrl: './terms.component.html',
  styleUrls: ['./terms.component.scss'],
})
export class TermsComponent {
  constructor(
    private router: Router,
    private authService: AuthService,
    private userStore: UserStore) {
  }

  onCancelClick(): void {
    this.userStore.acceptTerms.next(false);
    this.authService.logout();
  }

  onAcceptClick(): void {
    this.userStore.acceptTerms.next(true);
    this.router.navigate([AppRoutes.RepoList]);
  }
}
