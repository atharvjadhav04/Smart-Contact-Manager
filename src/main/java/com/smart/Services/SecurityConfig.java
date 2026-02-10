package com.smart.Services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration  // Marks this as configuration class
@EnableWebSecurity  // Enables Spring Security web security support
public class SecurityConfig {
    
    // BEAN 1: UserDetailsService
    @Bean  // Creates a Spring bean managed by container
    public UserDetailsService getUserDetailsService() {
        return new UserDetailsServiceImpl();  // Returns our custom implementation
    }
    
    // BEAN 2: PasswordEncoder (BCrypt for password hashing)
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Uses BCrypt hashing algorithm
    }
    
    // BEAN 3: DaoAuthenticationProvider (Connects UserDetailsService + PasswordEncoder)
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(getUserDetailsService());  // Set user source
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());  // Set password checker
        return daoAuthenticationProvider;
    }
    
    // BEAN 4: SecurityFilterChain (Main security configuration)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        http.csrf(csrf -> csrf.disable())  // Disable CSRF for simplicity (not for production!)
            .authorizeHttpRequests(auth -> auth
                // URL-based authorization rules
                .requestMatchers("/admin/**").hasRole("ADMIN")  // Only ADMIN can access /admin
                .requestMatchers("/user/**").hasRole("USER")    // Only USER can access /user
                .requestMatchers("/**").permitAll()  // Everyone can access root URLs
            )
            .formLogin(form -> form
                .loginPage("/login")  // Custom login page URL
                .loginProcessingUrl("/login")  // POST endpoint for login
                .defaultSuccessUrl("/user/index")  // Where to go after successful login
                .failureUrl("/login?error=true")  // Where to go after failed login
                .permitAll()  // Allow everyone to access login page
            )
            .logout(logout -> logout
                .logoutUrl("/logout")  // POST endpoint for logout
                .logoutSuccessUrl("/login?logout=true")  // Where to go after logout
                .permitAll()  // Allow everyone to logout
            )
            .authenticationProvider(authenticationProvider());  // Use our authentication provider
        
        return http.build();  // Build and return the security configuration
    }
}