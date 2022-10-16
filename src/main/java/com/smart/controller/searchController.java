package com.smart.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.repository.ContactRepository;
import com.smart.repository.UserRepository;

@RestController
public class searchController {
    
	@Autowired
	 private UserRepository userRepository;
	 @Autowired
	 private ContactRepository contactRepository;
	 
	 
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query")String query, Principal principle){
		
		System.out.println(query);
		
		User user = this.userRepository.getUserByUserName(principle.getName());
		
		List<Contact> contact = this.contactRepository.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contact);
		
		
	}
}
