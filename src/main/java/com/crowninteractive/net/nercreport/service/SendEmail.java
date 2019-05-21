/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.crowninteractive.net.nercreport.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.stereotype.Service;

/**
 *
 * @author johnson3yo
 */
@Service
public class SendEmail {

    public void sendAnEmail(String emailkey, String subject, String to, String msg, String filename, String bcc) throws EmailException {
        Config c = Config.getInstance();

        if (emailkey.equals("a12wq_minions")) {
            HtmlEmail emailAgent = new HtmlEmail();

            emailAgent.setHostName(c.getEmailHostName());
            emailAgent.setSmtpPort(Integer.parseInt(c.getEmailPort()));
            emailAgent.setAuthentication(c.getEmailAddress(), c.getEmailPassword());
            emailAgent.setTLS(true);
            //emailAgent.setDebug(true);
            emailAgent.setFrom(c.getEmailAddress());
            Map<String, String> headers = new HashMap<>();
            headers.put("Sender", c.getEmailSender());
            emailAgent.setHeaders(headers);
            emailAgent.setSubject(subject);
            emailAgent.setCharset("utf8");
            emailAgent.setMsg(msg);
            emailAgent.addTo(to);
            if (bcc != null) {
                emailAgent.addBcc(bcc);
            }
            if (filename != null) {
                String filePath = "/var/files/email/" + filename;
                File attachment = new File(filePath);
                if (attachment.exists()) {
                    emailAgent.attach(attachment);
                }
            }

            emailAgent.send();
        }
    }

    public int sendNoReplyEmail(String subject, String to, String msg) {
        HtmlEmail emailAgent = new HtmlEmail();
        Config c = Config.getInstance();
        emailAgent.setHostName(c.getEmailHostName());
        emailAgent.setSmtpPort(Integer.parseInt(c.getEmailPort()));
        emailAgent.setAuthentication(c.getEmailAddress(), c.getEmailPassword());
        emailAgent.setTLS(true);

        try {
            emailAgent.setFrom(c.getEmailAddress());
            Map<String, String> headers = new HashMap<>();
            headers.put("Sender", c.getEmailSender());
            emailAgent.setHeaders(headers);
            emailAgent.setSubject(subject);
            emailAgent.setCharset("utf8");
            emailAgent.setHtmlMsg(msg);
            emailAgent.addTo(to);
            emailAgent.send();

            return 1;
        } catch (EmailException e) {
            System.out.println("error while wending email: " + e.getMessage());

            return 0;
        }
    }
}
