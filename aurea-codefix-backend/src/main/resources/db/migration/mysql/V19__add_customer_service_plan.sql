CREATE TABLE service_plans (
    id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name nvarchar(255) NOT NULL
);

INSERT INTO service_plans (id, name) VALUES (1, 'Free');

ALTER TABLE customers
ADD COLUMN service_plan_id BIGINT UNSIGNED NOT NULL DEFAULT 1;

ALTER TABLE customers
ADD CONSTRAINT fk_service_plans
  FOREIGN KEY (service_plan_id)
  REFERENCES service_plans (id)
  ON DELETE NO ACTION
  ON UPDATE NO ACTION;
