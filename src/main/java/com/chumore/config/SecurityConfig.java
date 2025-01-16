package com.chumore.config;
import com.chumore.emp.model.EmpUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// 啟用方法層級的安全性註解，如 @PreAuthorize
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
// 配置 HttpSecurity 的類
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
// 啟用 Spring Security 的核心功能
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
// 提供 BCrypt 密碼編碼器
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// 提供 NoOp 密碼編碼器（無編碼器，用於測試）
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
// 密碼編碼器的接口
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
// 配置 Security Filter Chain 的接口
import org.springframework.security.web.SecurityFilterChain;

// 標記為配置類
@Configuration
// 啟用 Web 安全性配置
@EnableWebSecurity
// 啟用全局方法安全性，支持 @PreAuthorize、@PostAuthorize 註解
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

	// 自動注入自定義的用戶細節服務類
	@Autowired
	private EmpUserDetailsService empUserDetailsService;

	// 定義 SecurityFilterChain Bean
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
				// 設定認證提供者
				.authenticationProvider(authenticationProvider()).authorizeRequests()
				// 開放不需要認證的頁面
				//.antMatchers("/", "/index", "/public","/orders").permitAll()
				// 靜態資源不需要認證
				//.antMatchers("/css/**", "/js/**", "/images/**").permitAll()
				// 開放 Actuator 的所有訪問
				//.antMatchers("/actuator/**").permitAll()
				// 只有 ADMIN 角色可以訪問的路徑
				//.antMatchers("/emp/admin/**").hasRole("ADMIN")
				// 只有 USER 角色可以訪問的路徑
				//.antMatchers("/emp/**").hasRole("USER")
				// 其他請求需要認證
				//.anyRequest().authenticated().and().formLogin()
				//開發階段暫時先公開所有訪問權限，待最後階段再配置
				.anyRequest().permitAll()
				.and()
				.formLogin()
				// 自定義登錄頁面
				.loginPage("/login")
				// 登錄表單提交的 URL
				.loginProcessingUrl("/login")
				// 登錄成功後的默認跳轉頁面，目前設定登入後跳轉回首頁，此處可能需要微調!
				.defaultSuccessUrl("/", true)
				// 登錄失敗時的跳轉頁面
				.failureUrl("/login?error")
				// 開放登錄相關請求
				.permitAll()
				.and()
				.logout()
				// 自定義登出 URL
				.logoutUrl("/logout")
				// 登出成功後的跳轉頁面
				.logoutSuccessUrl("/login?logout")
				// 開放登出相關請求
				.permitAll().and()
				// 開發階段先關閉 CSRF 保護，讓API可用
				.csrf().disable();

		// 返回構建的 SecurityFilterChain
		return http.build();
	}

	// 定義 PasswordEncoder Bean
	@Bean
	public PasswordEncoder passwordEncoder() {
		// 使用無編碼器（僅用於測試，生產環境請改用 BCryptPasswordEncoder）
		return NoOpPasswordEncoder.getInstance();
	}

	// 定義 DaoAuthenticationProvider Bean
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		// 創建 DaoAuthenticationProvider 對象
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		// 設定自定義用戶細節服務
		authProvider.setUserDetailsService(empUserDetailsService);
		// 設定密碼編碼器
		authProvider.setPasswordEncoder(passwordEncoder());
		// 返回配置完成的認證提供者
		return authProvider;
	}
}
