import { Completed } from 'app/modules/issues/completed/model/completed';

export const SimpleCompletedId: number = 1;

export const SimpleCompleted: Completed = new Completed(SimpleCompletedId, 'type', 'description', 'repository', 'branch');
export const CompletedResult: Completed[] = [SimpleCompleted];
export const CompletedEmptyResult: Completed[] = [];
