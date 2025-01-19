package com.chumore.config;

import com.chumore.emp.model.EmpUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private EmpUserDetailsService empUserDetailsService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public DaoAuthenticationProvider employeeAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(empUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public DaoAuthenticationProvider memberAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    @Order(1)
    public SecurityFilterChain employeeFilterChain(HttpSecurity http) throws Exception {
        http
            .antMatcher("/login")  // 明確指定此配置只處理/login相關請求
            .authenticationProvider(employeeAuthenticationProvider())
            .authorizeRequests()
            .antMatchers("/css/**", "/js/**", "/images/**", "/login").permitAll()
            .antMatchers("/emp/admin/**").hasRole("ADMIN")
            .antMatchers("/emp/**").hasAnyRole("USER", "ADMIN")
            .and()
            .formLogin()
            .loginPage("/login")
            .loginProcessingUrl("/login")  // 處理登入表單提交的URL
            .usernameParameter("username")  // 確保與表單中的input name一致
            .passwordParameter("password")  // 確保與表單中的input name一致
            .successHandler((request, response, authentication) -> {
                for (GrantedAuthority auth : authentication.getAuthorities()) {
                    if (auth.getAuthority().equals("ROLE_ADMIN")) {
                        response.sendRedirect("/emp/admin/list");
                        return;
                    }
                }
                response.sendRedirect("/emp/profile");
            })
            .failureUrl("/login?error")
            .permitAll()
            .and()
            .logout()
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout")
            .permitAll()
            .and()
            .csrf().disable();

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain memberRestaurantFilterChain(HttpSecurity http) throws Exception {
        http
            .antMatcher("/auth/**")  // 明確指定此配置只處理/auth/**相關請求
            .authenticationProvider(memberAuthenticationProvider())
            .authorizeRequests()
            .antMatchers("/css/**", "/js/**", "/images/**", "/auth/register/**", "/auth/login").permitAll()
            .antMatchers("/secure/member/**").hasRole("MEMBER")
            .antMatchers("/secure/rest/**").hasRole("RESTAURANT")
            .and()
            .formLogin()
            .loginPage("/auth/login")
            .loginProcessingUrl("/auth/login")  // 處理登入表單提交的URL
            .usernameParameter("username")  // 確保與表單中的input name一致
            .passwordParameter("password")  // 確保與表單中的input name一致
            .successHandler((request, response, authentication) -> {
                String role = authentication.getAuthorities().iterator().next().getAuthority();
                if ("ROLE_MEMBER".equals(role)) {
                    response.sendRedirect("/secure/member/member_information");
                } else if ("ROLE_RESTAURANT".equals(role)) {
                    response.sendRedirect("/secure/rest/rest_information");
                }
            })
            .failureUrl("/auth/login?error")
            .permitAll()
            .and()
            .logout()
            .logoutUrl("/auth/logout")
            .logoutSuccessUrl("/auth/login?logout")
            .permitAll()
            .and()
            .csrf().disable();

        return http.build();
    }

    // 修改默認的安全配置，允許訪問首頁和其他公開頁面
    @Bean
    @Order(3)
    public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
            // 允許訪問靜態資源
            .antMatchers("/css/**", "/js/**", "/images/**").permitAll()
            // 允許訪問首頁和其他公開頁面
            .antMatchers("/", "/index", "/about", "/contact", "/products/**", "/restaurants/**").permitAll()
            // 允許訪問註冊相關頁面
            .antMatchers("/register/**").permitAll()
            // API 端點也需要允許訪問
            .antMatchers("/api/**").permitAll()
            // 其他路徑需要認證
            .anyRequest().authenticated()
            .and()
            .csrf().disable();

        return http.build();
    }
}