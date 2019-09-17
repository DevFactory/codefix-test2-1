import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { NavigationTabsComponent } from '@app/shared/components/navigation-tabs/navigation-tabs.component';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { NAVIGATION_TAB } from '@app/shared/mocks/navigation-tab';
import { NavigationTab } from '@app/shared/components/navigation-tabs/navigation-tab';

describe('NavigationTabsComponent', () => {

  let component: NavigationTabsComponent;
  let fixture: ComponentFixture<NavigationTabsComponent>;
  let router: jasmine.SpyObj<Router>;

  beforeEach(() => {
    router = jasmine.createSpyObj(['navigateByUrl']);

    TestBed.configureTestingModule({
      schemas: [NO_ERRORS_SCHEMA],
      declarations: [NavigationTabsComponent],
      providers: [
        {provide: Router, useValue: router},
      ]
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NavigationTabsComponent);
    component = fixture.componentInstance;
    component.tabs = [NAVIGATION_TAB];
    spyOn(component.tabClick, 'emit');
  });

  it('should be created', () => {
    expect(component).toBeTruthy();
  });

  it('#OnTabClick when tab is disable do not navigate', () => {
    const activeTab: NavigationTab = Object.create(NAVIGATION_TAB);
    activeTab.disabled = true;

    component.onTabClick(activeTab);

    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });

  it('#OnTabClick when tab does not have route do not navigate', () => {
    const activeTab: NavigationTab = Object.create(NAVIGATION_TAB);
    activeTab.route = '';

    component.onTabClick(activeTab);

    expect(router.navigateByUrl).not.toHaveBeenCalled();
  });

  it('#OnTabClick when tab is already active not event is emit', () => {
    const activeTab: NavigationTab = Object.create(NAVIGATION_TAB);
    activeTab.active = true;

    component.onTabClick(activeTab);

    expect(component.tabClick.emit).not.toHaveBeenCalled();
  });

  it('#OnTabClick when tab is not active not event is emit', () => {
    const activeTab: NavigationTab = Object.create(NAVIGATION_TAB);
    activeTab.active = false;

    component.onTabClick(activeTab);

    expect(component.tabClick.emit).toHaveBeenCalledWith(activeTab);
    expect(router.navigateByUrl).toHaveBeenCalledWith(activeTab.route);
  });
});
