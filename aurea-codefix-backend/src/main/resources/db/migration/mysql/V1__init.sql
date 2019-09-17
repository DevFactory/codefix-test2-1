CREATE TABLE CUSTOMERS(
    id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    name nvarchar(255) not null,
    last_modified dateTime,
    email nvarchar(255) not null
);

CREATE TABLE REPOSITORIES (
    id BIGINT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
    customer_id  BIGINT,
    branch nvarchar(255) not null,
    loc BIGINT,
    url nvarchar(255) not null,
    active_codefix bit
);
