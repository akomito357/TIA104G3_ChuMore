package com.chumore.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {

    private final Environment env;

    public HibernateConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public DataSource dataSource() throws NamingException{
        boolean useJndi = Boolean.parseBoolean(env.getProperty("app.datasource.jndi-enabled","false"));
        if(useJndi){
            //使用連線池連線
            JndiObjectFactoryBean jndi = new JndiObjectFactoryBean();
            jndi.setJndiName(env.getProperty("spring.datasource.jndi-name"));
            jndi.setProxyInterface(DataSource.class);
            jndi.afterPropertiesSet();
            System.out.println("using connection pool");
            return (DataSource) jndi.getObject();
        }else{
            // 使用內建driver連線
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
            dataSource.setUrl(env.getProperty("spring.datasource.url"));
            dataSource.setUsername(env.getProperty("spring.datasource.username"));
            dataSource.setPassword(env.getProperty("spring.datasource.password"));
            System.out.println("using default datasource");
            return dataSource;
        }

    }


    @Bean
    public LocalSessionFactoryBean sessionFactory() throws IOException, NamingException {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource());
        sessionFactory.setPackagesToScan("com.chumore");  // 掃描 entity
        sessionFactory.setHibernateProperties(hibernateProperties()); // 設定 hibernate properties

        return sessionFactory;
    }


    // 配置 TransactionManager
    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

    private Properties hibernateProperties() throws NamingException {
        Properties properties = new Properties();
        properties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
        properties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
        properties.put("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
        properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
        properties.put("hibernate.dialect.use_legacy_tinyint_type",
                env.getProperty("hibernate.dialect.use_legacy_tinyint_type", "false"));

        return properties;
    }


}
