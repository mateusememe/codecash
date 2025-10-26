CREATE TABLE users
(
    id         UUID PRIMARY KEY,
    name       VARCHAR(255)             NOT NULL,
    email      VARCHAR(255)             NOT NULL UNIQUE,
    document   VARCHAR(20)              NOT NULL UNIQUE,
    password   VARCHAR(255)             NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE accounts
(
    id      UUID PRIMARY KEY,
    balance NUMERIC(19, 2) NOT NULL,
    user_id UUID           NOT NULL UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE transactions
(
    id               UUID PRIMARY KEY,
    amount           NUMERIC(19, 2)           NOT NULL,
    payer_account_id UUID                     NOT NULL,
    payee_account_id UUID                     NOT NULL,
    transaction_time TIMESTAMP WITH TIME ZONE NOT NULL,
    FOREIGN KEY (payer_account_id) REFERENCES accounts (id),
    FOREIGN KEY (payee_account_id) REFERENCES accounts (id)
);