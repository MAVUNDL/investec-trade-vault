ALTER TABLE beneficiary ADD ingested_at TIMESTAMP;
ALTER TABLE transactions ADD ingested_at TIMESTAMP;
ALTER TABLE account_information ADD ingested_at TIMESTAMP;