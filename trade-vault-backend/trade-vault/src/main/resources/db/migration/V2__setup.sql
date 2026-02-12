
CREATE TABLE IF NOT EXISTS account(
      id SERIAL PRIMARY KEY,
      account_id VARCHAR UNIQUE NOT NULL,
      account_number VARCHAR,
      account_name VARCHAR,
      reference_name VARCHAR,
      product_name VARCHAR,
      kyc_compliant BOOLEAN,
      profile_id VARCHAR UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS  account_information(
    id SERIAL PRIMARY KEY,
    account_id VARCHAR references account(account_id) ON DELETE CASCADE UNIQUE ,
    current_balance NUMERIC(19,4) DEFAULT 0.0000,
    available_balance NUMERIC(19,4) DEFAULT 0.0000,
    currency VARCHAR(3)
);

CREATE TABLE IF NOT EXISTS transactions(
    id SERIAL PRIMARY KEY,
    account_id VARCHAR references account(account_id) ON DELETE CASCADE,
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

CREATE TABLE IF NOT EXISTS beneficiary (
    id SERIAL PRIMARY KEY,
    profile_id VARCHAR REFERENCES account(profile_id) ON DELETE CASCADE,
    beneficiary_id VARCHAR UNIQUE,
    account_number VARCHAR,
    code VARCHAR,
    bank VARCHAR,
    beneficiary_name VARCHAR,
    last_payment_amount NUMERIC(19,4),
    last_payment_date DATE,
    cell_no VARCHAR,
    email_address VARCHAR,
    name VARCHAR,
    reference_account_number VARCHAR,
    reference_name VARCHAR,
    faster_payment_allowed BOOLEAN,
    beneficiary_type VARCHAR,
    approved_beneficiary_category VARCHAR
);

