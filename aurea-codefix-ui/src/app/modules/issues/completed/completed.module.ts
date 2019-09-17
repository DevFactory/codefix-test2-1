import { CommonModule } from '@angular/common';
import { NgModule} from '@angular/core';
import { SharedModule } from '@app/shared/shared.module';
import { CompletedRoutingModule } from './completed-routing.module';
import { CompletedComponent } from './pages/completed.component';
import { CompletedGridComponent } from './components/completed-grid/completed-grid.component';

@NgModule({
  imports: [CommonModule, CompletedRoutingModule, SharedModule],
  declarations: [CompletedComponent, CompletedGridComponent]
})
export class CompletedModule {
}
