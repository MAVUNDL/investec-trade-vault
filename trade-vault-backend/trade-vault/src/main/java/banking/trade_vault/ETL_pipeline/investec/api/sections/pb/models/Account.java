package banking.trade_vault.ETL_pipeline.investec.api.sections.pb.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.beans.factory.annotation.Autowired;


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
