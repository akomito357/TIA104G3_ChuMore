package com.chumore.config;

import com.chumore.emp.model.EmpUserDetailsService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

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
		http.antMatcher("/login") // 明確指定此配置只處理/login相關請求
				.authenticationProvider(employeeAuthenticationProvider()).authorizeRequests()
				.antMatchers("/css/**", "/js/**", "/images/**", "/login").permitAll().antMatchers("/emp/admin/**")
				.hasRole("ADMIN").antMatchers("/emp/**").hasAnyRole("USER", "ADMIN").and().formLogin()
				.loginPage("/login").loginProcessingUrl("/login") // 處理登入表單提交的URL
				.usernameParameter("username") // 確保與表單中的input name一致
				.passwordParameter("password") // 確保與表單中的input name一致
				.successHandler((request, response, authentication) -> {
					for (GrantedAuthority auth : authentication.getAuthorities()) {
						if (auth.getAuthority().equals("ROLE_ADMIN")) {
							response.sendRedirect("/emp/admin/list");
							return;
						}
					}
					response.sendRedirect("/emp/profile");
				}).failureUrl("/login").permitAll().and().logout().logoutUrl("/logout").logoutSuccessUrl("/login")
				.permitAll().and().csrf().disable();

		return http.build();
	}

	@Bean
	@Order(2)
	public SecurityFilterChain memberRestaurantFilterChain(HttpSecurity http) throws Exception {
		http.requestMatchers().antMatchers("/secure/**", "/auth/**", "/restaurant/**", "/").and()
				.authenticationProvider(memberAuthenticationProvider()).authorizeRequests()
				// 靜態資源
				.antMatchers("/css/**", "/js/**", "/images/**", "/lib/**", "/webjars/**").permitAll()
				// 公開頁面
				.antMatchers("/", "/restaurant/**", "/search/**", "/auth/register/**", "/auth/login").permitAll()
				// 受保護的路徑
				.antMatchers("/secure/member/**").hasRole("MEMBER").antMatchers("/secure/rest/**").hasRole("RESTAURANT").antMatchers("/rests/**").hasRole("RESTAURANT")
				.antMatchers("/auth/logout").authenticated().anyRequest().authenticated().and().formLogin()
				.loginPage("/auth/login").loginProcessingUrl("/auth/login").usernameParameter("username")
				.passwordParameter("password").successHandler((request, response, authentication) -> {
					SavedRequest savedRequest = new HttpSessionRequestCache().getRequest(request, response);
					if (savedRequest != null) {
						String targetUrl = savedRequest.getRedirectUrl();
						// 檢查目標URL是否符合用戶角色
						String role = authentication.getAuthorities().iterator().next().getAuthority();
						boolean isValidUrl = ("ROLE_MEMBER".equals(role) && targetUrl.contains("/secure/member"))
								|| ("ROLE_RESTAURANT".equals(role) && targetUrl.contains("/secure/rest"));

						if (isValidUrl) {
							response.sendRedirect(targetUrl);
							return;
						}
					}

					// 如果沒有合適的保存請求，則根據角色重定向
					String role = authentication.getAuthorities().iterator().next().getAuthority();
					if ("ROLE_MEMBER".equals(role)) {
						response.sendRedirect("/secure/member/member_information");
					} else if ("ROLE_RESTAURANT".equals(role)) {
						response.sendRedirect("/rests/rest_infomation_setting");
					}
				}).failureUrl("/auth/login").permitAll().and().logout().logoutUrl("/auth/logout")
				.logoutSuccessUrl("/auth/login").deleteCookies("JSESSIONID").clearAuthentication(true)
				.invalidateHttpSession(true).permitAll().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).invalidSessionUrl("/auth/login")
				.maximumSessions(1).maxSessionsPreventsLogin(true).expiredUrl("/auth/login").and().and()
				.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
					if (isAjaxRequest(request)) {
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					} else {
						response.sendRedirect("/auth/login");
					}
				}).accessDeniedHandler((request, response, accessDeniedException) -> {
					if (isAjaxRequest(request)) {
						response.setStatus(HttpServletResponse.SC_FORBIDDEN);
					} else {
						response.sendRedirect("/auth/login");
					}
				}).and().headers().xssProtection().and()
				.contentSecurityPolicy("script-src 'self' https://cdn.jsdelivr.net").and().and().csrf().disable();

		return http.build();
	}

	// 判斷是否為 AJAX 請求
	private boolean isAjaxRequest(HttpServletRequest request) {
		return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
	}

	@Bean
	@Order(3)
	public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/css/**", "/js/**", "/images/**").permitAll()
				.antMatchers(
						"/search",
						"/search/**",
						"/searchPage",
						"/searchPage/**",
						"/restaurant/search/**",
						"/api/search/**",
						"/lucene/**",
						"/dailyReservations/**",
						"/getRestaurantByKeyword/**"
				).permitAll()
				.antMatchers(
						"/",
						"/restaurants/**",
						"/location/**",
						"/orders/**",
						"/reservations/**",
						"/cuisineTypes/**",
						"/notification/**",
						"/envImg/**",
						"/reviews/**",
						"/ws/**",
						"/getRandomRest/**",
						"/dailyReservations/**"
				).permitAll()
				.antMatchers("/register/**").permitAll()
				.antMatchers("/api/**").permitAll()
				.antMatchers("/member/**").hasRole("MEMBER")
				.antMatchers("/rests/**").hasRole("RESTAURANT")
				.anyRequest().authenticated()
				.and()
				.headers()
				.xssProtection()
				.and()
				.contentSecurityPolicy(
						"default-src 'self' " +
								"https://cdnjs.cloudflare.com " +
								"https://cdn.jsdelivr.net " +
								"https://stackpath.bootstrapcdn.com " +
								"https://maxcdn.bootstrapcdn.com " +
								"https://maps.google.com; " +

								"script-src 'self' 'unsafe-inline' 'unsafe-eval' " +
								"https://cdnjs.cloudflare.com " +
								"https://cdn.jsdelivr.net " +
								"https://stackpath.bootstrapcdn.com " +
								"https://maxcdn.bootstrapcdn.com " +
								"https://maps.google.com; " +

								"style-src 'self' 'unsafe-inline' " +
								"https://cdnjs.cloudflare.com " +
								"https://cdn.jsdelivr.net " +
								"https://stackpath.bootstrapcdn.com " +
								"https://maxcdn.bootstrapcdn.com; " +

								"img-src 'self' data: https: " +
								"https://maps.google.com " +
								"https://*.google.com; " +

								"frame-src 'self' " +
								"https://maps.google.com " +
								"https://www.google.com; " +

								"connect-src 'self' " +
								"https://maps.google.com " +
								"tel:*; " +

								"form-action 'self';"
				)
				.and()
				.and()
				.csrf().disable();

		return http.build();
	}
}