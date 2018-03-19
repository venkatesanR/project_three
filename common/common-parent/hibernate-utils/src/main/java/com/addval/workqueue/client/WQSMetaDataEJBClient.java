/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.workqueue.client;

import com.addval.springutils.ClientRegistry;
import com.addval.workqueue.api.WQSMetaData;

public class WQSMetaDataEJBClient {
	private WQSMetaData _wqSMetaData = null;
	private int _serverCount = 0;

	/** Creates a new instance of MyServerEJBClient */
	public WQSMetaDataEJBClient() {

	}

	public void setWqSMetaData(WQSMetaData wqSMetaData) {
		_wqSMetaData = wqSMetaData;
	}

	public WQSMetaData getWqSMetaData() {
		return _wqSMetaData;
	}

	public static void main(String[] args) {
		if (args.length > 1) {
			System.out.println("Usage: com.addval.workqueue.client.WQSMetaDataEJBClient");
			System.exit(1);
		}

		ClientRegistry registry = new ClientRegistry();

		try {
			WQSMetaDataEJBClient client = (WQSMetaDataEJBClient) registry.getBean("wqSMetaDataEJBClient");
			System.out.println("Invoke client ");
		}
		catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		System.exit(0);
	}
}
