package com.smart.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;


@Service
public class Emailservice {
  
public boolean sendEmail(String subject,String message, String to) {
		
		boolean f= false;
		String from="gawadeaditya5898@gmail.com";
					// TODO Auto-generated method stub
			//creating host of Gmail
			String host="smtp.gmail.com";
			
			//getting system properties 
			
			Properties properties = System.getProperties();
			
			
			//setting important information i properties object 
			
			//setting host
			properties.put("mail.smtp.host", host);
			properties.put("mail.smtp.port", "465");
			properties.put("mail.smtp.ssl.enable", "true");
			properties.put("mail.smtp.auth","true");
			
			//get the session object
			
			Session session = Session.getInstance(properties, new Authenticator() {

				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					// TODO Auto-generated method stub
					return new PasswordAuthentication("gawadeaditya5898@gmail.com","myssdteznmzdrwii");
				}
				
				
				
			});
			
			session.setDebug(true);
			
			//compose the message
			
			MimeMessage mimeMessage = new MimeMessage(session);
			
			
			
			try {
				//from
				mimeMessage.setFrom(from);
				
				//to adding to receipent
				
				mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
				
				//adding subject messsage
				
				mimeMessage.setSubject(subject);
				
				//adding message
				
				mimeMessage.setText(message);
				
				
				//sent using transport
				
				Transport.send(mimeMessage);
				
				System.out.println("message sent successfully");
				f=true;
				
			} catch (MessagingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return f;
		
	}
}
