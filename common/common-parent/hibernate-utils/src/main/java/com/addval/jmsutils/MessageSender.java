//Source file: d:\\projects\\cargores\\source\\com\\addval\\cargores\\CargoResMessageSender.java

//Source file: C:\\users\\prasad\\projects\\cargores\\source\\com\\addval\\cargores\\CargoResMessageSender.java

/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.jmsutils;

import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import org.apache.commons.messenger.Messenger;
import org.apache.commons.messenger.MessengerManager;
import com.addval.environment.Environment;
import javax.jms.Destination;
import java.io.Serializable;
import javax.jms.JMSException;
import java.net.URL;

public class MessageSender
{
	private static String _module = "com.addval.cargores.CargoResMessageSender";
	private static final int _DSTN_QUEUE = 0;
	private static final int _DSTN_TOPIC = 1;
	private static final String _QUEUE = "queue";
	private static final String _TOPIC = "topic";
	private static final String _QUEUE_FALLBACK = "queue_fallback";
	private static final String _TOPIC_FALLBACK = "topic_fallback";
    private static final String _TOPIC_BATCH = "topic_batch";
    private static final String _QUEUE_BATCH = "queue_batch";
	private static final String _MESSENGER_XML_FILE = "messenger.xml";
	private static MessengerManager _messengerManager = null;

	private static transient final org.apache.log4j.Logger _logger = com.addval.utils.LogMgr.getLogger(MessageSender.class);


	/**
	 * This interface allows users to send the object supplied to a topic. The object
	 * that is passed in should be serializable. This method will first try obtaining
	 * a connection to the  default topic. If that fails it will try obtaining a
	 * connection to the fall back topic.
	 * @param object
	 * @param topicName
	 * @throws javax.jms.JMSException
	 * @roseuid 3EF9DA6E032C
	 */
	public static final void sendToTopic(Serializable object, String topicName) throws JMSException
	{
		send( object, _DSTN_TOPIC, topicName, false );
	}

	/**
	 * @param object
	 * @param queueName
	 * @throws javax.jms.JMSException
	 * @roseuid 3EF9DB070167
	 */
	public static final void sendToQueue(Serializable object, String queueName) throws JMSException
	{
		send( object, _DSTN_QUEUE, queueName, false );
	}

	/**
	 * When sendToAll is false, the message is posted into the primary topic/queue
	 * if that fails then the message is posted into the fallback topic/queue.
	 * Use this when only one topic/queue in a cluster should receive the message
	 *
	 * When sentToAll is true, the message is posted to the primary topic/queue and
	 * the fallback topic/queue. Use this to refresh cache
	 *
	 * @param object
	 * @param destinationType
	 * @param destinationName
	 * @throws javax.jms.JMSException
	 * @roseuid 3EF9E04A0177
	 */
	private static final void send(Serializable object, int destinationType, String destinationName, boolean sendToAll) throws JMSException
	{
		try {

			Messenger 	  messenger   = null;
			Destination   destination = null;
			ObjectMessage message 	  = null;


			boolean       sentToPrimary = false;
			boolean       sentToFallback = false;

			try {

				if (destinationType == _DSTN_TOPIC)
					messenger = getMessengerManager().get( _TOPIC );
				else
					messenger = getMessengerManager().get( _QUEUE );

				sentToPrimary = true;

			}
			catch (JMSException e) {

				_logger.error( e );

				// Now Try the Fall Back Topic
				if (destinationType == _DSTN_TOPIC)
					messenger = getMessengerManager().get( _TOPIC_FALLBACK );
				else
					messenger = getMessengerManager().get( _QUEUE_FALLBACK );

				sentToFallback = true;

			}

			destination = messenger.getDestination( destinationName );

			// Create the Object Message
			message = messenger.createObjectMessage( object );
			messenger.send( destination, message );


			if (sendToAll)
			{
				if (sentToFallback == false)
				{
					messenger = null;

					try
					{
						if (destinationType == _DSTN_TOPIC)
							messenger = getMessengerManager().get( _TOPIC_FALLBACK );
						else
							messenger = getMessengerManager().get( _QUEUE_FALLBACK );

						sentToFallback = true;
					}
					catch (JMSException e) {
						_logger.error( e );
					}


					if (messenger != null)
					{
						destination = messenger.getDestination( destinationName );

						// Create the Object Message and send
						message = messenger.createObjectMessage( object );
						messenger.send( destination, message );
					}

				}
			}

		}
		catch (JMSException e) {
			// Log it and throw the error
			_logger.error( e );
			throw e;
		}
	}

