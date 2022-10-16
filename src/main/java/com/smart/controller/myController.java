package com.smart.controller;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.repository.UserRepository;

@Controller
public class myController {
  
	 @Autowired
	 private UserRepository userRepository;
	 
	 @Autowired
	 private BCryptPasswordEncoder passwordEncoder;
	 
	 @GetMapping("/")
	 public String home(Model m) {
		 
		 m.addAttribute("title","Home-Smart contact manager");
		 
		 return "home";
	 }
	 
	 @GetMapping("/signup")
	 public String signup(Model m) {
		 
		 m.addAttribute("title","Register-Smart contact manager");
		 m.addAttribute("user", new User());
		 return "signup";	
	 }
	 
	 @GetMapping("/about")
	 public String about(Model m) {
		 
		 m.addAttribute("title","About-Smart contact manager");
		 
		 
		 return "about";
	 }
	  @PostMapping("/do_register")
	  public String registerUser(@Valid @ModelAttribute("user") User user,BindingResult result,
			  @RequestParam("profileImage")MultipartFile file,
			  @RequestParam(value="agreement",defaultValue = "false")boolean agreement,
			  Model m, HttpSession session) {
		 
		  try {
			  
//			  if (!agreement) {
//				  System.out.println("Please check termed and condition");
//				  
//				  throw new Exception("Please check termed and condition");
//				
//			}
			  if (result.hasErrors()) {
				  System.out.println("Error "+result.toString());
				  m.addAttribute("user",user);
				return "signup";
			}
			  user.setRole("ROLE_USER");
			  user.setEnable(true);
			  if(file.isEmpty()) {
				   user.setImageUrl("default.png");
			   }
			   else {
				   user.setImageUrl(file.getOriginalFilename());
				   
				   File file2 = new ClassPathResource("static/image").getFile();
				   
				   Path path = Paths.get(file2.getAbsolutePath()+File.separator+file.getOriginalFilename());
				   
				   Files.copy(file.getInputStream(),path ,StandardCopyOption.REPLACE_EXISTING );
				   System.out.println("image uploaded");
			   }
			  
			  user.setPassword(passwordEncoder.encode(user.getPassword()));
			  System.out.println(agreement);
			  System.out.println("User"+user);
			  
			  User user2 = this.userRepository.save(user);
			  m.addAttribute("user", new User());
			  
			  session.setAttribute("message", new Message("You have successfully registered","alert-success"));
			  
			  return "signup";
			  
			  
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			m.addAttribute("user",user);
			session.setAttribute("message", new Message(e.getMessage(), "alert-danger"));
			return "signup";
		}
		  
		 
	  }

          @GetMapping("/signin")
	   public String customLogin(Model model) {
		   model.addAttribute("title","Login Page");
		   
		   return "login";
		   
	   }


}
