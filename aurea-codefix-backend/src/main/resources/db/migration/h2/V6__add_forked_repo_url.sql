ALTER TABLE `ISSUES` ADD COLUMN `forked_repo_url` nvarchar(255) NOT NULL ;
ALTER TABLE `ORDER_INFORMATION` MODIFY COLUMN issues_fix_cycle BIGINT NOT NULL;