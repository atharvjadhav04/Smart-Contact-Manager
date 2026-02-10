package com.smart.Services;

import java.util.Collection;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.smart.Entities.User;

//only for DATA FORMATTING in spring security understandable language
public class CustomUserDetails implements UserDetails{
    
    private User user;  // Holds actual User entity from database
    
    // Constructor: Wraps a User entity
    public CustomUserDetails(User user) {
        super();
        this.user = user;
    }

    // 1. GET AUTHORITIES (ROLES & PERMISSIONS)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        
        // Converts user's role (like "ROLE_ADMIN") to Spring Security authority
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());
        
        // Returns list containing single authority (can have multiple)
        return List.of(authority);
    }

    // 2. GET PASSWORD (Encrypted from database)
    @Override
    public String getPassword() {
        return user.getPassword();  // Returns BCrypt encoded password
    }

    // 3. GET USERNAME (In Spring Security, username = email in this case)
    @Override
    public String getUsername() {
        return user.getEmail();  // Uses email as username
    }
    
    // 4. ACCOUNT STATUS CHECKS (All return true by default)
    @Override
    public boolean isAccountNonExpired() {
        return true;  // Could check if account hasn't expired
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;  // Could check if account isn't locked
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;  // Could check if password hasn't expired
    }

    // 5. ACCOUNT ENABLED STATUS
    @Override
    public boolean isEnabled() {
        return user.isEnabled();  // Checks if user account is active
    }
}