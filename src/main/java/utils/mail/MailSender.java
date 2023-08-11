package utils.mail;

import property.annotation.Property;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.Properties;


@Singleton
public class MailSender {
    @Inject
    @Property("params.mail.host")
    private String host;
    @Inject
    @Property("params.mail.port")
    private String port;
    @Inject
    @Property("params.mail.username")
    private String username;
    @Inject
    @Property("params.mail.password")
    private String password;
    @Inject
    @Property("params.mail.sendFrom")
    private String from;
    @Inject
    @Property("params.mail.sendTo")
    private String sendTo;



    public void send(String messageTopic, String messageBody) {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port.toString());

        // Get the Session object.
        SimpleMailAuthenticator authenticator = new SimpleMailAuthenticator(username, password);
        Session session = Session.getInstance(properties, authenticator);

        try {
            MimeMessage message = new MimeMessage(session); // email message
            message.setFrom(new InternetAddress(from)); // setting header fields

//            Формирование списка рассылки
            for (String strSendTo:stringSplitter(sendTo, ",")) {
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(strSendTo));
            }
            message.setSubject(messageTopic); // subject line
//            Send message
            message.setText(messageBody);
            Transport.send(message);
            System.out.println("Email Sent successfully....");
        } catch (MessagingException mex){ mex.printStackTrace(); }
    }

    private ArrayList<String> stringSplitter(String inputStr, String splitterChar) {
        ArrayList<String> mailsList = new ArrayList<>();
        if (inputStr.contains(splitterChar)) {
            for (String str:inputStr.split(splitterChar)) {
                mailsList.add(str.trim());
            }
        } else {
            mailsList.add(inputStr.trim());
        }
        return mailsList;
    }
}
