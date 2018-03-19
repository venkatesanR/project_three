package com.addval.jmsutils;

import javax.jms.Message;


public interface MessageProcessor {
    public void processMessage(Message message) throws Exception;
}
