ALTER TABLE ORDER_INFORMATION ADD customer_id BIGINT NOT NULL;

ALTER TABLE ORDER_INFORMATION DROP COLUMN issues_fix_cycle;

ALTER TABLE ORDER_INFORMATION ADD issues_fix_cycle BIGINT UNSIGNED NOT NULL;
