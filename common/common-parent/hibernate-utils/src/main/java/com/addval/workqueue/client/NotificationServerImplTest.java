/**
 * Copyright
 * AddVal Technology Inc.
 */
package com.addval.workqueue.client;

import com.addval.springutils.ServerRegistry;

import com.addval.workqueue.api.NotificationServer;

public class NotificationServerImplTest {
    private NotificationServer _notificationServer;

    public NotificationServerImplTest() {
        ServerRegistry registry = new ServerRegistry();
//        registry.setBeanFactoryLocatorKey(NotificationServerConstants.PRIMARY_CONTEXT_ID);
//        _notificationServer = (NotificationServer) registry.getBean(NotificationServerConstants.NOTIFICATION_SERVER_BEAN_ID);
        System.out.println("NotificationServer : Got registry");
    }
}
