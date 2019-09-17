import { Component, Input } from '@angular/core';
import { DfCheckboxChange } from '@devfactory/ngx-df/checkbox';
import { RepositoriesStore } from '@app/core/stores/repositories.store';

@Component({
  selector: 'app-description',
  templateUrl: './description.component.html',
  styleUrls: ['./description.component.scss'],
})
export class DescriptionComponent {
  @Input() close: Function;

  constructor(private repositoriesStore: RepositoriesStore) {
  }

  onGotItClick(): void {
    this.close();
  }

  onCheckboxChange(value: DfCheckboxChange): void {
    this.repositoriesStore.showModal.next(!value.checked);
  }
}
