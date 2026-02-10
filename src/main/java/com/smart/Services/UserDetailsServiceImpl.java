package com.smart.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.smart.Entities.User;
import com.smart.Repository.UserRepository;

@Service  // Marks this as a Spring service bean BUSINESS LOGIC
public class UserDetailsServiceImpl implements UserDetailsService{

    @Autowired  // Injects UserRepository for database access
    private UserRepository repository;
    
    // MAIN METHOD: Load user by username (email in this case)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        
        // Step 1: Fetch user from database using email as username
        User user = repository.getUserByUserName(username);
        
        // Step 2: If user not found, throw exception
        if(user == null) {
            throw new UsernameNotFoundException("could not found user !");
        }
        
        // Step 3: Wrap User entity in CustomUserDetails
     // Step 2: Wrap in Standard ID Card Format
        CustomUserDetails customUserDetails = new CustomUserDetails(user);
        
     // Returns: Standard Spring Security format for authentication
        return customUserDetails;
    }
}