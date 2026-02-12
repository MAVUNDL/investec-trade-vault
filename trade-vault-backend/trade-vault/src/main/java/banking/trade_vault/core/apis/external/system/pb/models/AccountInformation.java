package banking.trade_vault.core.apis.external.system.pb.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AccountInformation(
        String accountId,
        BigDecimal currentBalance,
        BigDecimal availableBalance,
        String currency
) {}
