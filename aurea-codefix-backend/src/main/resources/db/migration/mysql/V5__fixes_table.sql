CREATE TABLE FIXES (
    id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    issue_id BIGINT UNSIGNED NOT NULL,
    customer_id BIGINT UNSIGNED NOT NULL,
    df_jira_key nvarchar(255),
    pr_merged_time timestamp,
    FOREIGN KEY (issue_id) REFERENCES `ISSUES`(`id`),
    FOREIGN KEY (customer_id) REFERENCES `CUSTOMERS`(`id`)
);

ALTER TABLE `fixes`
ADD INDEX `FK_ISSUE_idx` (`issue_id` ASC),
ADD INDEX `FK_CUSTOMER_idx` (`customer_id` ASC);