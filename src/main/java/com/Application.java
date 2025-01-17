package com;

import com.chumore.search.model.LuceneIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import java.io.IOException;

@SpringBootApplication(scanBasePackages = "com.chumore")
public class Application {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.addListeners((ApplicationListener<ApplicationEnvironmentPreparedEvent>) event -> {
            ConfigurableEnvironment environment = event.getEnvironment();
            try{
                // 載入 application-secret.properties
                Resource resource = new  ClassPathResource("application-secret.properties");
                if(resource.exists()) {
                    PropertySource<?> propertySource = new PropertiesPropertySourceLoader()
                            .load("application-secret.properties", new ClassPathResource("application-secret.properties"))
                            .get(0);
                    // 將 application-secret.properties 加入property source中，覆蓋原有設定
                    environment.getPropertySources().addFirst(propertySource);
                    System.out.println("application-secret.properties loaded");
                }else{
                    System.out.println("application-secret.properties not found,skipping");
                }
            }catch(IOException e){
                System.err.println("Failed to load application-secret.properties:"+e.getMessage());
            }
        });
        app.run(args);
    }

    // 應用程式啟動後，重建索引
    @Autowired
    public void initialize(LuceneIndexService luceneIndexService) {
        try{
            luceneIndexService.rebuildIndex();
            System.out.println("Lucene索引建立成功");
        }catch(Exception e){
            e.printStackTrace();
            System.err.println("Lucene索引建立失敗:"+e.getMessage());
        }
    }
}