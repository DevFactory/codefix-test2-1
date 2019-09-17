CREATE TABLE ISSUES (
  id                    BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  repository_id         BIGINT UNSIGNED NOT NULL,
  issue_type_id         BIGINT UNSIGNED NOT NULL,
  analysis_request_id   BIGINT UNSIGNED NOT NULL,
  insight_storage_id    BIGINT,
  order_id              BIGINT UNSIGNED,
  last_modified         datetime NOT NULL,
  issue_name            nvarchar(255) NOT NULL,
  issue_desc            text NOT NULL,
  severity              nvarchar(255) NOT NULL
);

CREATE TABLE ISSUE_TYPE (
  id              BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  description     nvarchar(255) NOT NULL
);

CREATE TABLE ORDER_INFORMATION (
  id                BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  start_date        datetime NOT NULL,
  due_date          datetime NOT NULL,
  issues_fix_cycle  datetime NOT NULL,
  status            nvarchar(255),
  fail_reason       nvarchar(255)
);

CREATE TABLE ANALYSIS_REQUEST (
  id                BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  repository_id     BIGINT NOT NULL,
  request_id        nvarchar(255),
  batch_id          nvarchar(255),
  revision          nvarchar(255),
  status            nvarchar(255),
  last_updated      datetime NOT NULL
);

ALTER TABLE `issues`
ADD INDEX `FK_REPOSITORIES_idx` (`repository_id` ASC),
ADD INDEX `FK_ISSUE_TYPE_idx` (`issue_type_id` ASC),
ADD INDEX `FK_ANALYSIS_REQUEST_idx` (`analysis_request_id` ASC),
ADD INDEX `FK_ORDER_INFORMATION_idx` (`order_id` ASC);

ALTER TABLE `issues`
ADD CONSTRAINT `FK_REPOSITORIES`
  FOREIGN KEY (`repository_id`)
  REFERENCES `repositories` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `FK_ISSUE_TYPE`
  FOREIGN KEY (`issue_type_id`)
  REFERENCES `issue_type` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `FK_ANALYSIS_REQUEST`
  FOREIGN KEY (`analysis_request_id`)
  REFERENCES `analysis_request` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION,
ADD CONSTRAINT `FK_ORDER_INFORMATION`
  FOREIGN KEY (`order_id`)
  REFERENCES `order_information` (`id`)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
