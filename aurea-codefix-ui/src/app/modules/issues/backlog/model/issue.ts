export class Issue {
  id?: number;
  order?: number;
  type?: string;
  description?: string;
  repository?: string;
  branch?: string;
  justMoved?: boolean;
  issueUrl?: string;

  constructor(id: number, order: number, type: string, description: string, repository: string, branch: string, issueUrl: string) {
    this.id = id;
    this.order = order;
    this.type = type;
    this.description = description;
    this.repository = repository;
    this.branch = branch;
    this.issueUrl = issueUrl;
  }
}
