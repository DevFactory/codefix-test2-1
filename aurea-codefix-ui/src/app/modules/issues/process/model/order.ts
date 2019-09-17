import { OrderIssue } from './order.issue';
import { IssueStatus } from '../services/orders.service';

export class Order {
  startDate: Date;
  dueDate: Date;
  issues: OrderIssue[];

  get activeIssues(): OrderIssue[] {
    return this.issues.filter(issue => issue.status === IssueStatus.IN_PROCESS);
  }

  constructor(startDate: Date, dueDate: Date, issues: OrderIssue[]) {
    this.startDate = startDate;
    this.dueDate = dueDate;
    this.issues = issues;
  }
}
