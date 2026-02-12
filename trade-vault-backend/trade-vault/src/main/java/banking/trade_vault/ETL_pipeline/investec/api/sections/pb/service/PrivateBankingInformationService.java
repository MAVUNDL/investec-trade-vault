package banking.trade_vault.ETL_pipeline.investec.api.sections.pb.service;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import tools.jackson.databind.JsonNode;

@HttpExchange(url = "/za/pb/v1")
public interface PrivateBankingInformationService {
    @GetExchange("/accounts")
    JsonNode getAccounts();

    @GetExchange("/accounts/{accountId}/balance")
    JsonNode getAccountInformation(@PathVariable String accountId);

    @GetExchange("/profiles/{profileId}/accounts/{accountId}/beneficiaries")
    JsonNode getBeneficiaries(@PathVariable String profileId, @PathVariable String accountId);

    @GetExchange("/accounts/{accountId}/transactions?includePending=true")
    JsonNode getTransactions(@PathVariable String accountId);
}
