package org.opennuri.study.architecture.settlement.adapter.in.batch;

import lombok.RequiredArgsConstructor;
import org.opennuri.study.architecture.settlement.port.in.RunSettlementUseCase;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class SettlementBatchConfig {

    private final RunSettlementUseCase runSettlementUseCase;

    @Bean
    public Job settlementJob(JobRepository jobRepository, Step settlementStep) {
        return new JobBuilder("settlementJob", jobRepository)
                .start(settlementStep)
                .build();
    }

    @Bean
    public Step settlementStep(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("settlementStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    JobParameters params = contribution.getStepExecution().getJobParameters();
                    String runId = params != null && params.getParameters().containsKey("runId")
                            ? params.getString("runId") : null;
                    runSettlementUseCase.runOnce(runId);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
