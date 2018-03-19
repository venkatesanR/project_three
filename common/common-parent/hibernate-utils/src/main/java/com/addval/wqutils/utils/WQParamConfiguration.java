package com.addval.wqutils.utils;

import java.util.Iterator;
import java.util.Hashtable;

public class WQParamConfiguration {

	private Hashtable _wqQueues = null;

	public void setWQQueues(Hashtable wqQueues) {
		_wqQueues = wqQueues;
	}

	public void addWQQueue(WQQueue wqQueue) {

		if (_wqQueues == null)
			_wqQueues = new Hashtable();

		_wqQueues.put( wqQueue.getName(), wqQueue);
	}

	public Hashtable getWQQueues() {
		if (_wqQueues == null)
			_wqQueues = new Hashtable();
		return _wqQueues;
	}

	public String toString() {
		StringBuffer queues = new StringBuffer();
		String queueName = null;
		WQParam wqParam= null;

		for(Iterator iterator = getWQQueues().keySet().iterator();iterator.hasNext(); ) {
			queueName = (String)iterator.next();
			queues.append( getWQQueues().get( queueName ).toString() ).append( System.getProperty( "line.separator" ) );

		}
		return queues.toString();
	}
}
