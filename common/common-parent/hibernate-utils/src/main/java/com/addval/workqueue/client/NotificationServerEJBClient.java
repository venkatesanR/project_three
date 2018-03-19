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
package com.addval.workqueue.client;

import com.addval.springutils.ClientRegistry;

import com.addval.workqueue.api.NotificationServer;


public class NotificationServerEJBClient {
    private NotificationServer _notificationServer = null;
    private int _serverCount = 0;

    public void setNotificationServer(NotificationServer notificationServer) {
        _notificationServer = notificationServer;
    }

    public NotificationServer getNotificationServer() {
        return _notificationServer;
    }

    public static void main(String[] args) {
        if (args.length > 1) {
            System.out.println(
                "Usage: com.addval.workqueueengine.client.NotificationServerEJBClient");
            System.exit(1);
        }

        ClientRegistry registry = new ClientRegistry();

        try {
            NotificationServerEJBClient client = (NotificationServerEJBClient) registry.getBean(
                    "notificationServerEJBClient");
            System.out.println("Invoke client ");
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
