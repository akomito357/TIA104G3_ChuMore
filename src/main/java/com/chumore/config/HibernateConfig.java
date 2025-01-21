package com.chumore.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@ComponentScan(basePackages={"com"})
@EnableTransactionManagement
public class HibernateConfig {

    private final Environment env;

    public HibernateConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public DataSource dataSource() throws NamingException {
        boolean useJndi = Boolean.parseBoolean(env.getProperty("app.datasource.jndi-enabled","false"));
        if(useJndi){
            // 使用 JNDI 連線
            JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
            jndi.setJndiName(env.getProperty("spring.datasource.jndi-name"));
            jndi.setProxyInterface(DataSource.class);
            jndi.afterPropertiesSet();
            System.out.println("using JNDI DataSource");
            return (DataSource) jndi.getObject();
        } else {
            // 使用 HikariCP 連線池
            HikariConfig config = new HikariConfig();
            config.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
            config.setJdbcUrl(env.getProperty("spring.datasource.url"));
            config.setUsername(env.getProperty("spring.datasource.username"));
            config.setPassword(env.getProperty("spring.datasource.password"));

            // 設定 HikariCP 相關參數
            config.setMinimumIdle(Integer.parseInt(env.getProperty("spring.datasource.hikari.minimum-idle", "5")));
            config.setMaximumPoolSize(Integer.parseInt(env.getProperty("spring.datasource.hikari.maximum-pool-size", "10")));
            config.setIdleTimeout(Long.parseLong(env.getProperty("spring.datasource.hikari.idle-timeout", "30000")));
            config.setConnectionTimeout(Long.parseLong(env.getProperty("spring.datasource.hikari.connection-timeout", "30000")));
            config.setPoolName(env.getProperty("spring.datasource.hikari.pool-name", "MyHikariCP"));

            HikariDataSource hikariDataSource = new HikariDataSource(config);

            System.out.println("using HikariCP datasource");
            return hikariDataSource;
        }
    }



    // Spring Data JPA 的 EntityManagerFactory 設定
    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() throws NamingException {
        LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(dataSource());
        emf.setPackagesToScan("com.chumore"); // 掃描 JPA 的 Entity

        // 明確指定 Persistence Provider (Hibernate)
        emf.setJpaVendorAdapter(new org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter());

        // 加入 Hibernate 相關屬性
        emf.setJpaProperties(hibernateProperties());
        return emf;
    }


    @Bean(name="transactionManager")
    @Primary
    public JpaTransactionManager jpaTransactionManager() throws NamingException {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        return transactionManager;
    }

    private Properties hibernateProperties() throws NamingException {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        properties.put("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect.use_legacy_tinyint_type",
                env.getProperty("hibernate.dialect.use_legacy_tinyint_type", "false"));
        properties.put("hibernate.current_session_context_class", env.getProperty("hibernate.current_session_context_class"));

        return properties;
    }


}