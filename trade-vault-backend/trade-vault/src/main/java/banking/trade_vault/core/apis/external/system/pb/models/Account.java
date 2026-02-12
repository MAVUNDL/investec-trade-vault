package banking.trade_vault.core.apis.external.system.pb.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Account(
        String accountId,
        String accountNumber,
        String accountName,
        String referenceName,
        String productName,
        Boolean kycCompliant,
        String profileId
) {}
