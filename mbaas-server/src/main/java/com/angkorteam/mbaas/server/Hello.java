package com.angkorteam.mbaas.server;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

/**
 * Created by socheat on 2/26/16.
 */
public class Hello {

    public void say(MimeMessage message) throws IOException, MessagingException {
        System.out.println(message.getSubject());
        if (message.getContent() instanceof String) {
            System.out.println((String) message.getContent());
        }
    }

}
