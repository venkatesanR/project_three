/**
 * Copyright
 * AddVal Technology Inc.
 */

package com.addval.utils;

import java.util.*;
import java.util.regex.*;

/**
 * This utility class provides a means for recursively resolving property "value" strings that contain delimited
 * references to other properties.
 * <p>
 * The different constructors provide different ways to control what delimiters
 * should be used: DEFAULT delimiters, specified delimiters, or delimiters defined in a Properties object.
 * <p>
 * Two forms of resolveReferences are available: one for resolving references in a single specified String,
 * and another for recursively resolving all references in a specified Properties object.
 * <p>
 * Example usage:
 * <pre>
 *
 *		Properties props = new Properties();
 *		props.setProperty("PropertyReferenceResolver.leftDelim", "\\$\\{");	// Note: this is same as DEFAULT leftDelim
 *		props.setProperty("PropertyReferenceResolver.rightDelim", "\\}");	// Note: this is same as DEFAULT rightDelim
 *
 *		props.setProperty("MyProjectHome", "C:/Projects/MyProject");
 *		props.setProperty("MyProjectLib", "${MyProjectHome}/lib");
 *		props.setProperty("VendorJar", "${MyProjectLib}/SomeVendorV23SP2.jar");
 *		props.setProperty("AnUnresolvableReference", "${SomeOtherProjectLib}/SomeOtherVendor.jar");
 *
 *		System.out.println("Test input properties = " + props);
 *
 *		PropertyReferenceResolver resolver = new PropertyReferenceResolver(props);
 *		props = resolver.resolveReferences( props );
 *
 *		System.out.println("Resolved properties = " + props);
 * </pre>
 */
public class PropertyReferenceResolver
{
	private static final String _DEFAULT_LEFT_DELIM = "\\$\\{";
	private static final String _DEFAULT_RIGHT_DELIM = "\\}";

	private String _leftDelim;
	private String _rightDelim;
	private Pattern _referenceMatchingPattern;

	/**
	 * Used by constructors; stores the specified delimeters and constructs a Pattern used by matchesReferencePattern.
	 */
	private void setDelims( String leftDelim, String rightDelim)
	{
		_leftDelim = leftDelim;
		_rightDelim = rightDelim;
		// Use a Pattern that requires at least one character to appear between the left and right delimiters
		_referenceMatchingPattern = Pattern.compile( ".*" + leftDelim + ".+" + rightDelim + ".*" );
	}

	/**
	 * Constructs an instance using the class DEFAULT values for left and right delimiters.
	 */
	public PropertyReferenceResolver()
	{
		this( _DEFAULT_LEFT_DELIM, _DEFAULT_RIGHT_DELIM);
	}

	/**
	 * Constructs an instance using the specified left and right delimiters.
	 */
	public PropertyReferenceResolver( String leftDelim, String rightDelim)
	{
		setDelims( leftDelim, rightDelim);
	}

	/**
	 * Constructs an instance using delimiter values specified in the input Properties with property names
	 * "PropertyReferenceResolver.leftDelim" and "PropertyReferenceResolver.rightDelim".
	 * If either/both do not exist in the input Properties, the corresponding class DEFAULT delimter is used.
	 */
	public PropertyReferenceResolver( Properties properties)
	{
		String leftDelim = properties.getProperty("PropertyReferenceResolver.leftDelim");
		if (leftDelim == null)
		{
			leftDelim = _DEFAULT_LEFT_DELIM;
		}

		String rightDelim = properties.getProperty("PropertyReferenceResolver.rightDelim");
		if (rightDelim == null)
		{
			rightDelim = _DEFAULT_RIGHT_DELIM;
		}

		setDelims( leftDelim, rightDelim);
	}

	/**
	 * Loops over entries in the specified Properties, resolves property value references whereever possible, and
	 * returns the (potentially) modified Properties object.
	 */
	public Properties resolveReferences( Properties properties)
	{
		boolean anyChangesMade = true;
		while (anyChangesMade)
		{
			anyChangesMade = false;
			for (Enumeration propNameEnum = properties.propertyNames(); propNameEnum.hasMoreElements(); )
			{
				String propName = (String) propNameEnum.nextElement();
				String originalPropValue = properties.getProperty( propName );
				String resolvedPropValue = resolveReferences( originalPropValue, properties );
				if (!resolvedPropValue.equals(originalPropValue))
				{
					properties.setProperty( propName, resolvedPropValue );
					anyChangesMade = true;
				}
			}
		}
		return properties;
	}

