CREATE TABLE CUSTOMERS
(
    id            BIGINT GENERATED BY DEFAULT AS IDENTITY,
    name          nvarchar(255) not null,
    last_modified dateTime,
    email         nvarchar(255) not null
);

CREATE TABLE REPOSITORIES
(
    id             BIGINT GENERATED BY DEFAULT AS IDENTITY,
    customer_id    BIGINT,
    branch         nvarchar(255) not null,
    loc            BIGINT,
    url            nvarchar(255) not null,
    active_codefix bit
);
