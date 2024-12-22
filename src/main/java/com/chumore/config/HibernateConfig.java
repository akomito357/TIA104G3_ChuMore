package com.chumore.config;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
public class HibernateConfig {

    @Autowired
    private DataSource dataSource;


    @Bean
    public LocalSessionFactoryBean sessionFactory() throws IOException {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // 載入 hibernate.cfg.xml 的屬性
        Properties hibernateProperties = new Properties();
        hibernateProperties.load(
                this.getClass().getResourceAsStream("/hibernate.cfg.xml")
        );
        sessionFactory.setHibernateProperties(hibernateProperties);

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
