package com.addval.springstruts;

import org.apache.log4j.Logger;

import com.addval.utils.WorkQueueUtils;
import com.addval.workqueue.api.WQSMetaData;
import com.addval.workqueue.api.WQServer;

public class WorkQueueBaseAction extends BaseAction {
	private static transient final Logger _logger = com.addval.utils.LogMgr.getLogger(WorkQueueBaseAction.class);
	private WQSMetaData _wqSMetaData = null;
	private WQServer _wqServer = null;
	private WorkQueueUtils workQueueUtils = null;

	public WQSMetaData getWqSMetaData() {
		return _wqSMetaData;
	}

	public void setWqSMetaData(WQSMetaData wqSMetaData) {
		this._wqSMetaData = wqSMetaData;
	}

	public WQServer getWqServer() {
		return _wqServer;
	}

	public void setWqServer(WQServer wqServer) {
		this._wqServer = wqServer;
	}

	public WorkQueueUtils getWorkQueueUtils() {
		if (this.workQueueUtils == null) {
			this.workQueueUtils = new WorkQueueUtils();
		}
		return this.workQueueUtils;
	}

	public void setWorkQueueUtils(WorkQueueUtils workQueueUtils) {
		this.workQueueUtils = workQueueUtils;
	}

}
