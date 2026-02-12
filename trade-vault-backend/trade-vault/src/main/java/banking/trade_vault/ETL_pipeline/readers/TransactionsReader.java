package banking.trade_vault.ETL_pipeline.readers;

import banking.trade_vault.ETL_pipeline.investec.api.sections.pb.models.Transaction;
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
public class TransactionsReader implements ItemReader<Transaction> {

    private final JdbcTemplate jdbcTemplate;
    private final PrivateBankingInformationService pbService;
    private final ObjectMapper objectMapper;

    private Iterator<String> accountIterator;          // Iterates over account IDs
    private Iterator<Transaction> transactionIterator; // Iterates over transactions for current account

    public TransactionsReader(JdbcTemplate jdbcTemplate, PrivateBankingInformationService pbService, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.pbService = pbService;
        this.objectMapper = objectMapper;
    }

    @Override
    public Transaction read() throws Exception {

        // If we don't yet have account IDs loaded, fetch them from DB
        if (accountIterator == null) {
            List<String> accountIds = jdbcTemplate.query(
                    "SELECT account_id FROM account",
                    (rs, _) -> rs.getString("account_id")
            );
            accountIterator = accountIds.iterator();
        }

        // If no transactions are ready for current account, fetch next account's transactions
        while (transactionIterator == null || !transactionIterator.hasNext()) {

            if (!accountIterator.hasNext()) {
                return null; // No more accounts → end of data
            }

            String accountId = accountIterator.next();

            // Call API to get transactions for this account
            JsonNode root = pbService.getTransactions(accountId); // returns JsonNode

            JsonNode transactionsNode = root.path("data").path("transactions");

            if (transactionsNode.isMissingNode() || !transactionsNode.isArray()) {
                transactionIterator = List.<Transaction>of().iterator(); // empty
            } else {
                List<Transaction> transactions = objectMapper.convertValue(
                        transactionsNode,
                        new TypeReference<List<Transaction>>() {}
                );
                transactionIterator = transactions.iterator();
            }
        }

        // Return next transaction
        return transactionIterator.next();
    }
}
