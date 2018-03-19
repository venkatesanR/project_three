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

package com.addval.workqueue.api;

import com.addval.ejbutils.dbutils.EJBResultSet;
import com.addval.workqueue.impl.Notification;
import com.addval.workqueue.impl.NotificationException;

import java.rmi.RemoteException;

import java.util.List;
import java.util.Map;


public interface NotificationServer
{
    public void sendMessage(Object message, List<Notification> notifications) throws NotificationException, RemoteException;

    public void sendMessage(Map<String,Object> message, Notification notification) throws NotificationException, RemoteException;

    public void sendMessage(Object message, Notification notification) throws NotificationException, RemoteException;

    public void delete(EJBResultSet ejbRS, Notification notification) throws NotificationException, RemoteException;

    public void refer(EJBResultSet ejbRS, Notification notification) throws NotificationException, RemoteException;

    public void sendMail(String mailContent, Notification notification) throws NotificationException, RemoteException;

    public void accept(EJBResultSet ejbRS, Notification notification) throws NotificationException, RemoteException;
    public void reject(EJBResultSet ejbRS, Notification notification) throws NotificationException, RemoteException;

    public void accept(Object obj, Notification notification) throws NotificationException, RemoteException;
    public void reject(Object obj, Notification notification) throws NotificationException, RemoteException;
    public void refer(Object obj, Notification notification) throws NotificationException, RemoteException;
    public void delete(Object obj, Notification notification) throws NotificationException, RemoteException;

    public void clearQueue(Object obj) throws NotificationException, RemoteException;
    public boolean isQueued(Object obj) throws NotificationException, RemoteException;

    public void sendMail(String mailContent, Notification notification, boolean isHtml) throws NotificationException, RemoteException;
}
