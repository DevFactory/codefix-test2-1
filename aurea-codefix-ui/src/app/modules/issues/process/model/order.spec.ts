import { SimpleOrderIssue } from '@app/shared/mocks/order';
import { Order } from './order';
import { OrderIssue } from './order.issue';
import { IssueStatus } from '../services/orders.service';

describe('Order', () => {

  it('#constructor', () => {
    let startDate: Date = new Date();
    let dueDate: Date = new Date(startDate.getDate() + 1);
    let issue: OrderIssue = SimpleOrderIssue();

    let order = new Order(startDate, dueDate, [issue]);
    expect(order.startDate).toEqual(startDate);
    expect(order.dueDate).toEqual(dueDate);
    expect(order.issues).toContain(issue);
  });

  it('#activeIssues', () => {
    let inProcessIssue: OrderIssue = createIssue(IssueStatus.IN_PROCESS);
    let completedIssue: OrderIssue = createIssue(IssueStatus.COMPLETED);

    let order = new Order(new Date(), new Date(), [inProcessIssue, completedIssue]);
    expect(order.activeIssues).toContain(inProcessIssue);
  });

  function createIssue(issueStatus: IssueStatus): OrderIssue {
    let issue: OrderIssue = SimpleOrderIssue();
    issue.status = issueStatus;
    return issue
  }
});
