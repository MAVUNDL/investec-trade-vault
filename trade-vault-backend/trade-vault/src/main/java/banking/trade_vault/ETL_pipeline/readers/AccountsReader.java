package banking.trade_vault.ETL_pipeline.readers;

import banking.trade_vault.ETL_pipeline.investec.api.sections.pb.models.Account;
import banking.trade_vault.ETL_pipeline.investec.api.sections.pb.service.PrivateBankingInformationService;
import tools.jackson.databind.ObjectMapper;

import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.core.type.TypeReference;

import java.util.Iterator;
import java.util.List;

@Component
public class AccountsReader implements ItemReader<Account> {

    private final PrivateBankingInformationService pbService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private Iterator<Account> accountIterator;

    public AccountsReader(PrivateBankingInformationService pbService) {
        this.pbService = pbService;
    }

    @Override
    public Account read() throws Exception {
        if (accountIterator == null) {
            tools.jackson.databind.JsonNode root = pbService.getAccounts();
            JsonNode accountsNode = root.path("data").path("accounts");

            if ( accountsNode.isMissingNode() || !accountsNode.isArray()) {
                return null;
            }

            List<Account> accounts = objectMapper.convertValue(accountsNode, new TypeReference<List<Account>>() {});
            accountIterator = accounts.iterator();
        }

        if (accountIterator.hasNext()) {
            return accountIterator.next();
        } else {
            return null;
        }
    }
}
