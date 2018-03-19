package com.addval.hibernateutils;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.hibernate.HibernateException;
import org.hibernate.cfg.Environment;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.type.CustomType;
import org.hibernate.type.Type;
import org.hibernate.usertype.UserType;
import org.hibernate.usertype.UserVersionType;


/**
 * UserType for non-default TimeZone. Hibernate's built-in date, time and timestamp types assume that dates in the database are in Java's default time zone, implicitly. If this assumption is false (and you can't make it true by calling java.util.TimeZone.setDefault), you can configure Hibernate to
 * map to a UserType that does something else....
 * 
 * This code is taken from {@link http ://www.hibernate.org/100.html}. However, there are comments there which apply to us, namely that it would be useful to treat the activeOn property as a version field which is maintained by the database and it is not clear how to get that to work. Also the
 * thread {@link http ://forum.hibernate.org/viewtopic.php?t=980279} suggests handling this conversion in the POJO instead. However, at least one link (reference?) I found indicated a problem when the front-end and the middle layer lived in different time zones. I'm not sure that applies to use
 * since we don't allow the front end to set timestamps however, if London starts handling releases, there may be a problem displaying times in an easily understood form for the users.
 */
public abstract class TimeZoneGMT implements UserVersionType, UserType {
	private static final transient org.apache.log4j.Logger logger = com.addval.utils.LogMgr.getLogger("org.hibernate.usertype");
	
	// Use to cooerce raw sql queries return columns to appropriate types
	public static final Type TIMESTAMP = new CustomType(new TimestampType());

	/** the SQL type this type manages */
	protected static int[] SQL_TYPES = { Types.TIMESTAMP };

	/**
	 * @see net.sf.hibernate.UserType#sqlTypes()
	 */
	public int[] sqlTypes() {
		return SQL_TYPES;
	}

	/**
	 * @see net.sf.hibernate.UserType#equals(java.lang.Object, java.lang.Object)
	 */
	public boolean equals(Object x, Object y) {
		return (x == null) ? (y == null) : x.equals(y);
	}

