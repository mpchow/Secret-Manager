package com.example.secretmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

@SpringBootApplication
public class SecretManagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecretManagerApplication.class, args);
	}

	@Bean
	public PasswordEncoder encoder() {
		return new BCryptPasswordEncoder();
	}

	@Configuration
	@EnableWebSecurity
	public class SecurityConfig extends WebSecurityConfigurerAdapter {

		@Override
		protected void configure(HttpSecurity security) throws Exception
		{
			// Disables app wide security
			security.httpBasic().disable();

			// Allows requests to all endpoints
			security.csrf().disable().authorizeRequests()
				.antMatchers("/**").permitAll()
				.anyRequest().authenticated();

			// Allows usage of h2 web console
			security.headers().frameOptions().disable();
		}
	}
}
