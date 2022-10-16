package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;

@Controller
@RequestMapping("/user")
public class UserController {
	

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContactRepository contacRepository;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	@ModelAttribute
	public void addcommondata(Model m ,Principal principle) {
		
		  String name = principle.getName();
		  System.out.println("Username "+name);
		  //get the user using username(email);
		  
		  User user = this.userRepository.getUserByUserName(name);
		  System.out.println("user"+user);
		  
		  m.addAttribute("user",user);
		
	}
	
	@GetMapping(value="/index")
	public String dashboard(Model m, Principal principle) {
		m.addAttribute("title","Dashboard");
		return "normal/user_dashboard";
	}
	
	//adding add contact handler
	@GetMapping("/addcontact")
	public String addcontact(Model m) {
		
		m.addAttribute("title","Add Contact");
		m.addAttribute("contact", new Contact());
		 
		
		return "normal/add_contact";
	}
  
	 //processing form
	@PostMapping("/process_contact")
     public String processform(@ModelAttribute Contact contact,@RequestParam("profileImage")MultipartFile file,
    		 Principal principle,HttpSession session) {
    	try { 
		   String name = principle.getName();
		   User user = this.userRepository.getUserByUserName(name);
		   
		   if(file.isEmpty()) {
			   contact.setImage("contact.png");
		   }
		   else {
			   contact.setImage(file.getOriginalFilename());
			   
			   File file2 = new ClassPathResource("static/image").getFile();
			   
			   Path path = Paths.get(file2.getAbsolutePath()+File.separator+file.getOriginalFilename());
			   
			   Files.copy(file.getInputStream(),path ,StandardCopyOption.REPLACE_EXISTING );
			   System.out.println("image uploaded");
		   }
		   
		   
		   contact.setUser(user);
		    user.getContacts().add(contact);
		    
		    this.userRepository.save(user);
		System.out.println("Conact Data "+ contact);
		System.out.println("Added");
		session.setAttribute("message",new Message("Your contact added successfully","alert-success"));
		
    	}
    	catch (Exception e) {
			System.out.println("Error" + e.getMessage());
			session.setAttribute("message",new Message("Something Went wrong!!","alert-danger"));
		} return "normal/add_contact";
     }
	
	//show contacs
	@GetMapping("/showcontact/{page}")
	public String showcontact(@PathVariable("page") int page,Model m,Principal principle, HttpSession session) {
		
		m.addAttribute("title","View Contact");
		
		String name = principle.getName();
		User user = this.userRepository.getUserByUserName(name);
		Pageable request = PageRequest.of(page, 4);
		Page<Contact> findContactByUser = this.contacRepository.findContactByUser(user.getUid(),request);
		
		m.addAttribute("contact",findContactByUser);
		m.addAttribute("currentpage",page);
		
		//total page
		m.addAttribute("totalpages",findContactByUser.getTotalPages());
		
		// adding pagination at one page 5 contact display
		return "normal/show_contact";
	}
	
	@GetMapping("/contact/{cid}")
	public String showcontactDetail(@PathVariable("cid")Integer cid,Model m,Principal principle) {
		m.addAttribute("title","contact detail");
		
		Optional<Contact> findById = this.contacRepository.findById(cid);
		Contact contact = findById.get();
		
		String name = principle.getName();
		User user = this.userRepository.getUserByUserName(name);
		
		if(user.getUid()==contact.getUser().getUid());
		
		m.addAttribute("contact",contact);
		m.addAttribute("title",contact.getName());
		return "normal/contact_detail";
	}
	//deleting contact
	@GetMapping("/delete/{cid}")
	public String deleteContact(@PathVariable("cid")Integer cid,Model m,HttpSession session,Principal principle) {
		String name = principle.getName();
		User user = this.userRepository.getUserByUserName(name);
		Optional<Contact> id = this.contacRepository.findById(cid);
		Contact contact = id.get();
		
		//user need to be unlinked before deleting contact;
		//one way to delete but sometime may not remove this entity from database it remove only from server
		if(user.getUid()==contact.getUser().getUid()) {
			contact.setUser(null);
			
		this.contacRepository.delete(contact);
		session.setAttribute("message", new Message("Contact Deleted Successfully !!...", "alert-success"));
		}
		/*second way to delete entity from both database and view 
		    add @orphanvalue=true in user class at contact variable 
		    add equal method in Contact
		    @Override
//	public boolean equals(Object obj) {
//		// TODO Auto-generated method stub
//		return this.cid==((Contact) obj).getCid();
//	}
	
		    then get user 
		    String name=principle.getName();
		   User user= this.userRepository.getUserByUserName(name);
		   user.getContact().remove(contact);
		    this.contacRepository.save(user);
		
		*/
		return "redirect:/user/showcontact/0";
	}
	
