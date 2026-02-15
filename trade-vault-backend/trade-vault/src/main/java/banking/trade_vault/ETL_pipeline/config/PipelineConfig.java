package banking.trade_vault.ETL_pipeline.config;

import banking.trade_vault.ETL_pipeline.entities.*;
import banking.trade_vault.ETL_pipeline.investec.api.sections.cib.models.Shipment;
import banking.trade_vault.ETL_pipeline.investec.api.sections.pb.models.Account;
import banking.trade_vault.ETL_pipeline.investec.api.sections.pb.models.AccountInformation;
import banking.trade_vault.ETL_pipeline.investec.api.sections.pb.models.Beneficiary;
import banking.trade_vault.ETL_pipeline.investec.api.sections.pb.models.Transaction;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.parameters.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.infrastructure.item.ItemProcessor;
import org.springframework.batch.infrastructure.item.ItemReader;
import org.springframework.batch.infrastructure.item.database.JdbcBatchItemWriter;
import org.springframework.batch.infrastructure.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DuplicateKeyException;


import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class PipelineConfig {

    private final DataSource dataSource;

    public PipelineConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Bean
    public Step ingestAccountsStep(JobRepository jobRepository, ItemReader<Account> reader, ItemProcessor<Account, AccountEntity> processor) {
        return new StepBuilder("accounts-ingestion-step", jobRepository)
                .<Account, AccountEntity>chunk(100)
                .reader(reader)
                .processor(processor)
                .writer(JdbcBatchItemWriter("""
                        INSERT INTO account (
                            account_id,
                            account_number,
                            account_name,
                            reference_name,
                            product_name,
                            kyc_compliant,
                            profile_id,
                            ingested_at
                        )
                        VALUES (
                            :accountId,
                            :accountNumber,
                            :accountName,
                            :referenceName,
                            :productName,
                            :kycCompliant,
                            :profileId,
                            :ingestedAt
                        )
                         ON CONFLICT (account_id)
                         DO UPDATE SET
                             account_number = EXCLUDED.account_number,
                             account_name = EXCLUDED.account_name,
                             reference_name = EXCLUDED.reference_name,
                             product_name = EXCLUDED.product_name,
                             kyc_compliant = EXCLUDED.kyc_compliant,
                             profile_id = EXCLUDED.profile_id,
                             ingested_at = EXCLUDED.ingested_at;
                       """))
                .faultTolerant()
                .skip(DuplicateKeyException.class)
                .skipLimit(Integer.MAX_VALUE)
                .build();
    }

    @Bean
    public Step ingestAccountInfoStep(JobRepository jobRepository, ItemReader<AccountInformation> reader,  ItemProcessor<AccountInformation, AccountInformationEntity> processor) {
        return new StepBuilder("account-information-ingestion_step", jobRepository)
                .<AccountInformation, AccountInformationEntity>chunk(100)
                .reader(reader)
                .processor(processor)
                .writer(JdbcBatchItemWriter("""
                        INSERT INTO account_information (
                            account_id,
                            current_balance,
                            available_balance,
                            currency,
                            ingested_at
                        )
                        VALUES (
                            :accountId,
                            :currentBalance,
                            :availableBalance,
                            :currency,
                            :ingestedAt
                        )
                        ON CONFLICT (account_id)
                        DO UPDATE SET
                            current_balance = EXCLUDED.current_balance,
                            available_balance = EXCLUDED.available_balance,
                            currency = EXCLUDED.currency,
                            ingested_at = EXCLUDED.ingested_at;
                        """))
                .faultTolerant()
                .skip(DuplicateKeyException.class)
                .skipLimit(Integer.MAX_VALUE)
                .build();
    }

    @Bean
    public Step ingestBeneficiariesStep(JobRepository jobRepository, ItemReader<Beneficiary> reader, ItemProcessor<Beneficiary, BeneficiaryEntity> processor) {
        return new StepBuilder("beneficiaries-ingestion-step", jobRepository)
                .<Beneficiary, BeneficiaryEntity>chunk(100)
                .reader(reader)
                .processor(processor)
                .writer(JdbcBatchItemWriter("""
                        INSERT INTO beneficiary (
                            beneficiary_id,
                            account_number,
                            code,
                            bank,
                            beneficiary_name,
                            last_payment_amount,
                            last_payment_date,
                            cell_no,
                            email_address,
                            name,
                            reference_account_number,
                            reference_name,
                            category_id,
                            profile_id,
                            faster_payment_allowed,
                            ingested_at
                        )
                        VALUES (
                            :beneficiaryId,
                            :accountNumber,
                            :code,
                            :bank,
                            :beneficiaryName,
                            :lastPaymentAmount,
                            :lastPaymentDate,
                            :cellNo,
                            :emailAddress,
                            :name,
                            :referenceAccountNumber,
                            :referenceName,
                            :categoryId,
                            :profileId,
                            :fasterPaymentAllowed,
                            :ingestedAt
                        )
                        ON CONFLICT (beneficiary_id)
                        DO UPDATE SET
                            account_number = EXCLUDED.account_number,
                            code = EXCLUDED.code,
                            bank = EXCLUDED.bank,
                            beneficiary_name = EXCLUDED.beneficiary_name,
                            last_payment_amount = EXCLUDED.last_payment_amount,
                            last_payment_date = EXCLUDED.last_payment_date,
                            cell_no = EXCLUDED.cell_no,
                            email_address = EXCLUDED.email_address,
                            name = EXCLUDED.name,
                            reference_account_number = EXCLUDED.reference_account_number,
                            reference_name = EXCLUDED.reference_name,
                            category_id = EXCLUDED.category_id,
                            profile_id = EXCLUDED.profile_id,
                            faster_payment_allowed = EXCLUDED.faster_payment_allowed,
                            ingested_at = EXCLUDED.ingested_at;
                        """))
                .faultTolerant()
                .skip(DuplicateKeyException.class)
                .skipLimit(Integer.MAX_VALUE)
                .build();
    }

    @Bean
    public Step ingestTransactionsStep(JobRepository jobRepository, ItemReader<Transaction> reader, ItemProcessor<Transaction, TransactionEntity> processor) {
        return new StepBuilder("transactions-ingestion-step", jobRepository)
                .<Transaction, TransactionEntity>chunk(100)
                .reader(reader)
                .processor(processor)
                .writer(JdbcBatchItemWriter("""
                        INSERT INTO transactions (
                            account_id,
                            type,
                            transaction_type,
                            status,
                            description,
                            card_number,
                            posting_date,
                            value_date,
                            action_date,
                            transaction_date,
                            amount,
                            running_balance,
                            uuid,
                            ingested_at
                        )
                        VALUES (
                            :accountId,
                            :type,
                            :transactionType,
                            :status,
                            :description,
                            :cardNumber,
                            :postingDate,
                            :valueDate,
                            :actionDate,
                            :transactionDate,
                            :amount,
                            :runningBalance,
                            :uuid,
                            :ingestedAt
                        )
                        """))
                .faultTolerant()
                .skip(DuplicateKeyException.class)
                .skipLimit(Integer.MAX_VALUE)
                .build();
    }

    @Bean
    public Step ingestShipmentsStep(JobRepository jobRepository, ItemReader<Shipment> reader, ItemProcessor<Shipment, ShipmentEntity> processor) {
        return new StepBuilder("shipment-ingestion-step", jobRepository)
                .<Shipment, ShipmentEntity>chunk(100)
                .reader(reader)
                .processor(processor)
                .writer(JdbcBatchItemWriter("""
                        INSERT INTO shipments (
                            shipment_number,
                            indent_number,
                            ifb_reference,
                            customer_name,
                            buyer_full_name,
                            supplier_name,
                            port_of_load,
                            port_of_discharge,
                            delivery_address,
                            ship_on_board,
                            eta,
                            delivery_date,
                            mv_start_date,
                            mv_end_date,
                            currency_code,
                            order_value,
                            shipped_value,
                            paid_amount,
                            unit_price,
                            estimated_landed_cost,
                            incoterm,
                            status,
                            movement_type,
                            shipment_mode,
                            vessel_name,
                            container_number,
                            container_count,
                            container_type,
                            load_type,
                            pallets,
                            cartons,
                            item_reference,
                            quantity,
                            description,
                            delivery_contact,
                            delivery_month,
                            delivery_year,
                            ingested_at
                        )
                        VALUES (
                            :shipment_number,
                            :indent_number,
                            :ifb_reference,
                            :customer_name,
                            :buyer_full_name,
                            :supplier_name,
                            :port_of_load,
                            :port_of_discharge,
                            :delivery_address,
                            :ship_on_board,
                            :eta,
                            :delivery_date,
                            :mv_start_date,
                            :mv_end_date,
                            :currency_code,
                            :order_value,
                            :shipped_value,
                            :paid_amount,
                            :unit_price,
                            :estimated_landed_cost,
                            :incoterm,
                            :status,
                            :movement_type,
                            :shipment_mode,
                            :vessel_name,
                            :container_number,
                            :container_count,
                            :container_type,
                            :load_type,
                            :pallets,
                            :cartons,
                            :item_reference,
                            :quantity,
                            :description,
                            :delivery_contact,
                            :delivery_month,
                            :delivery_year,
                            :ingested_at
                        )
                        """))
                .faultTolerant()
                .skip(DuplicateKeyException.class)
                .skipLimit(Integer.MAX_VALUE)
                .build();
    }


    private <T> JdbcBatchItemWriter<T> JdbcBatchItemWriter(String sql) {
        return new JdbcBatchItemWriterBuilder<T>()
                .dataSource(dataSource)
                .sql(sql)
                .beanMapped()
                .build();
    }

    @Bean
    public Job ingestionPipelineJob(JobRepository jobRepository, Step ingestAccountsStep, Step ingestAccountInfoStep, Step ingestTransactionsStep,Step ingestBeneficiariesStep, Step ingestShipmentsStep) {
        return new JobBuilder("ingestion-pipeline-job", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(ingestAccountsStep)
                .next(ingestAccountInfoStep)
                .next(ingestTransactionsStep)
                .next(ingestBeneficiariesStep)
                .next(ingestShipmentsStep)
                .build();
    }
}
