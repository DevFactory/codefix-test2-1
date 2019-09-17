export class Page<T> {

  currentPage: number;
  pages: number;
  total: number;
  content: T[];

  constructor(currentPage: number, pages: number, total: number, content: T[]) {
    this.currentPage = currentPage;
    this.pages = pages;
    this.total = total;
    this.content = content;
  }

  withContent(newContent: T[]): Page<T> {
    return new Page(this.currentPage, this.pages, this.total, newContent);
  }

  isEmpty(): boolean {
    return this.content.length === 0;
  }

  isNotEmpty(): boolean {
    return this.content.length !== 0;
  }
}
