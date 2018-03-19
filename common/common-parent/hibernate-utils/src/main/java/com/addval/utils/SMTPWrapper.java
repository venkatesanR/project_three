/* AddVal Technology Inc. */

package com.addval.utils;

import com.addval.utils.CnfgFileMgr;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.Transport;
import java.util.Vector;
import java.sql.Statement;
import java.sql.ResultSet;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.BodyPart;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Date;
import java.util.Properties;
import java.util.Enumeration;
import java.net.InetAddress;
import javax.activation.FileDataSource;
import javax.activation.DataHandler;

public class SMTPWrapper
{
   private String _mailhost;

   /**
   @roseuid 39E75FFD025F
   */
   public SMTPWrapper(String mailhost)
   {
	   _mailhost = mailhost;
   }

   /**
   @roseuid 39E765A2039D
   */
   public int send(Vector to, Vector cc, Vector file, String message, String subject, StringBuffer errorMsg, String from)
   {
	   	//System.out.println( "SMTPWrapper.send() : traceEnter()" );

		try {
			Properties props = System.getProperties();
			Enumeration get_to;
			if (_mailhost != null)
			props.put("mail.smtp.host", _mailhost);

			Session session = Session.getDefaultInstance(props, null);
			//System.out.println( "SMTPWrapper.send() : After Creating a Session" );

			MimeMessage msg = new MimeMessage(session);
			if (from != null)
			msg.setFrom(new InternetAddress(from));
			else
			msg.setFrom();

			//System.out.println( "SMTPWrapper.send() : After Setting the from Address" );

			InternetAddress[] addr_to = new InternetAddress[to.size()];
			get_to = to.elements();
			int counter = 0;

			while( get_to.hasMoreElements())
				addr_to[counter++] = new  InternetAddress( get_to.nextElement().toString() );

			msg.setRecipients(Message.RecipientType.TO, addr_to);
			//System.out.println( "SMTPWrapper.send() : After Setting the Recipients" );

			if ( cc != null)  {

				InternetAddress[] addr_cc = new InternetAddress[cc.size()];
				Enumeration get_cc = cc.elements();
				int counter1 = 0;
				while( get_cc.hasMoreElements())
					addr_cc[counter1++] = new  InternetAddress( get_cc.nextElement().toString() );

				msg.setRecipients(Message.RecipientType.CC, addr_cc);
			}

			if ( subject != null )
				msg.setSubject(subject);


			//first part of msg

			MimeBodyPart mbp_one = new MimeBodyPart();

			Multipart mp = new MimeMultipart();

			if ( message != null ) {

				mbp_one.setText(message);
				mp.addBodyPart(mbp_one);
			}

			if ( file != null) {

				Enumeration get_files;
				get_files = file.elements();
				String hold_file;
				String fileName;
				FileDataSource fds;
				MimeBodyPart mbp_name;
				while( get_files.hasMoreElements() ) {
					hold_file = get_files.nextElement().toString();
					fds = new FileDataSource( hold_file);
					mbp_name = new MimeBodyPart();
					mbp_name.setDataHandler( new DataHandler(fds));
					if (hold_file.lastIndexOf("\\") != -1)
						fileName = hold_file.substring(hold_file.lastIndexOf("\\")+1);
					else {
						if (hold_file.lastIndexOf("/") != -1)
							fileName = hold_file.substring(hold_file.lastIndexOf("/")+1);
						else
							fileName = hold_file;
					}

					mbp_name.setFileName(fileName);
					mp.addBodyPart(mbp_name);
				}
			}//end if file != null

			msg.setContent(mp);
			//System.out.println( "SMTPWrapper.send() : After Setting the Content" );

			//msg.setHeader("X-Mailer", mailer);
			msg.setSentDate(new Date());

			//System.out.println( "SMTPWrapper.send() : Before Sending the Email" );
			Transport.send(msg);
			//System.out.println( "SMTPWrapper.send() : After Sending the Email" );

			//System.out.println( "SMTPWrapper.send() : traceExit()" );
			return 0;
		}
		catch (Exception e)
		{
			ByteArrayOutputStream b =  new ByteArrayOutputStream() ;
			PrintStream ps = new PrintStream (b);
			e.printStackTrace(ps);
			ps.close();
			errorMsg.append( b.toString() );
			//System.out.println( errorMsg.toString() );
			return -1;
		}
   }

   /**
   @roseuid 39E765C601E6
   */
   public int send(String to, Vector cc, Message mesg, String message, String subject, StringBuffer errorMsg, String from)
   {

		try
			 {
				Properties props = System.getProperties();
				Enumeration get_to;
				if (_mailhost != null)
				  props.put("mail.smtp.host", _mailhost);

				Session session = Session.getDefaultInstance(props, null);
				MimeMessage msg = new MimeMessage(session);

				if (from != null)
				  msg.setFrom(new InternetAddress(from));
				else
				  msg.setFrom();

				InternetAddress[] addr_to = new InternetAddress[1];

				if ( to.indexOf(",") != -1)
				{
					int startPos = 0;
					int currentPos = 0;
					int index = 0;
					String toString;
					while (to.indexOf(",",startPos) != -1)
					{
						currentPos = to.indexOf(",",startPos);
						toString=to.substring(startPos,currentPos);
						addr_to[index]=new InternetAddress(toString);
						index++;
						startPos =currentPos + 1;
					}
					addr_to[index]=new InternetAddress(to.substring(startPos));
				}
				else
				{
					addr_to[0]=new InternetAddress(to);
				}
				msg.setRecipients(Message.RecipientType.TO, addr_to);

				if ( cc != null)
				{
				 InternetAddress[] addr_cc = new InternetAddress[cc.size()];
				 Enumeration get_cc = cc.elements();
				 int counter1 = 0;
				 while( get_cc.hasMoreElements())
					addr_cc[counter1++] = new  InternetAddress( get_cc.nextElement().toString() );

				 msg.setRecipients(Message.RecipientType.CC, addr_cc);
				}

				if ( subject != null )
				   msg.setSubject(subject);

				Multipart mp = new MimeMultipart();
				MimeBodyPart mbp_one;
				if (mesg.getContent() instanceof Multipart)
				{
					if ( message != null )
					{
						mbp_one = new MimeBodyPart();
						mbp_one.setText(message);
						mp.addBodyPart(mbp_one);
					}
					Multipart mpPrev=(Multipart)mesg.getContent();
					int count = mpPrev.getCount();
					for (int i = 0; i < count; i++)
						mp.addBodyPart(mpPrev.getBodyPart(i));
				}
				else
				{
					if (mesg.getContent() instanceof String)
					{
						mbp_one = new MimeBodyPart();
						if (message != null)
							message += (String)mesg.getContent();
						else
							message = (String)mesg.getContent();
						mbp_one.setText(message);
						mp.addBodyPart(mbp_one);
					}
					else
					{
						errorMsg.append("MIME PART : " + mesg.getContentType()+ " not handled");
						return -1;
					}
				}

				msg.setContent(mp);
				msg.setSentDate(new Date());

				Transport.send(msg);

				return 0;
			}
			catch (Exception e)
			{
				ByteArrayOutputStream b =  new ByteArrayOutputStream() ;
				PrintStream ps = new PrintStream (b);
				e.printStackTrace(ps);
				ps.close();
				errorMsg.append(b.toString());
				return -1;
			}
   }
}