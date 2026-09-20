package de.Abd.services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.sql.SQLException;
import java.util.Properties;
import java.security.SecureRandom;


public class EmailVerificationService {

    Properties props = new Properties();



     public EmailVerificationService() {

    }


    private int GenerateVerificationCode() {
        SecureRandom secureRandom = new SecureRandom();
        return 100000 + secureRandom.nextInt(900000);

    }


    public Session jackartaInit() {
        final  String appPassword = System.getenv("APP_PASSWORD");
        final  String appUser = System.getenv("APP_EMAIL");

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp-relay.brevo.com");
        props.put("mail.smtp.port", "587");

        return Session.getInstance(props, new Authenticator() {protected PasswordAuthentication getPasswordAuthentication() {return new PasswordAuthentication(appUser, appPassword);}});
     }


    public void sendVerificationCode(String email) throws SQLException {

         Session session = jackartaInit();
         int verificationCode = GenerateVerificationCode();
         String emailFrom = System.getenv("EMAILFROM");

         if(DataService.isUserVerified(email)){
             throw  new SQLException("Email already verified");
         }

        try {
            Message message = new MimeMessage(session);

             message.setFrom(new InternetAddress(emailFrom));
             message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
             message.setSubject("Here is your verification code");
             message.setContent(String.valueOf(verificationCode), "text/html");

            Transport.send(message);
            DataService.SaveVerificationCode(verificationCode,email);



         } catch (MessagingException | SQLException e) {
             throw new RuntimeException(e);
         }


    }








}

