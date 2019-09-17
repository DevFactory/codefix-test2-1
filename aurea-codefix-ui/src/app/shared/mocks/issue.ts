import { IssuePriority } from 'app/modules/issues/backlog/model/issuePriority';
import { Issue } from 'app/modules/issues/backlog/model/issue';

// Dummy data entities
export const SimpleIssueId: number = 1;

export const SimpleIssue: Issue = new Issue(SimpleIssueId, 1, 'type', 'description', 'repository', 'branch', 'issueUrl' );
export const SimpleIssueTwo: Issue = new Issue(2, 2, 'type', 'description', 'repository', 'branch', 'issueUrl');
export const SimpleIssueThree: Issue = new Issue(3, 3, 'type', 'description', 'repository', 'branch', 'issueUrl');
export const SimpleIssueFour: Issue = new Issue(4, 4, 'type', 'description', 'repository', 'branch', 'issueUrl');
export const SimpleIssueFive: Issue = new Issue(5, 5, 'type', 'description', 'repository', 'branch', 'issueUrl');
export const IssueList: Issue[] = [SimpleIssue, SimpleIssueTwo];
export const IssueListFive: Issue[] = [SimpleIssue, SimpleIssueTwo, SimpleIssueThree, SimpleIssueFour, SimpleIssueFive];
export const IssuePriorityList: IssuePriority[] = [new IssuePriority(1, 1), new IssuePriority(2, 2)];

