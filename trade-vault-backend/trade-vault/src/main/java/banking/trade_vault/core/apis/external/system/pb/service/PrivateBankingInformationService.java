package banking.trade_vault.core.apis.external.system.pb.service;

import banking.trade_vault.core.apis.external.system.pb.models.Account;
import banking.trade_vault.core.apis.external.system.pb.models.AccountInformation;
import banking.trade_vault.core.apis.external.system.pb.models.Beneficiary;
import banking.trade_vault.core.apis.external.system.pb.models.Transaction;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import tools.jackson.databind.JsonNode;


import java.util.List;

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
