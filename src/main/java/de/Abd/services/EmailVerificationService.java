package de.Abd.services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.sql.SQLException;
import java.util.Properties;
import java.security.SecureRandom;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class EmailVerificationService extends Thread {

     public static String appPassword;
     public static String appUser;
     public static String port;
     public static String smptserver;

    static Properties props = new Properties();
    static int cooldown = 60;
    static boolean  debounce = false;
    static ExecutorService executor = Executors.newSingleThreadExecutor();


static private int GenerateVerificationCode() {
    SecureRandom secureRandom = new SecureRandom();
    return 100000 + secureRandom.nextInt(900000);

}


static public Session jackartaInit() {


    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.smtp.host", smptserver);
    props.put("mail.smtp.port", port);

    return Session.getInstance(props, new Authenticator() {
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(appUser, appPassword);
        }
    });
}






    static public void SendVerificationCode(String email) throws SQLException {


        if (debounce) {System.out.println("Code cd"); return;}
        debounce = true;

        executor.execute(() -> {
        Session session = jackartaInit();
        int verificationCode = GenerateVerificationCode();
        String emailFrom = System.getenv("EMAILFROM");
        System.out.println(System.getenv("EMAILFROM"));

        try {

            if(DataService.isUserVerified(email)){
                throw  new SQLException("Email already verified");
            }

            Message message = new MimeMessage(session);

            message.setFrom(new InternetAddress(emailFrom));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Here is your verification code");
            message.setContent(String.valueOf(verificationCode), "text/html");

            Transport.send(message);
            DataService.SaveVerificationCode(verificationCode,email);
            Thread.sleep(30000);


        } catch (MessagingException | SQLException | InterruptedException e ) {
            throw new RuntimeException(e);
        }finally {
            debounce = false;
        }
        });



    }








}

