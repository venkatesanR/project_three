package com.addval.jaxbutils.adapters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.addval.jaxbutils.InvalidDate;

public class ISODateAdapter extends XmlAdapter<String, Date> {
	private SimpleDateFormat dateFormat = null;

	@Override
	public String marshal(Date date) throws Exception {
		if (date != null) {
			// System.out.println("ISODateAdapter : marshal " + getDateFormat().format(date));
			return getDateFormat().format(date);
		}
		return null;
	}

	@Override
	public Date unmarshal(String value) throws Exception {
		try {
			// System.out.println("ISODateAdapter : unmarshal " + getDateFormat().format(getDateFormat().parse(value)));
			return getDateFormat().parse(value);
		}
		catch (Exception ex) {
			return new InvalidDate(value);
		}
	}

	private SimpleDateFormat getDateFormat() {
		if (dateFormat == null) {
			dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"); // ISO 8601 format
			dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
		}
		return dateFormat;
	}
}
