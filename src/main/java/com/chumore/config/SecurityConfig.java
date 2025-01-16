package com.chumore.config;
import com.chumore.emp.model.EmpUserDetailsService;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(employeeAuthenticationProvider())
                .authenticationProvider(memberAuthenticationProvider())
                .authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/images/**", "/", "/emp/login", "/auth/login").permitAll()
                .anyRequest().permitAll()
                .and()
                
                // 員工登入配置
                .formLogin()
                .loginPage("/emp/login")           // 改為 /emp/login
                .loginProcessingUrl("/emp/login")  
                .defaultSuccessUrl("/emp/profile", true)  // 添加默認成功頁面
                .successHandler((request, response, authentication) -> {
                    Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
                    if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                        response.sendRedirect("/emp/admin/list");
                    } else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
                        response.sendRedirect("/emp/profile");
                    }
                })
                .failureUrl("/emp/login?error")    // 添加失敗URL
                .permitAll()
                .and()
                
                // 會員登入配置
                .formLogin()
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .successHandler((request, response, authentication) -> {
                    String role = authentication.getAuthorities().iterator().next().getAuthority();
                    if ("ROLE_MEMBER".equals(role)) {
                        response.sendRedirect("/secure/member/member_information");
                    } else if ("ROLE_RESTAURANT".equals(role)) {
                        response.sendRedirect("/secure/rest/rest_information");
                    }
                })
                .failureUrl("/auth/login?error")   // 添加失敗URL
                .permitAll()
                .and()
                
                // 登出配置
                .logout()
                .logoutSuccessUrl("/emp/login")
                .permitAll()
                .and()
                .csrf().disable();

        return http.build();
    }

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
}