	//updating contact detail
	
	@PostMapping("/update-contact/{cid}")
	public String updateContact(@PathVariable("cid")Integer cid, Model m) {
		m.addAttribute("tilte","update contact");
		Contact contact = this.contacRepository.findById(cid).get();
		
		m.addAttribute("contact",contact);
		
		
		return "normal/update_contact";
	}
	//processing updating form
	//@RequestMapping(value="/process_update" method="post")
	@PostMapping("/process_update")
	public String processUpdate(@ModelAttribute Contact contact,@RequestParam("profileImage")MultipartFile file,
			HttpSession session,Principal principle) {
		
//		
		//old contact detail need to fetch so we can set previoud image
		
		
		try {
			 Optional<Contact> byId = this.contacRepository.findById(contact.getCid());
			 Contact oldcontact = byId.get();
			if(!file.isEmpty()) {
				contact.setImage(file.getOriginalFilename());
				   
				   File file2 = new ClassPathResource("static/image").getFile();
				   
				   Path path = Paths.get(file2.getAbsolutePath()+File.separator+file.getOriginalFilename());
				   
				   Files.copy(file.getInputStream(),path ,StandardCopyOption.REPLACE_EXISTING );
				   contact.setImage(file.getOriginalFilename());
			    System.out.println(contact);	
			}
			else {
				contact.setImage(oldcontact.getImage());
			}
			User user = this.userRepository.getUserByUserName(principle.getName());
			contact.setUser(user);
			this.contacRepository.save(contact);
			session.setAttribute("message", new Message("Information updated succcessfully", "alert-success"));
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		return "redirect:/user/contact/"+contact.getCid();
	}
	
	
	//getting user profile information
	@GetMapping("/profile")
	public String userProfile(Model m) {
		m.addAttribute("title","user profile");
		return "normal/user_profile";
	}
	
	//adding seeting handler
	@GetMapping("/setting")
	public String seetinghandler(Model m) {
		m.addAttribute("title","Setting");
		return "normal/setting";
	}
	//create handler to handle change password
	@PostMapping("/changepassword")
	public String changePasswordHandler(@RequestParam("oldpassword")String oldpassword,
			@RequestParam("newpassword")String newpassword,
			@RequestParam("confirmpassword")String confirmpassword,Principal principle,HttpSession session) {
		System.out.println(oldpassword);
		System.out.println(newpassword);
		System.out.println(confirmpassword);
		
		String name = principle.getName();
		User user = this.userRepository.getUserByUserName(name);
		String password = user.getPassword();
		
		if (this.passwordEncoder.matches(oldpassword, user.getPassword())) {
			
		
			if (newpassword.equals(confirmpassword)) {
				user.setPassword(this.passwordEncoder.encode(newpassword));	
				this.userRepository.save(user);
				session.setAttribute("message", new Message("Your password is successfully changed", "alert-success"));	
			}
			
			else {
				
				session.setAttribute("message", new Message("new password and confirm password does not match", "alert-danger"));
				return "redirect:/user/setting";
			}
			
		}
		else {
			session.setAttribute("message", new Message("Old pasword does not match", "alert-danger"));
			return "redirect:/user/setting";
		}
		
		return "redirect:/user/index";
	}
}
