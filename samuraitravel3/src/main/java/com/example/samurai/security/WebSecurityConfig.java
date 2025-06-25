package com.example.samurai.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception {
		
		http.authorizeHttpRequests((requests)->requests
				.requestMatchers("/css/**","/images/**","/js/**","/storage/**","/","/signup/**","/houses","/houses/{id}","/stripe/webhook").permitAll()
				.requestMatchers("/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated())
			
			.formLogin((form) -> form
					.loginPage("/login")//ログインページのURL
					.loginProcessingUrl("/login")//ログインフォームの送信先URL
					.defaultSuccessUrl("/?loggedIn")//ログイン成功時のURL
					.failureUrl("/login?error")//ログイン失敗時のURL
					.permitAll())
			
			.logout((logout) -> logout
					.logoutSuccessUrl("/?loggedOut")//ログアウト時のリダイレクト先URL
					.permitAll())
			
			//.csrf().ignoringREquesrMatchers("/stripe/webhook")
			;
		return http.build();
		
	}
	
	
	//パスワードのハッシュアルゴリズムを設定
	@Bean
	public PasswordEncoder paswordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
