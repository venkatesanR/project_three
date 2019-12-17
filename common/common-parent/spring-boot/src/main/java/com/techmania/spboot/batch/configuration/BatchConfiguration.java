package com.techmania.spboot.batch.configuration;

import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@EnableConfigurationProperties({SpringBatchProperties.class})
public class BatchConfiguration {
    @Autowired
    public SpringBatchProperties properties;

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .driverClassName("com.mysql.jdbc.Driver")
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .build();
    }

    @Bean
    public BatchConfigurer batchConfigurer(@Autowired DataSource dataSource) {
        return new DefaultBatchConfigurer() {

            private PlatformTransactionManager txManager = new DataSourceTransactionManager(dataSource);

            @Override
            protected JobRepository createJobRepository() throws Exception {
                JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
                factory.setDataSource(dataSource);
                factory.setTransactionManager(txManager);
                factory.setIsolationLevelForCreate("ISOLATION_SERIALIZABLE");
                factory.setTablePrefix("BATCH_");
                factory.setMaxVarCharLength(1000);
                return factory.getObject();
            }

            @Override
            protected JobLauncher createJobLauncher() throws Exception {
                SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
                jobLauncher.setJobRepository(createJobRepository());
                jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
                jobLauncher.afterPropertiesSet();
                return jobLauncher;
            }

            @Override
            public PlatformTransactionManager getTransactionManager() {
                return txManager;
            }
        };
    }
}
