package com.chumore.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
public class HibernateConfig {

    @Autowired
    private DataSource dataSource;


    @Bean
    public LocalSessionFactoryBean sessionFactory() throws IOException {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        sessionFactory.setConfigLocations(new ClassPathResource("hibernate.cfg.xml"));

        // 掃描 entity
        sessionFactory.setPackagesToScan("com.chumore");

        return sessionFactory;
    }

    // 配置 TransactionManager
    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }

}
