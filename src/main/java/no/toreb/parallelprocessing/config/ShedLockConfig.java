package no.toreb.parallelprocessing.config;

import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.provider.jdbctemplate.JdbcTemplateLockProvider;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.JdbcTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableSchedulerLock(defaultLockAtMostFor = "PT5M")
public class ShedLockConfig {

    @Bean
    LockProvider lockProvider(final DataSource dataSource, final JdbcTransactionManager transactionManager) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return new JdbcTemplateLockProvider(
                JdbcTemplateLockProvider.Configuration.builder()
                                                      .withJdbcTemplate(jdbcTemplate)
                                                      .withTransactionManager(transactionManager)
                                                      .build());
    }
}
