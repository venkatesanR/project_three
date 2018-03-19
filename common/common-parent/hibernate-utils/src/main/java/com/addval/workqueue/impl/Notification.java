//Source file: D:\\Projects\\CargoRes\\source\\com\\addval\\cfc\\restrictions\\Notification.java

package com.addval.workqueue.impl;

import java.io.Serializable;

public class Notification implements Serializable
{

    public static final String _TELEX = "TELEX";
    public static final String _QUEUE = "QUEUE";
    public static final String _EMAIL = "EMAIL";

    public static final String _RESPONSE_NOT_REQUIRED = "RESPONSE_NOT_REQUIRED";
    public static final String _ACCEPT_REJECT_REFER = "ACCEPT_REJECT_REFER";


    /**
     * Specifies the type of the notification: QUEUE, EMAIL, or TELEX (as defined in NotificationConstants).
     * The type indicates the medium for transmission that will be used, but can also carry
     * other processing implications; e.g. QUEUE has workflow support implications.
     */
	private String _notificationType;

	/**
     * A brief descriptive string that serves as the unique ID for a prompt.
     * (Corresponds to schema RS_PROMPT_DEFINITION.RS_PROMPT_DEFINITION_CODE)
     */
	private String _promptDefinitionCode;

	/**
     * A two-part notification destination address, to be used as follows:
     * For QUEUE, (queueName, stationCode)
     * For EMAIL, (emailAddress, NULL)
     * For TELEX, (airlineCode.departmentCode, stationCode)
     */
	private String _address1;
	private String _address2;
        private String _activeStation = null;
        private String _userName = "SYSTEM";
        private String _subject = null;
        private String _reason = null;
        /**
         * For customer queue capture the accept or reject status .
         */
        private String _customerQueueStatus=null;
        /**
         * For customer queue capture the customer reject reason.
         */
        private String _rejectReason=null;

        /**
         * Step number for address1 (queue) to enable workflow
         * Step number 1 notification will be first shown in queue console
         */
        protected int _stepNumber = 0;

        /**
         * Rank of address2 (queue user group) within a queue
         * Rank 1 will be first shown in queue console
         */
        protected int _rank = 0;

        private Boolean overrideNotificationMsg = false;
        
	/**
	 * Full constructor.
	 */
	public Notification(String notificationType, String promptDefinitionCode, String address1, String address2)
	{
		_notificationType = notificationType;
		_promptDefinitionCode = promptDefinitionCode;
		_address1 = address1;
		_address2 = address2;
	}
	
	public Notification(String notificationType, String promptDefinitionCode, String address1, String address2, int rank)
	{
		_notificationType = notificationType;
		_promptDefinitionCode = promptDefinitionCode;
		_address1 = address1;
		_address2 = address2;
		_rank = rank;
	}

    public Notification(String notificationType, String promptDefinitionCode, String address1, String address2,String userName,String subject,String reason)
    {
        this(notificationType, promptDefinitionCode, address1,address2);
        _userName = userName;
        _subject = subject;
        _reason = reason;
    }
    
    public Notification(String notificationType, String promptDefinitionCode, String address1, String address2,int rank,String userName,String subject,String reason)
    {
        this(notificationType, promptDefinitionCode, address1,address2,rank);
        _userName = userName;
        _subject = subject;
        _reason = reason;
    }
    /**
     * 
     * @param notificationType
     * @param promptDefinitionCode
     * @param address1
     * @param address2
     * @param userName
     * @param subject
     * @param reason
     * @param customerQueueStatus
     * @param rejectReason
     */
    public Notification(String notificationType, String promptDefinitionCode, String address1, String address2,String userName,String subject,String reason,String customerQueueStatus,String rejectReason)
    {
        this(notificationType, promptDefinitionCode, address1,address2);
        _userName = userName;
        _subject = subject;
        _reason = reason;
        _customerQueueStatus=customerQueueStatus;
        _rejectReason=rejectReason;
        
    }

	/**
	 * Sets the value of the _notificationType property.
	 *
	 * @param notificationType - the new value of the _notificationType property
	 */
	public void setNotificationType(String notificationType)
	{
		_notificationType = notificationType;
	}

	/**
	 * Access method for the _notificationType property.
	 *
	 * @return   the current value of the _notificationType property
	 */
	public String getNotificationType()
	{
		return _notificationType;
	}

	/**
	 * Sets the value of the _promptDefinitionCode property.
	 *
	 * @param promptDefinitionCode - the new value of the _promptDefinitionCode property
	 */
	public void setPromptDefinitionCode(String promptDefinitionCode)
	{
		_promptDefinitionCode = promptDefinitionCode;
	}

