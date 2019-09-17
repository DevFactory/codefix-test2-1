export class IssuePriority {
    priority?: number;
    issueId?: number;

    constructor(priority: number, issueId: number) {
        this.priority = priority;
        this.issueId = issueId;
      }
}
