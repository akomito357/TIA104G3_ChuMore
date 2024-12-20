package com.chumore.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HibernateUtil {
    private static StandardServiceRegistry registry;
    private static final SessionFactory sessionFactory = createSessionFactory();

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        InputStream is = null;
        try {
            // 使用 ClassLoader 讀取資源檔案
            is = HibernateUtil.class.getClassLoader().getResourceAsStream("db.properties");
            if (is != null) {
                properties.load(is);
                System.out.println("db.properties loaded successfully");
            } else {
                System.out.println("db.properties not found, using default configuration");
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to load db.properties: " + e.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }

    private static SessionFactory createSessionFactory() {
        try {
            // 載入 db.properties
            Properties properties = loadProperties();

            // 建立 Hibernate 設定
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml");

            // 套用 db.properties 設定
            if (properties.containsKey("hibernate.connection.username") &&
                    properties.containsKey("hibernate.connection.password")) {
                if (properties.containsKey("hibernate.connection.url")) {
                    builder.applySetting("hibernate.connection.url", properties.getProperty("hibernate.connection.url"));
                }
                builder.applySetting("hibernate.connection.username", properties.getProperty("hibernate.connection.username"));
                builder.applySetting("hibernate.connection.password", properties.getProperty("hibernate.connection.password"));
            }

            // 建立 SessionFactory
            registry = builder.build();
            return new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();

        } catch (Exception e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void shutdown() {
        if (registry != null) {
            StandardServiceRegistryBuilder.destroy(registry);
            System.out.println("SessionFactory shutdown");
        }
    }
}
