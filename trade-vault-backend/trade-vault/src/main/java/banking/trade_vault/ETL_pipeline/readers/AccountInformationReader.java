package banking.trade_vault.ETL_pipeline.readers;

import banking.trade_vault.ETL_pipeline.investec.api.sections.pb.models.AccountInformation;
import banking.trade_vault.ETL_pipeline.investec.api.sections.pb.service.PrivateBankingInformationService;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;

import java.util.Iterator;
import java.util.List;

@Component
public class AccountInformationReader implements ItemReader<AccountInformation> {

    private final JdbcTemplate jdbcTemplate;
    private final PrivateBankingInformationService pbService;
    private final ObjectMapper objectMapper;

    private Iterator<String> accountIterator; // iterates over account IDs

    public AccountInformationReader(JdbcTemplate jdbcTemplate,
                                    PrivateBankingInformationService pbService,
                                    ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.pbService = pbService;
        this.objectMapper = objectMapper;
    }

    @Override
    public AccountInformation read() throws Exception {

        // Lazy load account IDs from DB
        if (accountIterator == null) {
            List<String> accountIds = jdbcTemplate.query(
                    "SELECT account_id FROM account",
                    (rs, _) -> rs.getString("account_id")
            );
            accountIterator = accountIds.iterator();
        }

        // If no more accounts, signal end of data
        if (!accountIterator.hasNext()) {
            return null;
        }

        // Get next account ID
        String accountId = accountIterator.next();

        // Call API for this account
        JsonNode root = pbService.getAccountInformation(accountId); // returns JsonNode
        JsonNode dataNode = root.path("data"); // move tree to "data"

        // Map JSON → AccountInformation
        if (dataNode.isMissingNode() || dataNode.isNull()) {
            return null; // API returned nothing for this account
        }

        return objectMapper.convertValue(dataNode,new TypeReference<AccountInformation>(){});
    }
}
