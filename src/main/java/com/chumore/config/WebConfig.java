package com.chumore.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        // 使用 ISO 格式
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true); // 設置為 ISO 格式
        registrar.registerFormatters(registry);

        // 自定義 LocalDate 格式化器，格式為 yyyy-MM-dd
        registry.addFormatterForFieldType(LocalDate.class, new LocalDateFormatter());
    }

    // 自定義 LocalDate 格式化器
    private static class LocalDateFormatter implements org.springframework.format.Formatter<LocalDate> {

        @Override
        public LocalDate parse(String text, java.util.Locale locale) {
            return LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        @Override
        public String print(LocalDate object, java.util.Locale locale) {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(object);
        }
    }
}
