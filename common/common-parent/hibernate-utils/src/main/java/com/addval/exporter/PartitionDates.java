/* Copyright AddVal Technology Inc. */

package com.addval.exporter;

import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import com.addval.utils.CommandLine;

import java.text.SimpleDateFormat;

import java.io.*;

/**
 * Exports Data from the tables in MPS Format
 */
public final class PartitionDates
{
	/**
	 * @exception
	 * @roseuid 3AA677E0005B
	 */
	private static SimpleDateFormat _formatter  = new SimpleDateFormat ("dd-MMM-yyyy");

	public PartitionDates()
	{
	}

	public static void printDate(PrintWriter writer, Date d) throws Exception {
		writer.print( _formatter.format(d) );
		writer.print( "\t" );
	}

	public static void main (String [] args) throws Exception {

		System.out.println( "Start time:" + new Date() );

		Date inputDate = null;
		int endOffset = 0;
		int beginOffset = 0;
		String outputFile = null;
		int partitionCount = 4;

		boolean limitBeginDate = false;
		boolean limitEndDate = false;

		//try {
			CommandLine cl = new CommandLine( args );

			if ( cl.hasFlag( "d" ) ) {
				inputDate = _formatter.parse( cl.getFlagArgument( "d" ) );
			}

			if ( cl.hasFlag( "e" ) ) {
				endOffset = Integer.parseInt( cl.getFlagArgument( "e" ) );
			}
			else if ( cl.hasFlag( "E" ) ) {
				endOffset = Integer.parseInt( cl.getFlagArgument( "E" ) );
				limitEndDate = true;
			}

			if ( cl.hasFlag( "b" ) ) {
				beginOffset = Integer.parseInt( cl.getFlagArgument( "b" ) );
			}
			else if ( cl.hasFlag( "B" ) ) {
				beginOffset = Integer.parseInt( cl.getFlagArgument( "B" ) );
				limitBeginDate = true;
			}

			if ( cl.hasFlag( "o" ) ) {
				outputFile = cl.getFlagArgument( "o" ) ;
			}

			if ( cl.hasFlag( "p" ) ) {
				partitionCount = Integer.parseInt( cl.getFlagArgument( "p" ) );
			}

			Calendar cal = new GregorianCalendar();

			cal.setTime( inputDate );
			cal.add( Calendar.DATE, endOffset );
			Date endDate = new Date( cal.getTime().getTime() );

			cal.setTime( inputDate );
			cal.add( Calendar.DATE, -beginOffset );
			Date beginDate = new Date( cal.getTime().getTime() );

			int elapsedDays = Math.round( ( endDate.getTime() - beginDate.getTime() ) / (1000 * 60 * 60 * 24) );

			int partitionSize = Math.round( elapsedDays / partitionCount );

			System.out.println( "Date range:" + _formatter.format(beginDate) + "-" + _formatter.format(endDate) + ":" + elapsedDays + ":" + partitionSize);

			PrintWriter partWriter 	= new PrintWriter( new BufferedWriter( new FileWriter( outputFile ) ) );

			if (limitBeginDate) {
				printDate( partWriter, beginDate );
			}

			for (int i=0; i<partitionCount-1; i++) {
				cal.add( Calendar.DATE, partitionSize );
				printDate( partWriter, cal.getTime() );
			}

			if (limitEndDate) {
				printDate( partWriter, endDate );
			}

			partWriter.close();

			System.out.println( "End time:" + new Date() );

		//}
		//catch( Exception e ) {
		//	e.printStackTrace();
		//}
	}
}
