import { DelayService } from '@app/core/services/delay.service';

describe('DelayService', () => {

  const delayService: DelayService = new DelayService();
  let timerCallback: any;

  beforeEach(() => {
    timerCallback = jasmine.createSpy('timerCallback');
    jasmine.clock().install();
  });

  afterEach(() => {
    jasmine.clock().uninstall();
  });

  it('execute when should be executed', () => {
    delayService.execute(timerCallback, 1);
    expect(timerCallback).not.toHaveBeenCalled();

    jasmine.clock().tick(1001);

    expect(timerCallback).toHaveBeenCalled();
  });
})
;
