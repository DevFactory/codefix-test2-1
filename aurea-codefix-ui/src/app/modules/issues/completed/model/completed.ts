export class Completed {
  id?: number;
  type?: string;
  description?: string;
  repository?: string;
  branch?: string;

  constructor(id: number, type: string, description: string, repository: string, branch: string) {
    this.id = id;
    this.type = type;
    this.description = description;
    this.repository = repository;
    this.branch = branch;
  }
}
