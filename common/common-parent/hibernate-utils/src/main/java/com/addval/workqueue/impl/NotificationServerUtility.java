/*
 * Copyright (c) 2001-2006 Accenture LLC.
 * All Rights Reserved.
 *
 * This software is the confidential and proprietary information of
 * Accenture. ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Accenture.
 *
 * ACCENTURE MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. ACCENTURE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A
 * RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */
package com.addval.workqueue.impl;

import com.addval.ejbutils.dbutils.EJBResultSet;

import com.addval.utils.SMTPWrapper;
import com.addval.utils.WorkQueueUtils;

import com.addval.workqueue.api.NotificationServer;

import com.addval.wqutils.metadata.WQMetaData;
import com.addval.wqutils.utils.WQConstants;

import org.apache.log4j.Logger;

import java.rmi.RemoteException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;


public class NotificationServerUtility extends BaseNotificationServerUtility implements NotificationServer
{
    private static transient final Logger _logger = com.addval.utils.LogMgr.getLogger(NotificationServerUtility.class);
    private String _mailHost;
    private String _mailFrom;
	private JavaMailSender javaMailSender = null;

	public JavaMailSender getJavaMailSender() {
		return javaMailSender;
	}

	public void setJavaMailSender(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

    public void sendMessage(Object message, List<Notification> notifications) throws NotificationException, RemoteException
    {
        if(notifications == null)
            return;

        try
        {
            for(Iterator<Notification> iterator = notifications.iterator(); iterator.hasNext();)
                sendMessage( message, iterator.next() );
        }
        catch(Exception e)
        {
            _logger.error(e);
            throw new NotificationException( e );
        }
    }

    public void sendMessage(Object message, Notification notification) throws NotificationException, RemoteException
    {
        if(message instanceof Map)
        {
            sendMessage((Map<String,Object>) message, notification);
            _logger.debug("NotificationServerBean.sendMessage(Object,Notification), message=" + message + ", notification=" + notification);
        }
        else
            _logger.warn("Objects of type Map alone supported in the base version. " + message);
    }

    public void sendMessage(Map<String,Object> message, Notification notification) throws NotificationException, RemoteException
    {
        if(notification == null)
            return;

        if(!Notification._QUEUE.equalsIgnoreCase(notification.getNotificationType()))
            return;

        if(message.isEmpty())
            throw new NotificationException("No values to add into Message Queue");
        try {
	        WQMetaData wqMetaData = getWqSMetaData().lookup(notification.getAddress1());
	        if(message.get("QUEUE_NBR") == null)
	            message.put("QUEUE_NBR", String.valueOf(wqMetaData.getQueueNo()));

	        if((message.get("QUEUE_ADDRESS1") == null) && (wqMetaData.getQueueName() != null))
	            message.put("QUEUE_ADDRESS1", wqMetaData.getQueueName());

	        EJBResultSet ejbRS = WorkQueueUtils.getInsertEJBResultSet(wqMetaData.getEditorMetaData(), message);
        	getWqServer().sendMessage(ejbRS);
        }
        catch(Exception e) {
        	_logger.error( e );
        	throw new NotificationException( e  );
        }
    }

    public void refer(EJBResultSet ejbRS, Notification notification) throws NotificationException
    {
        if((ejbRS == null) || (notification == null))
            return;

        if(!Notification._QUEUE.equalsIgnoreCase(notification.getNotificationType()))
            return;
        try
        {
            WQMetaData wqMetaData = getWqSMetaData().lookup(notification.getAddress1());
            ejbRS.beforeFirst();
            if(!ejbRS.next())
                return;

            EJBResultSet ejbResultSet = getQueueDeleteEJBResultSet(ejbRS, notification, wqMetaData);
            getWqServer().deleteMessage(ejbResultSet);
            Map<String, Object> params = getParams(ejbRS, notification, wqMetaData);
            EJBResultSet insertEJBRS = WorkQueueUtils.getInsertEJBResultSet(wqMetaData.getEditorMetaData(), params);
            getWqServer().sendMessage(insertEJBRS);
        }
        catch(Exception ex)
        {
            _logger.error(ex);
            throw new NotificationException(ex);
        }
    }

    protected Map<String, Object> getParams(EJBResultSet ejbRS, Notification notification, WQMetaData wqMetaData)
    {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("SENDER_USERID", notification.getUserName());
        params.put("SENDER_QUEUE_ADDRESS1", ejbRS.getString("QUEUE_ADDRESS1"));
        params.put("SENDER_QUEUE_ADDRESS2", ejbRS.getString("QUEUE_ADDRESS2"));
        params.put("SUBJECT", notification.getSubject());
        params.put("MESSAGE_STR", notification.getReason());
        params.put("QUEUE_NAME", String.valueOf(wqMetaData.getQueueName()));
        params.put("QUEUE_NBR", String.valueOf(wqMetaData.getQueueNo()));
        params.put("QUEUE_ADDRESS1", wqMetaData.getQueueName());
        params.put("QUEUE_ADDRESS2", notification.getAddress2());
        params.put("MESSAGE_STATUS", WQConstants._STATUS_UNPROCESSED);
        return params;
    }

    public void delete(EJBResultSet ejbRS, Notification notification) throws NotificationException
    {
        try
        {
            if((ejbRS == null) || (notification == null))
                return;

            if(!Notification._QUEUE.equalsIgnoreCase(notification.getNotificationType()))
                return;

            WQMetaData wqMetaData = getWqSMetaData().lookup(notification.getAddress1());
            ejbRS.beforeFirst();
            if(!ejbRS.next())
                return;

            EJBResultSet ejbResultSet = getQueueDeleteEJBResultSet(ejbRS, notification, wqMetaData);
            getWqServer().deleteMessage(ejbResultSet);
        }
        catch(Exception ex)
        {
            _logger.error(ex);
            throw new NotificationException(ex);
        }
    }

    //todo: to use the spring based mail sender - Jeyaraj
    /*public void sendMail(String mailContent, Notification notification) throws NotificationException
    {
        String rsNotifyTypeCode = notification.getNotificationType();
        String rsNotifyAddress1 = notification.getAddress1();
        if((rsNotifyTypeCode == null) || !rsNotifyTypeCode.equalsIgnoreCase("EMAIL"))
            return;

        if((rsNotifyAddress1 == null) || !rsNotifyAddress1.equalsIgnoreCase(rsNotifyAddress1))
            return;

        if(rsNotifyAddress1.indexOf(",") > 0)
        {
            String[] address1 = rsNotifyAddress1.split(",");
            Vector mailCc = new Vector();
            mailCc.addAll(Arrays.asList(address1));
            sendMail(address1[0], mailCc, mailContent, notification);
        }
        else if(rsNotifyAddress1.length() > 0)
            sendMail(rsNotifyAddress1, null, mailContent, notification);
    }*/

    private void sendMail(String mailTo, Vector mailCc, String mailContent, Notification notification)
    {
        String mailHost = getMailHost();
        String mailFrom = getMailFrom();
        StringBuffer response = new StringBuffer();
        int retValue = 0;
        try
        {
            Properties props = System.getProperties();
            if(mailHost != null)
                props.put("mail.smtp.host", mailHost);

            javax.mail.Session session = javax.mail.Session.getDefaultInstance(props, null);
            javax.mail.Message mess = new MimeMessage(session);
            Multipart multi = new MimeMultipart();
            mess.setContent(multi);
            mess.addHeader("Content-Type", "text/html");
            SMTPWrapper smtpWrapper = new SMTPWrapper(mailHost);
            // Sending Mail yet to be tested
            retValue = smtpWrapper.send(mailTo, mailCc, mess, mailContent, notification.getSubject(), response, mailFrom);
        }
        catch(Exception e)
        {
            retValue = -1;
            _logger.warn(e);
        }

        if(retValue == 0)
            _logger.info("Mail from" + mailFrom + "Mail sent to=" + mailTo + " using server=" + mailHost);
        else
            _logger.error("Could not send mail." + response);
    }

    public String getMailFrom()
    {
        return _mailFrom;
    }

    public void setMailFrom(String mailFrom)
    {
        this._mailFrom = mailFrom;
    }

    public String getMailHost()
    {
        return _mailHost;
    }

    public void setMailHost(String mailHost)
    {
        this._mailHost = mailHost;
    }

    public void accept(EJBResultSet ejbRS, Notification notification) throws NotificationException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void reject(EJBResultSet ejbRS, Notification notification) throws NotificationException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void accept(Object obj, Notification notification) throws NotificationException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void reject(Object obj, Notification notification) throws NotificationException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void refer(Object obj, Notification notification) throws NotificationException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void delete(Object obj, Notification notification) throws NotificationException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void clearQueue(Object obj) throws NotificationException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public boolean isQueued(Object obj) throws NotificationException, RemoteException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void sendMail(String mailContent, Notification notification) throws NotificationException
    {
    	sendMail(mailContent, notification, false);
    }

    /*
     * This method supports HTML formatting of text
     * 
     * isHtml - to set to true for HTML formatting
     * 
     */
    public void sendMail(String mailContent, Notification notification, boolean isHtml) throws NotificationException
    {
        String rsNotifyTypeCode = notification.getNotificationType();
        String rsNotifyAddress1 = notification.getAddress1();
        if((rsNotifyTypeCode == null) || !rsNotifyTypeCode.equalsIgnoreCase("EMAIL"))
            return;

        if((rsNotifyAddress1 == null) || !rsNotifyAddress1.equalsIgnoreCase(rsNotifyAddress1))
            return;

        String[] address1 = rsNotifyAddress1.split(",");
        sendMail(address1, mailContent, notification, isHtml);
    }

    /*
     * TO address, from address, subject, text is set in MimeMessageHelper
     * 
     * isHtml - to set to true for HTML formatting
     * 
     */
    private void sendMail(String[] addresses, String mailContent, Notification notification, boolean isHtml)
    {
    	MimeMessage mimeMessage = getJavaMailSender().createMimeMessage();
    	try {
    		 
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
	        String mailFrom = getMailFrom();
	        String mailHost = getMailHost();
	        Properties props = System.getProperties();
         	if(mailHost != null)
                props.put("mail.smtp.host", mailHost);
			helper.setFrom(mailFrom);
			if (addresses != null) {
				helper.setTo(addresses);
			} 
			
			helper.setSubject(notification.getSubject());
			helper.setText(mailContent, isHtml);
			try {
				getJavaMailSender().send(mimeMessage);
				for(String address : addresses){
					_logger.info("addresses" + address);
				}
				_logger.info("Mail sent using server=" + mailHost);
			} catch (MailException ex) {
				_logger.error("Could not send mail.", ex);
			}
		} catch (Exception ex) {
			_logger.error("Could not send mail outer try catch loop.", ex);
		}
		
		
    }
}
