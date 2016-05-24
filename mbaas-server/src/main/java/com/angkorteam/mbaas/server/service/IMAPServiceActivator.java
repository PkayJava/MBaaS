package com.angkorteam.mbaas.server.service;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;

/**
 * Created by socheat on 2/26/16.
 */
public class IMAPServiceActivator {

    public void activation(MimeMessage message) throws IOException, MessagingException {
        if (message.getContent() instanceof String) {
            String content = (String) message.getContent();
            System.out.println(message.getSubject());
            System.out.println(content);
        } else if (message.getContent() instanceof MimeMultipart) {
            MimeMultipart content = (MimeMultipart) message.getContent();
            for (int index = 0; index < content.getCount(); index++) {
                BodyPart part = content.getBodyPart(index);
                if (part.getContent() instanceof String && part.getContentType().startsWith("text/plain")) {

                }
            }
        }
    }

}