	/**
	 * @see net.sf.hibernate.UserType#isMutable()
	 */
	public boolean isMutable() {
		return true;
	}

	
	/**
	 * Get a hashcode for the instance, consistent with persistence "equality"
	 */
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	/**
	 * Transform the object into its cacheable representation. At the very least this method should perform a deep copy if the type is mutable. That may not be enough for some implementations, however; for example, associations must be cached as identifier values. (optional operation)
	 * 
	 * @param value
	 *            the object to be cached
	 * @return a cachable representation of the object
	 * @throws HibernateException
	 */
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) deepCopy(value);
	}

	/**
	 * Reconstruct an object from the cacheable representation. At the very least this method should perform a deep copy if the type is mutable. (optional operation)
	 * 
	 * @param cached
	 *            the object to be cached
	 * @param owner
	 *            the owner of the cached object
	 * @return a reconstructed object from the cachable representation
	 * @throws HibernateException
	 */
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		return deepCopy(cached);
	}

	/**
	 * During merge, replace the existing (target) value in the entity we are merging to with a new (original) value from the detached entity we are merging. For immutable objects, or null values, it is safe to simply return the first parameter. For mutable objects, it is safe to return a copy of
	 * the first parameter. For objects with component values, it might make sense to recursively replace component values.
	 * 
	 * @param original
	 *            the value from the detached entity being merged
	 * @param target
	 *            the value in the managed entity
	 * @return the value to be merged
	 */
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return deepCopy(original);
	}

	public Object seed(SessionImplementor si) {
		return null;
	}

	public Object next(Object current, SessionImplementor si) {
		return null;
	}

	/**
	 * Like a Hibernate date, but using the UTC TimeZone (not the default TimeZone).
	 */
	public static class DateType extends TimeZoneGMT {
		protected static int[] SQL_TYPES_DATE = { Types.DATE };
		private GregorianCalendar _GMT_CALENDAR = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

		public Class<Date> returnedClass() {
			return Date.class;
		}
		/**
		 * @see net.sf.hibernate.UserType#sqlTypes()
		 */
		public int[] sqlTypes() {
			return SQL_TYPES_DATE;
		}

		/**
		 * @see net.sf.hibernate.UserType#deepCopy(java.lang.Object)
		 */
		public Object deepCopy(Object value) {
			return (value == null) ? null : new java.sql.Date(((Date) value).getTime());

		}

		/**
		 * @see net.sf.hibernate.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
		 */
		public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws SQLException {
			synchronized(DateType.class){
				return rs.getDate(names[0], _GMT_CALENDAR);
			}
		}

		/**
		 * @see net.sf.hibernate.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
		 */
		public void nullSafeSet(PreparedStatement st, Object value, int index) throws SQLException {
			if (!(value instanceof java.sql.Date)) {
				value = deepCopy(value);
			}
			synchronized(DateType.class){
				st.setDate(index, (java.sql.Date) value, _GMT_CALENDAR);
			}
			if (logger.isDebugEnabled()) {
				logger.debug("binding " + value + " to parameter: " + index);
			}
		}

		public int compare(Object x, Object y) {
			if (x == null && y == null) {
				return 0;
			}
			else if (x == null) {
				return 1;
			}
			else if (y == null) {
				return -1;
			}
			else {
				java.sql.Date c1 = (java.sql.Date) x;
				java.sql.Date c2 = (java.sql.Date) y;
				return c1.compareTo(c2);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
		 */
		public int hashCode(Object x) throws HibernateException {
			return ((java.sql.Date) x).hashCode();
		}

		public Object seed(SessionImplementor si) {
			return new java.sql.Date(System.currentTimeMillis());
		}

		public Object next(Object current, SessionImplementor si) {
			return new java.sql.Date(System.currentTimeMillis());
		}
	}

	/**
	 * Like a Hibernate time, but using the UTC TimeZone (not the default TimeZone).
	 */
	public static class TimeType extends TimeZoneGMT {
		protected static int[] SQL_TYPES_TIME = { Types.TIME };
		private GregorianCalendar _GMT_CALENDAR = new GregorianCalendar(TimeZone.getTimeZone("GMT"));

		public Class<Date> returnedClass() {
			return Date.class;
		}

		/**
		 * @see net.sf.hibernate.UserType#sqlTypes()
		 */
		public int[] sqlTypes() {
			return SQL_TYPES_TIME;
		}

		/**
		 * @see net.sf.hibernate.UserType#deepCopy(java.lang.Object)
		 */
		public Object deepCopy(Object value) {
			return (value == null) ? null : new java.sql.Time(((Date) value).getTime());
		}

		/**
		 * @see net.sf.hibernate.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
		 */
		public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws SQLException {
			synchronized(TimeType.class){
				return rs.getTime(names[0], _GMT_CALENDAR);	
			}
		}

		/**
		 * @see net.sf.hibernate.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
		 */
		public void nullSafeSet(PreparedStatement st, Object value, int index) throws SQLException {
			if (!(value instanceof java.sql.Time)) {
				value = deepCopy(value);
			}
			synchronized(TimeType.class){
				st.setTime(index, (java.sql.Time) value, _GMT_CALENDAR);	
			}
			if (logger.isDebugEnabled()) {
				logger.debug("binding " + value + " to parameter: " + index);
			}
		}

		public int compare(Object x, Object y) {
			if (x == null && y == null) {
				return 0;
			}
			else if (x == null) {
				return 1;
			}
			else if (y == null) {
				return -1;
			}
			else {
				java.sql.Time c1 = (java.sql.Time) x;
				java.sql.Time c2 = (java.sql.Time) y;
				return c1.compareTo(c2);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
		 */
		public int hashCode(Object x) throws HibernateException {
			return ((java.sql.Time) x).hashCode();
		}

		public Object seed(SessionImplementor si) {
			return new java.sql.Time(System.currentTimeMillis());
		}

		public Object next(Object current, SessionImplementor si) {
			return new java.sql.Time(System.currentTimeMillis());
		}

	}

	/**
	 * Like a Hibernate timestamp, but using the UTC TimeZone (not the default TimeZone).
	 */
	public static class TimestampType extends TimeZoneGMT {
		private GregorianCalendar _GMT_CALENDAR = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		public Class<Timestamp> returnedClass() {
			return Timestamp.class;
		}
		/**
		 * @see net.sf.hibernate.UserType#deepCopy(java.lang.Object)
		 */
		public Object deepCopy(Object value) {
			if (value == null) {
				return null;
			}
			else if (value instanceof java.util.Date) {
				value = new Timestamp( ((java.util.Date) value).getTime() );
			}
			java.sql.Timestamp ots = (java.sql.Timestamp) value;
			java.sql.Timestamp ts = new java.sql.Timestamp(ots.getTime());
			ts.setNanos(ots.getNanos());
			return ts;
		}

		/**
		 * @see net.sf.hibernate.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
		 */
		public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws SQLException {
			synchronized(TimestampType.class){
				return rs.getTimestamp(names[0], _GMT_CALENDAR);
			}
		}

		/**
		 * @see net.sf.hibernate.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
		 */

		public void nullSafeSet(PreparedStatement st, Object value, int index) throws SQLException {
			if (!(value instanceof java.sql.Timestamp)) {
				value = deepCopy(value);
			}
			synchronized(TimestampType.class){
				st.setTimestamp(index, (java.sql.Timestamp) value, _GMT_CALENDAR);	
			}
			if (logger.isDebugEnabled()) {
				logger.debug("binding " + value + " to parameter: " + index);
			}
		}

		public int compare(Object x, Object y) {
			if (x == null && y == null) {
				return 0;
			}
			else if (x == null) {
				return 1;
			}
			else if (y == null) {
				return -1;
			}
			else {
				Timestamp c1 = (Timestamp) x;
				Timestamp c2 = (Timestamp) y;
				return c1.compareTo(c2);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
		 */
		public int hashCode(Object x) throws HibernateException {
			return ((Timestamp) x).hashCode();
		}

		public Object seed(SessionImplementor si) {
			return new Timestamp(System.currentTimeMillis());
		}

		public Object next(Object current, SessionImplementor si) {
			return new Timestamp(System.currentTimeMillis());
		}
	}

	public static class CalendarType extends TimeZoneGMT {
		private GregorianCalendar _GMT_CALENDAR = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		public Class<Calendar> returnedClass() {
			return Calendar.class;
		}

		/**
		 * @see net.sf.hibernate.UserType#deepCopy(java.lang.Object)
		 */
		public Object deepCopy(Object value) {
			if (value == null) {
				return null;
			}
			synchronized(CalendarType.class){
				Calendar c = (Calendar) _GMT_CALENDAR.clone();
				c.setTimeInMillis(((Calendar) value).getTimeInMillis());
				return c;
			}
		}

		/**
		 * @see net.sf.hibernate.UserType#nullSafeGet(java.sql.ResultSet, java.lang.String[], java.lang.Object)
		 */
		public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws SQLException {
			synchronized(CalendarType.class){
				Calendar cal = (Calendar) _GMT_CALENDAR.clone();
				Timestamp ts = rs.getTimestamp(names[0], cal);
				if (ts == null || rs.wasNull()) {
					return null;
				}
				if (Environment.jvmHasTimestampBug()) {
					cal.setTime(new Date(ts.getTime() + ts.getNanos() / 1000000));
				}
				else {
					cal.setTime(ts);
				}
				return cal;
			}
		}

		/**
		 * @see net.sf.hibernate.UserType#nullSafeSet(java.sql.PreparedStatement, java.lang.Object, int)
		 */

		public void nullSafeSet(PreparedStatement st, Object value, int index) throws SQLException {
			synchronized(CalendarType.class){
				if (value == null) {
					st.setNull(index, Types.TIMESTAMP);
				}
				else {
					Timestamp t = new Timestamp(((Calendar) value).getTimeInMillis());
					st.setTimestamp(index, t, _GMT_CALENDAR);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("binding " + value + " to parameter: " + index);
				}
			}
		}

		/*
		 * (non-Javadoc)
		 * @see org.hibernate.usertype.UserType#equals(java.lang.Object, java.lang.Object)
		 */
		public boolean equals(Object x, Object y) {
			if (x == y) {
				return true;
			}
			if (x == null || y == null) {
				return false;
			}
			Calendar calendar1 = (Calendar) x;
			Calendar calendar2 = (Calendar) y;
			return calendar1.getTimeInMillis() == calendar2.getTimeInMillis();
		}

		public int compare(Object x, Object y) {
			if (x == null && y == null) {
				return 0;
			}
			else if (x == null) {
				return 1;
			}
			else if (y == null) {
				return -1;
			}
			else {
				Calendar c1 = (Calendar) x;
				Calendar c2 = (Calendar) y;
				return c1.compareTo(c2);
			}
		}

		/*
		 * (non-Javadoc)
		 * @see org.hibernate.usertype.UserType#hashCode(java.lang.Object)
		 */
		public int hashCode(Object x) throws HibernateException {
			return ((Calendar) x).hashCode();
		}

		public Object seed(SessionImplementor si) {
			synchronized(CalendarType.class){
				Calendar cal = (Calendar) _GMT_CALENDAR.clone();
				cal.setTimeInMillis(System.currentTimeMillis());
				return cal;
			}
		}

		public Object next(Object current, SessionImplementor si) {
			synchronized(CalendarType.class){
				Calendar cal = (Calendar) _GMT_CALENDAR.clone();
				cal.setTimeInMillis(System.currentTimeMillis());
				return cal;
			}
		}
	}
}
