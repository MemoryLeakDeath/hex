package tv.memoryleakdeath.hex.test.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@PropertySource("classpath:hex-test.properties")
public class TestDBConfig {
    @Autowired
    private Environment env;

    @Bean
    public DataSource getDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setDataSourceClassName("org.postgresql.ds.PGSimpleDataSource");
        ds.setUsername(getDatabaseUsername());
        ds.setPassword(getDatabasePassword());
        ds.addDataSourceProperty("applicationName", "hex");
        ds.addDataSourceProperty("databaseName", getDatabaseName());
        ds.addDataSourceProperty("serverNames", new String[] { getDatabaseServer() });
        ds.addDataSourceProperty("portNumbers", new int[] { getDatabasePort() });
        return ds;
    }

    @Bean
    public JdbcTemplate getJdbcTemplate() {
        return new JdbcTemplate(getDataSource());
    }

    @Bean
    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return new NamedParameterJdbcTemplate(getDataSource());
    }

    private String getDatabaseUsername() {
        return env.getProperty("databaseUsername", "");
    }

    private String getDatabasePassword() {
        return env.getProperty("databasePassword", "");
    }

    @Bean
    public String databaseUrl() {
        return env.getProperty("databaseUrl", "");
    }

    private String getDatabaseName() {
        return env.getProperty("databaseName", "");
    }

    private String getDatabaseServer() {
        return env.getProperty("databaseServer", "");
    }

    private int getDatabasePort() {
        return Integer.parseInt(env.getProperty("databasePort", "0"));
    }

}
