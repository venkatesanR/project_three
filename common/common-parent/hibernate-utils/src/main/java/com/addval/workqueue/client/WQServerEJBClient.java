package com.addval.workqueue.client;

import com.addval.springutils.ClientRegistry;
import com.addval.workqueue.api.WQServer;

public class WQServerEJBClient {
	private WQServer _wqServer = null;
	private int _serverCount = 0;

	/** Creates a new instance of MyServerEJBClient */
	public WQServerEJBClient() {

	}

	public void setWqServer(WQServer wqServer) {
		_wqServer = wqServer;
	}

	public WQServer getWqServer() {
		return _wqServer;
	}

	public static void main(String[] args) {
		if (args.length > 1) {
			System.out.println("Usage: com.addval.workqueue.client.WQServerEJBClient");
			System.exit(1);
		}

		ClientRegistry registry = new ClientRegistry();

		try {
			WQServerEJBClient client = (WQServerEJBClient) registry.getBean("wqServerEJBClient");
			System.out.println("Invoke client ");
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.exit(0);
	}
}
