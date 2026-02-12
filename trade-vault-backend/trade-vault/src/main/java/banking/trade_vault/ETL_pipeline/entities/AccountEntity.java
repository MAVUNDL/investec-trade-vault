package banking.trade_vault.ETL_pipeline.entities;

import java.time.LocalDateTime;

public record AccountEntity(
        String accountId,
        String accountNumber,
        String accountName,
        String referenceName,
        String productName,
        Boolean kycCompliant,
        String profileId,
        LocalDateTime ingestedAt
) {}
