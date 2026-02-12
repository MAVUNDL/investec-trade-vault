package banking.trade_vault.core.apis.external.controller;

import banking.trade_vault.core.apis.external.system.pb.models.Account;
import banking.trade_vault.core.apis.external.system.pb.models.AccountInformation;
import banking.trade_vault.core.apis.external.system.pb.models.Beneficiary;
import banking.trade_vault.core.apis.external.system.pb.models.Transaction;
import banking.trade_vault.core.apis.external.system.pb.service.PrivateBankingInformationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/banking")
public class SystemController {
    private final PrivateBankingInformationService pbService;
    private final ObjectMapper mapper =  new ObjectMapper();

    public SystemController(PrivateBankingInformationService pbService) {
        this.pbService = pbService;
    }

    @GetMapping("/accounts")
    public List<Account> findAllAccounts(){
        tools.jackson.databind.JsonNode root = pbService.getAccounts();
        JsonNode accountsNode = root.path("data").path("accounts");

        if (accountsNode.isMissingNode() || !accountsNode.isArray()) {
            return List.of();
        }

        try {
            return mapper.convertValue(accountsNode, new TypeReference<List<Account>>(){});
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }


    @GetMapping("/account-information/{accountId}")
    public AccountInformation findAccountById(@PathVariable String accountId){
        tools.jackson.databind.JsonNode root = pbService.getAccountInformation(accountId);
        JsonNode accountNode = root.path("data");

        if (accountNode.isMissingNode()) {
            return null;
        }

        try {
            return mapper.convertValue(accountNode, new TypeReference<AccountInformation>(){});
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @GetMapping("/profile/{profileId}/account/{accountId}/beneficiaries")
    public List<Beneficiary> getBeneficiaries(@PathVariable String profileId, @PathVariable String accountId){
        tools.jackson.databind.JsonNode root = pbService.getBeneficiaries(profileId, accountId);
        JsonNode beneficiariesNode = root.path("data");

        if (beneficiariesNode.isMissingNode() || !beneficiariesNode.isArray()) {
            return List.of();
        }

        try {
            return mapper.convertValue(beneficiariesNode, new TypeReference<List<Beneficiary>>(){});
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    @GetMapping("/account/{accountId}/transactions")
    public List<Transaction> getTransactions(@PathVariable String accountId){
        tools.jackson.databind.JsonNode root = pbService.getTransactions(accountId);
        JsonNode transactionsNode = root.path("data").path("transactions");

        if (transactionsNode.isMissingNode() || !transactionsNode.isArray()) {
            return List.of();
        }

        try {
            return mapper.convertValue(transactionsNode, new TypeReference<List<Transaction>>() {});
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }
}
