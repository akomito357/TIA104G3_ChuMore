package com.chumore.filter;

import com.chumore.HibernateUtil;
import org.hibernate.SessionFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;


// 在進入頁面時，開啟一個session，並且在頁面結束時，關閉session
@WebFilter
public class OpenSessionInViewFilter implements Filter {

    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        SessionFactory factory = HibernateUtil.getSessionFactory();
        try{
            System.out.println("filter open transaction");
            factory.getCurrentSession().beginTransaction();
            chain.doFilter(req,res);
            factory.getCurrentSession().getTransaction().commit();
        }catch(Exception e){
            factory.getCurrentSession().getTransaction().rollback();
            e.printStackTrace();
            chain.doFilter(req,res);
        }
    }
}
