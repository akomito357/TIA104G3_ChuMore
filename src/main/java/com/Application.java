package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		app.addListeners((ApplicationListener<ApplicationEnvironmentPreparedEvent>) event -> {
			ConfigurableEnvironment environment = event.getEnvironment();
			try{
				// 載入 application-secret.properties
				PropertySource<?> propertySource = new PropertiesPropertySourceLoader()
						.load("application-secret.properties",new ClassPathResource("application-secret.properties"))
						.get(0);
				// 將 application-secret.properties 加入property source中，覆蓋原有設定
				environment.getPropertySources().addFirst(propertySource);
				System.out.println("application-secret.properties loaded");
			}catch(IOException e){
				e.printStackTrace();
				throw new RuntimeException("Failed to load application-secret.properties", e);
			}
		});
		app.run(args);

	}

}
