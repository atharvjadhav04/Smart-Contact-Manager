package com.smart.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.Entities.User;
import com.smart.Repository.UserRepository;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repository;
    
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title","Home - Smart Contact Manager");
        return "home";
    }
    
    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("title","About - Smart Contact Manager");
        return "about";
    }
    
    
    
    
    @GetMapping("/signup")
    public String signup(Model model) {
        model.addAttribute("title","Register - Smart Contact Manager");
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/do_register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                              BindingResult bindingResult,
                              @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, 
                              Model model, 
                              HttpSession session,
                              RedirectAttributes attributes) {
        
        try {
            // Check agreement first (before validation)
            if (!agreement) {
                System.out.println("Agreement not accepted");
                attributes.addFlashAttribute("message", new Message("You must agree to terms and conditions!", "alert-danger"));
                return "redirect:/signup";
            }
            
            // If there are validation errors
            if (bindingResult.hasErrors()) {
                System.out.println("=== VALIDATION ERRORS ===");
                bindingResult.getAllErrors().forEach(error -> {
                    System.out.println("Error: " + error.toString());
                    System.out.println("Code: " + error.getCode());
                    System.out.println("Default Message: " + error.getDefaultMessage());
                });
                System.out.println("=== END ERRORS ===");
                
                model.addAttribute("title", "Register - Smart Contact Manager");
                return "signup"; 
            }
            
            // If no validation errors and agreement is accepted
            user.setRole("ROLE_USER");
            user.setEnabled(true);
            user.setImageUrl("default.png");
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            System.out.println("Saving user: " + user);
            User result = this.repository.save(user);
            
            // Clear the form and show success message
            attributes.addFlashAttribute("message", new Message("Successfully Registered!", "alert-success"));
            return "redirect:/signup"; 
            
        } catch (Exception e) {
            e.printStackTrace();
            
            // Handle specific duplicate email exception
            if (e.getMessage() != null && e.getMessage().contains("Duplicate entry") && e.getMessage().contains("email")) {
                attributes.addFlashAttribute("message", new Message("Email address already registered!", "alert-danger"));
            } else {
                attributes.addFlashAttribute("message", new Message("Something went wrong: " + e.getMessage(), "alert-danger"));
            }
            
            return "redirect:/signup";
        }
    }
    
    
    //handler for custom login 
    @GetMapping("/login")
    public String customLogin(Model model) {
    	model.addAttribute("title","Login Page");
    	return "login";
    }
}


















