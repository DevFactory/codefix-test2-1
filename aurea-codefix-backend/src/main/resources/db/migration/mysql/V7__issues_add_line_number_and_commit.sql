ALTER TABLE ISSUES
ADD   issue_commit          nvarchar(255) NOT NULL;

ALTER TABLE ISSUES
ADD   line_number           BIGINT UNSIGNED NOT NULL;