	/**
	 * if sendToAll is false, the message is posted into the primary topic/queue
	 * if that fails then the message is posted into the fallback topic/queue.
	 * Use this when only one topic/queue in a cluster should receive the message
	 *
	 * When sentToAll is true, the message is posted to the primary topic/queue and
	 * the fallback topic/queue. Use this to refresh cache
	 * @param msg
	 * @param destinationType
	 * @param destinationName
	 * @throws javax.jms.JMSException
	 * @roseuid 3F0A0CA70003
	 */
	private static final String send(String msg, int destinationType, String destinationName, boolean sendToAll) throws JMSException
	{
		String messageId = null;
		try {

			Messenger 	  messenger   = null;
			Destination   destination = null;
			TextMessage   message 	  = null;

			boolean       sentToPrimary = false;
			boolean       sentToFallback = false;

			try {

				if (destinationType == _DSTN_TOPIC)
					messenger = getMessengerManager().get( _TOPIC );
				else
					messenger = getMessengerManager().get( _QUEUE );

				sentToPrimary = true;
			}
			catch (JMSException e) {

				_logger.error( e );

				// Now Try the Fall Back Topic
				if (destinationType == _DSTN_TOPIC)
					messenger = getMessengerManager().get( _TOPIC_FALLBACK );
				else
					messenger = getMessengerManager().get( _QUEUE_FALLBACK );

				sentToFallback = true;
			}

			destination = messenger.getDestination( destinationName );

			// Create the Object Message
			message = messenger.createTextMessage( msg );
			messenger.send( destination, message );
            messageId = message.getJMSMessageID();

			if (sendToAll)
			{

				if (sentToFallback == false)
				{
					messenger = null;

					try
					{

						if (destinationType == _DSTN_TOPIC)
							messenger = getMessengerManager().get( _TOPIC_FALLBACK );
						else
							messenger = getMessengerManager().get( _QUEUE_FALLBACK );

						sentToFallback = true;
                        if (messenger != null)
                        {
                            destination = messenger.getDestination( destinationName );

                            // Create the Text Message and send
                            message = messenger.createTextMessage( msg );
                            messenger.send( destination, message );
                        }
                    }
                    catch (JMSException e) {
                        _logger.error( e );
                    }

                }


                messenger = null;
                try
                {
                    if (destinationType == _DSTN_TOPIC)
                        messenger = getMessengerManager().get( _TOPIC_BATCH );
                    else
                        messenger = getMessengerManager().get( _QUEUE_BATCH );

                    if (messenger != null)
                    {
                        destination = messenger.getDestination( destinationName );

                        // Create the Text Message and send
                        message = messenger.createTextMessage( msg );
                        messenger.send( destination, message );
                    }

                }
                catch (JMSException e) {
                    _logger.error( e );
                }
            }

		}
		catch (JMSException e) {
			// Log it and throw the error
			_logger.error( e );
			throw e;
		}
        return messageId;
    }

	/**
	 * @param msg
	 * @param topicName
	 * @throws javax.jms.JMSException
	 * @roseuid 3F0A0CF10154
	 */
	public static final void sendTextToTopic(String msg, String topicName) throws JMSException
	{
		send( msg, _DSTN_TOPIC, topicName, false );
	}

	/**
	 * @param msg
	 * @param queueName
	 * @throws javax.jms.JMSException
	 * @roseuid 3F0A0CF1015E
	 */
	public static final String sendTextToQueue(String msg, String queueName) throws JMSException
	{
		return send( msg, _DSTN_QUEUE, queueName, false );
	}

	/**
	 * @return org.apache.commons.messenger.MessengerManager
	 * @throws javax.jms.JMSException
	 * @roseuid 3F0A0DC10347
	 */
	public static final MessengerManager getMessengerManager() throws JMSException
	{
		// Threading issues ??? But if MessengerManager is handling it, we should not bother
		if (_messengerManager == null) {
			URL url = MessengerManager.class.getClassLoader().getResource( _MESSENGER_XML_FILE );
			if (url == null) {
				// lets try the threads class loader
				ClassLoader loader = Thread.currentThread().getContextClassLoader();
				url = loader.getResource( _MESSENGER_XML_FILE );
			}
			if ( url != null ) {
				MessengerManager.configure( url.toString() );
				_messengerManager = MessengerManager.getInstance();
			}
			else {
				throw new JMSException( "No Messenger.xml configuration document found on the CLASSPATH. Could not initialise the default MessengerManager!!" );
			}
		}
		return _messengerManager;
	}


	/*
	 * The following methods will send messages to all primary and fallback queues/topics
	 */
	public static final void sendTextToClusterQueues(String msg, String queueName) throws JMSException
	{
		send( msg, _DSTN_QUEUE, queueName, true);
	}


	public static final void sendTextToClusterTopics(String msg, String topicName) throws JMSException
	{
		send( msg, _DSTN_TOPIC, topicName, true);
	}


	public static final void sendToClusterTopics(Serializable object, String topicName) throws JMSException
	{
		send( object, _DSTN_TOPIC, topicName, true );
	}

	public static final void sendToClusterQueues(Serializable object, String queueName) throws JMSException
	{
		send( object, _DSTN_QUEUE, queueName, true );
	}


}
