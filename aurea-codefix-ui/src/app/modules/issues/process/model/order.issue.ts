import { IssueStatus } from '../services/orders.service';

export class OrderIssue {
  id: number;
  type: string;
  status: IssueStatus;
  description: string;
  repository: string;
  branch: string;

  constructor(id: number, type: string, status: IssueStatus, description: string, repository: string, branch: string) {
    this.id = id;
    this.type = type;
    this.description = description;
    this.repository = repository;
    this.branch = branch;
    this.status = status;
  }
}
