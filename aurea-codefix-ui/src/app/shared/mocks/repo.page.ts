import { Page } from '@app/core/models/page';
import { IgnoredRepo } from '../../modules/repo-list/model/ignored-repo';
import { SyncResult } from '../../modules/repo-list/model/sync.resutl';
import { Repo } from '../../modules/repo-list/model/repo';

// Repo dummy data
export const SimpleRepoId: number = 1;
export const SimpleRepo: Repo = new Repo(SimpleRepoId, 'branchName', 'repo-url', true);

// Ignored repo
export const IgnoredRepoMessage: string = 'Repository ignored';
export const SimpleIgnoredRepo: IgnoredRepo = {branch: 'branchName', url: 'repo-url', reason: IgnoredRepoMessage};

// Page dummy data
export const currentPage: number = 1;
export const pages: number = 2;
export const total: number = 100;

// Dummy data entities
export const RepoPage: Page<Repo> = new Page<Repo>(currentPage, pages, total, [SimpleRepo]);
export const EmptyRepoPage: Page<Repo> = new Page<Repo>(0, 0, 0, []);

// Sync results
export const SimpleSyncResult: SyncResult = new SyncResult(RepoPage, []);
export const EmptySyncResult: SyncResult = {repositories: EmptyRepoPage, ignoredItems: []};
export const IgnoredSyncResult: SyncResult = {repositories: EmptyRepoPage, ignoredItems: [SimpleIgnoredRepo]};
