//Source file: D:\\projects\\COMMON\\src\\com\\addval\\wqutils\\utils\\WQConstants.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.wqutils.utils;


public interface WQConstants
{
	public static final String _SHADOW = "_SHADOW";
	public static final int _NEW_MESSAGE = 1;
	public static final int _ERROR_MESSAGE = - 1;
	public static final int _PROCESSED_MESSAGE = 1;
	public static final int _REMOVED_MESSAGE = 0;
	public static final String _NOTIFICATION_TOPIC = "WQUTILS.NOTIFICATION_TOPIC";
	public static final String _JMS_MESSAGE_ID = "JMSMessageID";
	public static final String _QUEUE_TYPE = "QUEUE_TYPE";
	public static final String _QUEUE_NAME = "QUEUE_NAME";
	public static final String _ERROR_QUEUE = "ERROR_QUEUE";
	public static final String _MESSAGE_STATUS = "MESSAGE_STATUS";
	public static final String _NAME_BASED_QUEUE_TYPE = "NAME_BASED";
	public static final String _VALUE_BASED_QUEUE_TYPE = "VALUE_BASED";
	public static final String _STATUS_INPROCESS = "INPROCESS";
	public static final String _STATUS_PROCESSED = "PROCESSED";
	public static final String _STATUS_UNPROCESSED = "UNPROCESSED";
	public static final String _MESSAGE_STATUS_COLUMN = "MESSAGE_STATUS";
	public static final String _LAST_UPDATED_USER_COLUMN = "LAST_UPDATED_BY";
	public static final String _CREATED_TIMESTAMP_COLUMN = "CREATE_TIMESTAMP";
	public static final String _LAST_UPDATED_TIMESTAMP_COLUMN = "LAST_UPDATED_TIMESTAMP";
	public static final String _WQ_DWELL_TIME_COLUMN = "WQ_DWELL_TIME";

	public interface QueueRolesTbl {
		public static final String QUEUE_ROLES_TBL	 = "QUEUE_ROLES";
		public static final String QUEUE_NAME	 = "QUEUE_NAME";
		public static final String ROLE_NAME	 = "ROLE_NAME";
	}

    public interface QueuesTbl {
		public static final String QUEUES_TBL	 = "QUEUES";
		public static final String QUEUE_NAME	 = "QUEUE_NAME";
		public static final String QUEUE_DESC	 = "QUEUE_DESC";
		public static final String EDITOR_NAME	 = "EDITOR_NAME";
		public static final String QUEUE_GROUP_NAME	 = "QUEUE_GROUP_NAME";
		public static final String QUEUE_CONDITION	 = "QUEUE_CONDITION";
		public static final String QUEUE_STATUS_TIMEOUT	 = "QUEUE_STATUS_TIMEOUT";
        public static final String QUEUE_AUTOREFRESH_TIME	 = "QUEUE_AUTOREFRESH_TIME";
        public static final String QUEUE_NEWMESSAGE_TIME	 = "QUEUE_NEWMESSAGE_TIME";
        public static final String DEFAULT_PROMPT_DEFINITION = "DEFAULT_PROMPT_DEFINITION";
        public static final String PROCESS_ACTION	 = "PROCESS_ACTION";
		public static final String DELETE_ACTION	 = "DELETE_ACTION";
		public static final String EDITOR_SOURCE_NAME	 = "EDITOR_SOURCE_NAME";

    }



}
