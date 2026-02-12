DROP TABLE transactions;

CREATE TABLE IF NOT EXISTS transactions(
       id SERIAL PRIMARY KEY,
       account_id VARCHAR references account(account_id) ON DELETE CASCADE UNIQUE ,
       type VARCHAR,
       transaction_type VARCHAR,
       status VARCHAR,
       description VARCHAR,
       card_number VARCHAR,
       posting_date DATE,
       value_date DATE,
       action_date DATE,
       transaction_date DATE,
       amount NUMERIC(19,4) DEFAULT 0.0000,
       running_balance NUMERIC(19,4) DEFAULT 0.0000,
       uuid VARCHAR
);