	/**
	 * Access method for the _promptDefinitionCode property.
	 *
	 * @return   the current value of the _promptDefinitionCode property
	 */
	public String getPromptDefinitionCode()
	{
		return _promptDefinitionCode;
	}

	/**
	 * Sets the value of the _address1 property.
	 *
	 * @param address1 - the new value of the _address1 property
	 */
	public void setAddress1(String address1)
	{
		_address1 = address1;
	}

	/**
	 * Access method for the _address1 property.
	 *
	 * @return   the current value of the _address1 property
	 */
	public String getAddress1()
	{
		return _address1;
	}

	/**
	 * Sets the value of the _address2 property.
	 *
	 * @param address2 - the new value of the _address2 property
	 */
	public void setAddress2(String address2)
	{
		_address2 = address2;
	}

	/**
	 * Access method for the _address2 property.
	 *
	 * @return   the current value of the _address2 property
	 */
	public String getAddress2()
	{
		return _address2;
	}

	/**
	 * Sets the value of the _address1 and _address2 properties.
	 *
	 * @param address1 - the new value of the _address1 property
	 * @param address2 - the new value of the _address2 property
	 */
	public void setAddress(String address1, String address2)
	{
		this.setAddress1(address1);
		this.setAddress2(address2);
	}

	/**
	 * @return java.lang.String
	 */
	public String toString(){
		StringBuffer sb = new StringBuffer();
		String lineSep = System.getProperty("line.separator");

		sb.append(lineSep);
		sb.append("Notification[");
		sb.append("notifyTyp=").append(this._notificationType).append("; ");
		sb.append("promptCode=").append(this._promptDefinitionCode).append("; ");
                sb.append("activeStation=").append(this._activeStation).append("; ");
		sb.append("addr1=").append(this._address1).append("; ");
		sb.append("addr2=").append(this._address2).append("; ");
                sb.append("step=").append(this._stepNumber).append("; ");
                sb.append("rank=").append(this._rank).append("]");

		return sb.toString();
	}

    public String getReason() {
        return _reason;
    }

    public void setReason(String reason) {
        this._reason = reason;
    }
    public String getUserName() {
        return _userName;
    }

    public void setUserName(String userName) {
        this._userName = userName;
    }
    public String getSubject() {
        return _subject;
    }

    public void setSubject(String subject) {
        this._subject = subject;
    }
    public String getActiveStation() {
        return _activeStation;
    }

    public void setActiveStation(String activeStation) {
        this._activeStation = activeStation;
    }

    public Notification cloneNotification()
    {
        Notification notification = new Notification(this.getNotificationType(),this.getPromptDefinitionCode(),this.getAddress1(), this.getAddress2());
        notification.setReason( this.getReason() );
        notification.setUserName( this.getUserName() );
        notification.setSubject( this.getSubject() );
        notification.setActiveStation( this.getActiveStation() );
        notification.setStepNumber(this.getStepNumber());
        notification.setRank(this.getRank());
        notification.setOverrideNotificationMsg(this.getOverrideNotificationMsg());

        return notification;
    }

    public int getStepNumber() {
        return _stepNumber;
    }

    public void setStepNumber(int stepNumber) {
        this._stepNumber = stepNumber;
    }

    public int getRank() {
        return _rank;
    }

    public void setRank(int rank) {
        this._rank = rank;
    }

	
    /**
     * gets the value of the _customerQueueStatus
     * @return
     */
	public String getCustomerQueueStatus() {
		return _customerQueueStatus;
	}
   /**
    * sets the value of the _customerQueueStatus
    * @param customerQueueStatus
    */
	public void setCustomerQueueStatus(String customerQueueStatus) {
		this._customerQueueStatus = customerQueueStatus;
	}
    /**
     *  gets the value of the _rejectReason
     * @return
     */
	public String getRejectReason() {
		return _rejectReason;
	}
   /**
   *  sets the value of the _rejectReason
   * @param rejectReason
   */
	public void setRejectReason(String rejectReason) {
		this._rejectReason = rejectReason;
	}

	/**
	 * @return the overrideNotificationMsg
	 */
	public Boolean getOverrideNotificationMsg() {
		return overrideNotificationMsg;
	}

	/**
	 * @param overrideNotificationMsg the overrideNotificationMsg to set
	 */
	public void setOverrideNotificationMsg(Boolean overrideNotificationMsg) {
		this.overrideNotificationMsg = overrideNotificationMsg;
	}

	
}
