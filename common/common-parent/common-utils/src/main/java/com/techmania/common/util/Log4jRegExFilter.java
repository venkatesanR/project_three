package com.techmania.common.util;

import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

public class Log4jRegExFilter extends Filter {

	private boolean acceptOnMatch = false;
	private RE matcher = null;

	public void setRegEx(String regEx) throws RESyntaxException {
		matcher = new RE(regEx);
	}

	public void setAcceptOnMatch(boolean acceptOnMatch) {
		this.acceptOnMatch = acceptOnMatch;
	}

	public int decide(LoggingEvent event) {
		String msg = event.getRenderedMessage();
		if (msg == null || matcher == null) {
			return Filter.NEUTRAL;
		}
		if (matcher.match(msg)) {
			return acceptOnMatch ? Filter.ACCEPT : Filter.DENY;
		}
		return Filter.NEUTRAL;
	}
}
