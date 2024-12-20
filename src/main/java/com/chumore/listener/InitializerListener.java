package com.chumore.listener;

import com.chumore.util.HibernateUtil;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

// 啟動與關閉應用程式時自動建立與關閉 SessionFactory
@WebListener
public class InitializerListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce){
        System.out.println("context started");
        HibernateUtil.getSessionFactory();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce){
        System.out.println("context ended");
        HibernateUtil.shutdown();
    }






}