	/**
	 * Returns the input propertyValueString, but after resolving property value references whereever possible.
	 * When a property value reference in the string cannot be resolved, it is left "as is".
	 */
	public String resolveReferences( String propertyValueString, Properties properties)
	{
		String resolvedString = propertyValueString;

		if (matchesReferencePattern(propertyValueString))
		{
			// Get the left-most referenced property name, and the text to its left and right.
			String[] splitString = splitIn3( propertyValueString );
			String leftOfReference = splitString[0];			// = "" if nothing to the left
			String referencedPropertyName = splitString[1];		// delimiters are omitted
			String rightOfReference = splitString[2];			// = "" if nothing to the right

			// Start a new StringBuffer to hold the resolvedString as we build it up.
			StringBuffer sb = new StringBuffer();

			// Attempt to resolve the reference.  If resolved, substitute the resolved value; else restore to original (unresolved) string.
			String resolvedValue = properties.getProperty( referencedPropertyName );
			if (resolvedValue != null)
			{
				sb.append( leftOfReference );
				sb.append( resolvedValue );
			}
			else if (rightOfReference.equals( "" ))
			{
				sb.append( propertyValueString );
			}
			else 
			{
				sb.append( propertyValueString.substring(0, propertyValueString.lastIndexOf(rightOfReference) ) );
			}

			// If the part to the right of reference contains another reference, recurse on it.
			if (matchesReferencePattern(rightOfReference))
			{
				rightOfReference = resolveReferences( rightOfReference, properties );
			}

			sb.append( rightOfReference );
			resolvedString = sb.toString();
		}

		return resolvedString;
	}

	/**
	 * Returns true IFF the string contains an apparent property value "reference"
	 */
	private boolean matchesReferencePattern( String propertyValueString )
	{
		return propertyValueString != null && _referenceMatchingPattern.matcher(propertyValueString).matches();
	}

	/**
	 * Returns a String[3] where:
	 * String[0] contains the text that appears to the left of the first occurence of _leftDelim; will be "" if none
	 * String[1] contains the text that appears between the first occurences of _leftDelim and _rightDelim, without the delimeters themselves
	 * String[2] contains the text that appears to the right of the first occurence of _rightDelim; will be "" if none
	 */
	private String[] splitIn3(String aString)
	{
		String[] splits = aString.split(_leftDelim,2);
		String leftOfReference = splits[0];
		String refPlusTheRest = splits[1];

		splits = refPlusTheRest.split(_rightDelim,2);
		String theRef = splits[0];
		String rightOfReference = splits[1];

		String[] results = new String[3];
		results[0] = leftOfReference;
		results[1] = theRef;
		results[2] = rightOfReference;

		return results;
	}

	/**
	 * Formats properties for display.
	 */
	private static String formatProperties( Properties props ) {
		StringBuffer sb = new StringBuffer();
		Enumeration enumeration = props.propertyNames();
		while (enumeration.hasMoreElements()) {
			String name  = (String) enumeration.nextElement();
			String value = props.getProperty( name );
			sb.append("\n\t").append( (name+"                                                  ").substring(0,50) ).append("= ").append(value);
		}
		return sb.toString();
	}

	/**
	 * Demonstrates usage.
	 */
	public static void main (String[] args)
	{
		System.out.println("PropertyReferenceResolver.main(): ");

		Properties props = new Properties();
		props.setProperty("PropertyReferenceResolver.leftDelim", "\\$\\{");
		props.setProperty("PropertyReferenceResolver.rightDelim", "\\}");

		props.setProperty("MyProjectHome", "C:/Projects/MyProject");
		props.setProperty("MyProjectLib", "${MyProjectHome}/lib/ext");
		props.setProperty("VendorJar", "${MyProjectLib}/SomeVendorV23SP2.jar");
		props.setProperty("AnUnresolvableReference", "${SomeOtherProjectLib}/SomeOtherVendor.jar");

		System.out.println("Test input properties = " + formatProperties(props));

		PropertyReferenceResolver resolver = new PropertyReferenceResolver(props);
		props = resolver.resolveReferences( props );

		System.out.println("Resolved properties = " + formatProperties(props));
	}

}