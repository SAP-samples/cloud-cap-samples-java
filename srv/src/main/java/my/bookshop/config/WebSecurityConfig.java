package my.bookshop.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@ConditionalOnWebApplication
@EnableWebSecurity
@Order(1)
public class WebSecurityConfig {

	@Bean
	public SecurityFilterChain configure(HttpSecurity http) throws Exception {
		return http.requestMatchers() //
				.antMatchers("/actuator/health") //
				.antMatchers("/swagger/**") //
				.and().csrf().disable().authorizeRequests().anyRequest().permitAll().and()
				.build();
	}
}
