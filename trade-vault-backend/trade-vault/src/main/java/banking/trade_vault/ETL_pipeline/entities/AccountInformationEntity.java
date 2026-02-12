package banking.trade_vault.ETL_pipeline.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountInformationEntity(
        String accountId,
        BigDecimal currentBalance,
        BigDecimal availableBalance,
        String currency,
        LocalDateTime ingestedAt
){}
