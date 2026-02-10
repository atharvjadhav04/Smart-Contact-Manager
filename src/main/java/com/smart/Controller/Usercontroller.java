package com.smart.Controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.KeyStore.Entry.Attribute;
import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.smart.Entities.Contact;
import com.smart.Entities.User;
import com.smart.Repository.ContactRepository;
import com.smart.Repository.UserRepository;
import com.smart.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
@RequestMapping("/user")
public class Usercontroller {
	
	@Autowired
	private BCryptPasswordEncoder encoder;

    @Autowired
    private UserRepository repository;
    
    @Autowired
    private ContactRepository contactRepository;
    
    // Method for adding common data to response
    @ModelAttribute
    public void addCommonData(Model m, Principal p) {
        String userName = p.getName();
        User user = repository.getUserByUserName(userName);
        m.addAttribute("user", user);
    }
    
    // Dashboard home
    @RequestMapping("/index")
    public String dashBoard(Model model, Principal principal) {
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }
    
    // Open add form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());
        return "normal/add_contact_form";
    }
    
    // Processing add contact form
    @PostMapping("/process-contact")
    public String processContact(@ModelAttribute Contact contact,
            @RequestParam("profileimage") MultipartFile file,
            Principal principal, RedirectAttributes attributes) {
        
        try {
            String name = principal.getName();
            User user = this.repository.getUserByUserName(name);
            
            // Processing image file
            if (file.isEmpty()) {
                System.out.println("file is empty !!!");
                contact.setImage("contact.png");
            } else {
                contact.setImage(file.getOriginalFilename());
                File savefile = new ClassPathResource("static/img").getFile();
                Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("Image is uploaded...");
            }
            
            user.getContacts().add(contact);
            contact.setUser(user);
            this.repository.save(user);
            
            System.out.println("DATA" + contact);
            System.out.println("added contact to database...");
            
            // Success message
            attributes.addFlashAttribute("message",new Message("Your contact is added successfully! Add more", "success"));
           
            
        } catch (Exception e) {
            e.printStackTrace();
            // Error message
            attributes.addFlashAttribute("message",new Message("Something went wrong! Try again.", "danger"));
            
            
        }
        //return "normal/add_contact_form";
        return "redirect:/user/add-contact";

    }
    
    // Default handler for /show-contacts (without page number) - ADD THIS METHOD
    @GetMapping("/show-contacts")
    public String showContactsDefault(Principal principal) {
        // Redirect to page 0
        return "redirect:/user/show-contacts/0";
    }
    
    // Show contacts with pagination
    @GetMapping("/show-contacts/{page}") 
    public String showContacts(@PathVariable("page") Integer page, Model m, Principal principal) {
        m.addAttribute("title", "Show User Contacts");
        
        String username = principal.getName();
        User user = this.repository.getUserByUserName(username);
        
        PageRequest pageable = PageRequest.of(page, 5);
        Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), pageable);
        
        m.addAttribute("contacts", contacts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", contacts.getTotalPages());
        
        return "normal/show_contacts";
    }
    
    // Showing specific contact details
    @GetMapping("/{cId}/contact")
    public String showContactDetails(@PathVariable("cId") Integer cId, Model model,Principal principal) {
        
        Optional<Contact> contactOptional = this.contactRepository.findById(cId);
        
        if (contactOptional.isPresent()) {
            Contact contact = contactOptional.get();
            
           
            String Username = principal.getName();
            User user = this.repository.getUserByUserName(Username);
            
            if(user.getId() == contact.getUser().getId()) {
            	model.addAttribute("contact", contact);
            	model.addAttribute("title",contact.getName());
            }
            return "normal/contact_detail";
        } else {
            // Contact not found
            return "redirect:/user/show-contacts";
        }
    }
    
    //delete contact 
    
    @GetMapping("/delete/{cid}")
    @Transactional
    public String deleteContact(@PathVariable("cid")Integer cId,Model model,Principal principal,RedirectAttributes redirectAttributes) {
    	Contact contact = this.contactRepository.findById(cId).get();
    	
    	
    	String Username = principal.getName();
    	User user = this.repository.getUserByUserName(Username);
    	
    	if(user.getId() == contact.getUser().getId()) {
    		this.contactRepository.deleteById(cId);
    		redirectAttributes.addFlashAttribute("message", 
    	            new Message("Contact deleted succesfully...", "success"));
    	}
    	
    	return "redirect:/user/show-contacts/0";
    }
    
    
    //update contact
    @PostMapping("/update-contact/{cid}")
    public String updateForm(@PathVariable("cid")Integer cid,Model model) {
    	
    	Contact contact = this.contactRepository.findById(cid).get();
    	model.addAttribute("contact",contact);
    	return "normal/update_form";
    }
    
  //update contact handler
  @PostMapping("/process-update")
  public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileimage")MultipartFile file,Principal principal,Model m,RedirectAttributes attributes) {
  	try {
  		//old contact 
  		Contact oldContact = this.contactRepository.findById(contact.getcId()).get();
  		if(! file.isEmpty()) {
  			//rewrite file 
  			//delete old photo
  			File savefile = new ClassPathResource("static/img").getFile();
  			
              Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
              
              Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
              
              contact.setImage(file.getOriginalFilename());
  			
  			
  		}else {
  			contact.setImage(oldContact.getImage());
  		}
  		
  		User user = this.repository.getUserByUserName(principal.getName());
  		
  		contact.setUser(user);
  		
  		this.contactRepository.save(contact);
  		
  		attributes.addFlashAttribute("message", new Message("Your contact is updated...","success"));
  		
  		
  	}catch(Exception e) {
  		e.printStackTrace();
  	}
  	return "redirect:/user/"+contact.getcId()+"/contact";
  }
  
  
  //profile controller
  @GetMapping("/profile")
  public String yourProfile(Model model) {
	  model.addAttribute("title","Profile Page");
	  return "normal/profile";
  }
    
  
  //open settings handler
  @GetMapping("/settings")
  public String OpenSettings() {
	  return "normal/settings";
  }
  
  //change password handler
  @PostMapping("/change-password")
  public String changePassword(Principal principal,@RequestParam("oldpassword") String oldpass,@RequestParam("newpassword") String newpass,RedirectAttributes attributes) {
//	  System.out.println("oldPassword== "+oldpass);
//	  System.out.println("newpassword==== "+newpass);
	  
	  String username = principal.getName();
	  User curruser = this.repository.getUserByUserName(username);
	  
	  if(this.encoder.matches(oldpass, curruser.getPassword())) {
		  //change pass
		  curruser.setPassword(this.encoder.encode(newpass));
		  
		  this.repository.save(curruser);
		  attributes.addFlashAttribute("message",new Message("Your password is successfully changed..","success"));
		  
	  }else {
		  //error
		  attributes.addFlashAttribute("message",new Message("Please Enter correct old password !!","danger"));
		  return "redirect:/user/settings";
	  }
	  
	  return "redirect:/user/index";
  }
  
  
  
  
  
  //edit profile handler
  @GetMapping("/editProfile/{id}")
  public String editProfile(@PathVariable("id") Integer id,Model model){
	  User user = this.repository.findById(id).get();
	  model.addAttribute("user",user);
	  
	  return "normal/editProfile";
  }
  
  
  //process edit profile form
  @PostMapping("/process-editProfile")
  public String processEditProfile(@ModelAttribute User user,Principal principal,RedirectAttributes attributes) {
	  
	  try {
		  String uname = principal.getName();
		  User olddetailUser = this.repository.getUserByUserName(uname);
		  
		  user.setId(olddetailUser.getId());
		  user.setPassword(olddetailUser.getPassword());
		  user.setRole(olddetailUser.getRole());
		  user.setImageUrl(olddetailUser.getImageUrl());
		  
		  this.repository.save(user);
		  
		  attributes.addFlashAttribute("message",new Message("Profile Updated Successfully!","success"));
	  }catch(Exception e) {
		  
		  attributes.addFlashAttribute("message",new Message("Something Went Wrong!","danger"));
	  }
	  return "redirect:/user/profile";
  }
    
    



}

















