import { RepositoriesStore } from '@app/core/stores/repositories.store';
import { DescriptionComponent } from './description.component';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { DfCheckboxChange } from '@devfactory/ngx-df';

describe('DescriptionComponent', () => {
  const repositoriesStore: RepositoriesStore = new RepositoriesStore();
  let inputFunction: jasmine.SpyObj<Function>;

  let component: DescriptionComponent;
  let fixture: ComponentFixture<DescriptionComponent>;

  beforeEach(() => {
    inputFunction = jasmine.createSpyObj(['call']);

    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [DescriptionComponent],
      providers: [{provide: RepositoriesStore, useValue: repositoriesStore},]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DescriptionComponent);
    component = fixture.componentInstance;
    component.close = () => inputFunction.call();
  });

  it('#onGotItClick', () => {
    component.onGotItClick();

    expect(inputFunction.call).toHaveBeenCalledWith();
  });

  it('#onCheckboxChange', () => {
    const checkedChange = new DfCheckboxChange();
    checkedChange.checked = true;

    component.onCheckboxChange(checkedChange);
    expect(repositoriesStore.showModal.getValue()).toBe(false)
  });
});
