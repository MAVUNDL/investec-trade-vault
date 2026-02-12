package banking.trade_vault.ETL_pipeline.processors;

import banking.trade_vault.ETL_pipeline.entities.AccountInformationEntity;
import banking.trade_vault.ETL_pipeline.investec.api.sections.pb.models.AccountInformation;
import org.jspecify.annotations.Nullable;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class AccountInformationProcessor implements ItemProcessor<AccountInformation, AccountInformationEntity> {
    @Override
    public @Nullable AccountInformationEntity process(AccountInformation information) throws Exception {

        return new AccountInformationEntity(
                information.accountId(),
                information.currentBalance(),
                information.availableBalance(),
                information.currency(),
                LocalDateTime.now()
        );

    }
}
