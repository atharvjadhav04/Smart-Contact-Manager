package com.smart.Repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.Entities.Contact;
import com.smart.Entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
    
    @Query("SELECT c FROM Contact c WHERE c.user.id = :userId")
    Page<Contact> findContactByUser(@Param("userId") int userId, Pageable pageable);
    
    // OR if you want to keep PageRequest specifically:
    // Page<Contact> findContactByUser(@Param("userId") int userId, PageRequest pageable);
    
    //search
    public List<Contact> findByNameContainingAndUser(String name,User user);
}

