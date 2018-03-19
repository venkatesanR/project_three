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

import com.addval.springutils.ServerRegistry;

import com.addval.workqueue.api.NotificationServer;

import org.apache.log4j.Logger;

import org.springframework.ejb.support.AbstractStatelessSessionBean;

import java.io.Serializable;

import java.rmi.RemoteException;

import java.util.List;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.SessionContext;


public class NotificationServerBean extends AbstractStatelessSessionBean implements NotificationServer, Serializable
{
    private static final long serialVersionUID = 7338965229409659826L;
    private static transient final Logger _logger = com.addval.utils.LogMgr.getLogger(NotificationServerBean.class);
    private NotificationServer notificationServer;

    public void setSessionContext(SessionContext sessionContext)
    {
        super.setSessionContext(sessionContext);
        ServerRegistry registry = new ServerRegistry();
        setBeanFactoryLocator(registry.getBeanFactoryLocator());
        setBeanFactoryLocatorKey(getClass().getPackage().getName());
    }

    protected void onEjbCreate() throws CreateException
    {
        setNotificationServer((NotificationServer) getBeanFactory().getBean( "com.addval.workqueue.api.NotificationServer" ));
    }

    public void sendMessage(Object message, List<Notification> notifications) throws NotificationException, RemoteException
    {
        _logger.info("NotificationServerBean.sendMessage(Object, List of Notification), message=" + message + ", notification=" + notifications);
        getNotificationServer().sendMessage(message, notifications);
    }

    public void sendMessage(Object message, Notification notification) throws NotificationException, RemoteException
    {
        _logger.info("NotificationServerBean.sendMessage(Object,Notification), message=" + message + ", notification=" + notification);
        getNotificationServer().sendMessage(message, notification);

    }

    public void sendMessage(Map<String,Object> message, Notification notification) throws NotificationException, RemoteException
    {
        _logger.info("NotificationServerBean.sendMessage(Map,Notification), message=" + message + ", notification=" + notification);
        if(message instanceof Map)
            getNotificationServer().sendMessage((Map<?,?>) message, notification);
    }

    public void refer(EJBResultSet ejbRS, Notification notification) throws NotificationException, RemoteException
    {
        getNotificationServer().refer(ejbRS, notification);
    }

    public void delete(EJBResultSet ejbRS, Notification notification) throws NotificationException, RemoteException
    {
        getNotificationServer().delete(ejbRS, notification);
    }

    public void sendMail(String mailContent, Notification notification) throws NotificationException, RemoteException
    {
        getNotificationServer().sendMail(mailContent, notification);
    }

    public void sendMail(String mailContent, Notification notification, boolean isHtml) throws NotificationException, RemoteException
    {
    	getNotificationServer().sendMail(mailContent, notification, isHtml);
     }

    protected NotificationServer getNotificationServer()
    {
        return notificationServer;
    }

    protected void setNotificationServer(NotificationServer notificationServer)
    {
        this.notificationServer = notificationServer;
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
}
