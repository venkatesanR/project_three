package com.techmania.utils;

import java.util.Collections;
import java.util.List;

/**
 * 
 * @author Yuvaraj
 *
 */
public class GenUtl {
	/**
	 * Creates a new instance of GenUtl
	 */
	public GenUtl()
	{

	}
	
	/**
	 * Method will perform sorting operation based on the sortOrder and MethodName
	 * 
	 * Example 
	 *        List of Employee Objects (input)
	 *          Employee Id       Employee Name   Employee Salary
	 *             1               Ashwin          150000.00
	 *             2               Yuvaraj         2500000.00
	 *  
	 *  if sortOrder = DESC methodName = Employee Salary
	 *             
	 *         List of Employee Objects (output)    
	 *          Employee Id       Employee Name   Employee Salary
	 *             2               Yuvaraj          2500000.00
	 *             1               Ashwin           150000.00
	 *             
	 * @param methodName
	 * @param sortOrder
	 * @param vectorTobeSorted
	 * @throws Exception
	 */
	public static void performSort( String methodName, String sortOrder, List vectorTobeSorted ) throws Exception
	{
		if (methodName != null && methodName.trim().length() != 0 && vectorTobeSorted != null && ! vectorTobeSorted.isEmpty())
		{
			if (sortOrder == null || sortOrder.trim().length() == 0)
				sortOrder = "ASC";

			ObjectComparator comparator = new ObjectComparator( methodName , sortOrder );
			Collections.sort( vectorTobeSorted , comparator );
		}
	}
}
