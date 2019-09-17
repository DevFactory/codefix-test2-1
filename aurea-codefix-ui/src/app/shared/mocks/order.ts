import { OrderIssue } from '../../modules/issues/process/model/order.issue';
import { Order } from '../../modules/issues/process/model/order';
import { IssueStatus } from '../../modules/issues/process/services/orders.service';

export function SimpleOrder(): Order {
  return new Order(new Date(), new Date(), [SimpleOrderIssue()]);
}

export function SimpleOrderIssue(): OrderIssue {
  return new OrderIssue(1, 'type', IssueStatus.IN_PROCESS, 'description', 'repository', 'branch');
}
