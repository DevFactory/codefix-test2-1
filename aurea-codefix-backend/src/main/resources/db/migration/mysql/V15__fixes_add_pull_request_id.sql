ALTER TABLE FIXES
ADD pull_request_id nvarchar(255);

ALTER TABLE FIXES
ADD status nvarchar(64) not null default 'NONE'
