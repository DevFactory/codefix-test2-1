UPDATE REPOSITORIES SET active_codefix = 1 WHERE active_codefix is null;
ALTER TABLE REPOSITORIES MODIFY COLUMN active_codefix bit not null default 0;
