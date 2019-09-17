export class RepositoryUtils {
  static formatRowName(name: string): string {
    const data: string[] = name.split('/');
    return `${data[data.length - 2]} / <strong>${data[data.length - 1].replace('.git', '')}</strong>`;
  }
}
