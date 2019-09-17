import { Page } from '@app/core/models/page';

describe('Page', () => {
  const currentPage: number = 1;
  const pages: number = 2;
  const total: number = 100;
  const element: string = 'an element';

  let page: Page<string>;

  beforeEach(() => {
    page = new Page<string>(currentPage, pages, total, [element]);
  });

  it('constructor', () => {
    expect(page.currentPage).toBe(currentPage);
    expect(page.pages).toBe(pages);
    expect(page.total).toBe(total);
    expect(page.content).toContain(element)
  });

  it('#isEmpty #isNotEmpty when not empty', () => {
    expect(page.isEmpty()).toBe(false);
    expect(page.isNotEmpty()).toBe(true);
  });

  it('#isEmpty #isNotEmpty when empty', () => {
    page = new Page<string>(currentPage, pages, total, []);

    expect(page.isEmpty()).toBe(true);
    expect(page.isNotEmpty()).toBe(false);
  });

  it('Page#withContent', () => {
    const newPage = new Page<string>(currentPage, pages, total, [])
      .withContent([element]);

    expect(newPage.currentPage).toBe(page.currentPage);
    expect(newPage.pages).toBe(page.pages);
    expect(newPage.total).toBe(page.total);
    expect(newPage.content).toContain(element);
  });

});
