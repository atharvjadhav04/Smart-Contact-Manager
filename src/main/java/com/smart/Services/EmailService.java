package com.smart.Services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import java.util.Properties;

@Service
public class EmailService {

    public boolean sendEmail(String message, String subject, String to, String from) {

        String host = "smtp.gmail.com";

        Properties properties = System.getProperties();
        System.out.println("Properties " + properties);

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                        "viki40546@gmail.com",
                        "xyzl ckwq szgu klui"
                );
            }
        });

        session.setDebug(true);

        try {
            MimeMessage m = new MimeMessage(session);

            m.setFrom(new InternetAddress(from));
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            m.setSubject(subject);
            m.setContent(message,"text/html");
            //m.setText(message);

            Transport.send(m);
            System.out.println("Email sent successfully");

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
