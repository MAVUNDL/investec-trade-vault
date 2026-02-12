DROP TABLE beneficiary;


CREATE TABLE IF NOT EXISTS beneficiary (
    id SERIAL PRIMARY KEY,
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
    category_id VARCHAR,
    profile_id VARCHAR NOT NULL,
    faster_payment_allowed BOOLEAN
);