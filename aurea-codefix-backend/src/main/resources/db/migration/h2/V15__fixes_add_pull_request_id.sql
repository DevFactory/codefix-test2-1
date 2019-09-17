ALTER TABLE FIXES
ADD COLUMN pull_request_id nvarchar(255);

ALTER TABLE FIXES
ADD COLUMN status nvarchar(64) not null default 'NONE'
