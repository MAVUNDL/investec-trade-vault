package banking.trade_vault.ETL_pipeline.processors;

import banking.trade_vault.ETL_pipeline.entities.TransactionEntity;
import banking.trade_vault.ETL_pipeline.investec.api.sections.pb.models.Transaction;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionsProcessor implements ItemProcessor<Transaction, TransactionEntity> {
    @Override
    public @Nullable TransactionEntity process(Transaction transaction) throws Exception {
        return new  TransactionEntity(
                transaction.accountId(),
                transaction.type(),
                transaction.transactionType(),
                transaction.status(),
                transaction.description(),
                transaction.cardNumber(),
                transaction.postingDate(),
                transaction.valueDate(),
                transaction.actionDate(),
                transaction.transactionDate(),
                transaction.amount(),
                transaction.runningBalance(),
                transaction.uuid(),
                LocalDateTime.now()
        );
    }
}
