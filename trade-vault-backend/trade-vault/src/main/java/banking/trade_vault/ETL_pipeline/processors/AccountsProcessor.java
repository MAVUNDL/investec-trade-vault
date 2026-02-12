package banking.trade_vault.ETL_pipeline.processors;

import banking.trade_vault.ETL_pipeline.entities.AccountEntity;
import banking.trade_vault.ETL_pipeline.investec.api.sections.pb.models.Account;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class AccountsProcessor implements ItemProcessor<Account, AccountEntity> {

    @Override
    public @Nullable AccountEntity process(Account account) throws Exception {
        return new AccountEntity(
                account.accountId(),
                account.accountNumber(),
                account.accountName(),
                account.referenceName(),
                account.productName(),
                account.kycCompliant(),
                account.profileId(),
                LocalDateTime.now()
        );
    }
}
