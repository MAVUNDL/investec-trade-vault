package banking.trade_vault.ETL_pipeline.investec.api.sections.pb.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Beneficiary(
        String beneficiaryId,
        String accountNumber,
        String code,
        String bank,
        String beneficiaryName,
        String lastPaymentAmount,
        String lastPaymentDate,
        String cellNo,
        String emailAddress,
        String name,
        String referenceAccountNumber,
        String referenceName,
        Boolean fasterPaymentAllowed,
        String beneficiaryType,
        String approvedBeneficiaryCategory
) {}
