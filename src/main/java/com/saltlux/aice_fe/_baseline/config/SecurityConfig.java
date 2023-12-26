package com.saltlux.aice_fe._baseline.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web.ignoring()
//                .antMatchers( "/v2/api-docs", "/swagger-resources/**"
//                        , "/swagger-ui.html", "/webjars/**", "/swagger/**"
//                        // -- Swagger UI v3 (Open API)
//                        , "/v3/api-docs/**", "/swagger-ui/**","/api/docs/**");
//    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

	    http.csrf().disable();
//        http.csrf().ignoringAntMatchers("/swagger-ui/**","/api-docs/**");

	    /*
	    http.csrf()
		        .ignoringAntMatchers("/test/**")
			    .ignoringAntMatchers("/dummy/**")
		        .ignoringAntMatchers("/auth/**")
		        .ignoringAntMatchers("/member/**")
			    .ignoringAntMatchers("/terms/**");
	     */
        http.cors().disable();
    }
}
