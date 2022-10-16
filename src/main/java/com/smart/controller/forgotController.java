package com.smart.controller;

import java.util.Random;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.repository.UserRepository;
import com.smart.service.Emailservice;

@Controller
public class forgotController {

	Random ran= new Random(1000);
	//creating controller for forgot password 
	
	@Autowired
	private Emailservice emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	@GetMapping("/forgot")
	public String forgotHandler() {
		
		
		return "forgot_form";
	}
	@PostMapping("/send-OTP")
	public String sendOTpHandler(@RequestParam("email")String email,HttpSession session) {
		
		
		//generating OTP
		
		int otp = ran.nextInt(9999);
		System.out.println(otp);
		
		String subject ="OTP for password reset";
		String message="OTP for password reset "+otp;
		String to=email;
		
		boolean flag = this.emailService.sendEmail(subject, message, to);
		
		if (flag){
			
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			return "verifyOTP";
		}
		else {
		
			
			session.setAttribute("message", new Message("enter valid email","alert-danger"));
		return "forgot_form";
		}
		
	}
	
	@PostMapping("/verifyOTP")
	public String verifyOtp(@RequestParam("otp")int otp,HttpSession session) {
		
		int myOtp=(int) session.getAttribute("myotp");
		String email= (String) session.getAttribute("email");
		
		if (myOtp==otp) {
			
			User user = this.userRepository.getUserByUserName(email);
			if(user==null) {
				session.setAttribute("message", "Username does not exist");
				return "forgot_form";
			}
			else {
				
			}
			
			return "passwordchange";
		}
		else {
			
			session.setAttribute("message", new Message("Wrong OTP entered", "alert-danger"));
			return "verifyOTP";
		}
		
	}
	
	@PostMapping("/changepassword")
	public String changepasswordHandler(@RequestParam("newpassword")String newpassword,@RequestParam("confirmpassword")String confirmpassword,HttpSession session) {
		
		String email= (String) session.getAttribute("email");
		User user = userRepository.getUserByUserName(email);
		
		if(newpassword.equals(confirmpassword)) {
		user.setPassword(passwordEncoder.encode(newpassword));
		this.userRepository.save(user);
		session.setAttribute("message", new Message("Password change successfully", "alert-success"));
		}
		else {
			session.setAttribute("message", new Message("new password and confirm password does not match", "alert-danger"));
			
			return "passwordchange";
		}
		
		return "redirect:/signin?change=password changed successfully";
		
	}
	
}

