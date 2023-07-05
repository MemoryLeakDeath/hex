package tv.memoryleakdeath.hex.test.config;

import javax.sql.DataSource;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import tv.memoryleakdeath.hex.config.HexDBConfig;

@Configuration
public class MockDBConfig extends HexDBConfig {

    @Override
    public DataSource getDataSource() {
        return null;
    }

    @Bean
    public JdbcTemplate getJdbcTemplate() {
        return Mockito.mock(JdbcTemplate.class);
    }

    @Bean
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return Mockito.mock(NamedParameterJdbcTemplate.class);
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        return Mockito.mock(DataSourceTransactionManager.class);
    }

}
