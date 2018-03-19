/**
 *
 */
package com.addval.utils;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;

import com.addval.ui.OrderByEnum;
import com.addval.ui.SortableCol;

public class AVSearchComparator<T extends Object> implements AVComparator<T>, Serializable {
	private static final long serialVersionUID = 1L;
	public List<String> subTotalColumns = null;
	public HashMap<String, String> sortMap = null;
	public List<SortableCol> sortableColumns = null;
	public List<SortableCol> defaultSortableColumns = null;
	private HashMap<String, PropertyDescriptor> descriptorsMap = null;

	public AVSearchComparator() {
		descriptorsMap = new HashMap<String, PropertyDescriptor>();
	}

	@Override
	public List<String> getSubTotalColumns() {
		return new ArrayList<String>();
	}

	@Override
	public HashMap<String, String> getSortMap() {
		return sortMap;
	}

	@Override
	public void setSortBy(String sortBy) {
		// TODO : Override in Overriding class
	}

	@Override
	public boolean isSubTotalEnabled() {
		return false;
	}

	@Override
	public List<SortableCol> getSortableColumns() {
		return sortableColumns;
	}

	@Override
	public void setSortableColumns(List<SortableCol> sortableColumns) {
		this.sortableColumns = sortableColumns;
	}

	@Override
	public List<SortableCol> getDefaultSortableColumns() {
		return defaultSortableColumns;
	}

	@Override
	public void setDefaultSortableColumns(List<SortableCol> defaultSortableColumns) {
		this.defaultSortableColumns = defaultSortableColumns;
	}

	/* (non-Javadoc)
	 * @see com.addval.utils.AVComparator#multiSort(java.util.List)
	 */
	@Override
	public Collection<T> multiSort(List<T> source) {
		if (source.size() > 0) {
			T clazz = source.get(0);
			PropertyDescriptor[] descriptors = PropertyUtils.getPropertyDescriptors(clazz.getClass());
			descriptorsMap = new HashMap<String, PropertyDescriptor>();
			for (SortableCol gridCol : sortableColumns) {
				for (PropertyDescriptor descriptor : descriptors) {
					if (gridCol.getColName().equalsIgnoreCase(descriptor.getName())) {
						descriptorsMap.put(gridCol.getColName(), descriptor);
					}
				}
			}
			Collections.sort(source, this);
		}
		return source;
	}

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(T o1, T o2) {
		Object attribute1 = null;
		Object attribute2 = null;
		Class<?> propertyType = null;
		Method invokeMethod = null;
		OrderByEnum orderByEnum = null;
		int orderBy = -1;
		PropertyDescriptor descriptor = null;
		for (SortableCol gridCol : sortableColumns) {
			if (descriptorsMap.containsKey(gridCol.getColName())) {
				descriptor = descriptorsMap.get(gridCol.getColName());
				propertyType = descriptor.getPropertyType();
				invokeMethod = descriptor.getReadMethod();
				orderByEnum = gridCol.getOrderByEnum();
				orderBy = (orderByEnum == OrderByEnum.ASC ? 1 : -1);
				int returnVal = 0;
				try {
					attribute1 = invokeMethod.invoke(o1);
					attribute2 = invokeMethod.invoke(o2);
				}
				catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				returnVal = nullCheck(attribute1, attribute2);
				if (returnVal == 2 && !propertyType.isArray()) {
					if (String.class == propertyType) {
						String val1 = (String) attribute1;
						String val2 = (String) attribute2;
						int val = val1.compareTo(val2);
						returnVal = val * orderBy;
					}
					else if (Date.class == propertyType) {
						Date date1 = (Date) attribute1;
						Date date2 = (Date) attribute2;
						returnVal = date1.compareTo(date2) * orderBy;
					}
					else if (Double.class == propertyType || attribute1 instanceof Double) {
						returnVal = MathUtl.compare((Double) attribute1, (Double) attribute2) * orderBy;
					}
					else if (Boolean.class == propertyType || attribute1 instanceof Boolean) {
						Boolean b1 = (Boolean) attribute1;
						Boolean b2 = (Boolean) attribute2;
						returnVal = compareBoolean(b1, b2) * orderBy;
					}
					else if (Integer.class == propertyType || attribute1 instanceof Integer) {
						Integer val1 = (Integer) attribute1;
						Integer val2 = (Integer) attribute2;
						returnVal = MathUtl.compare(val1, val2) * orderBy;
					}
					else if (Long.class == propertyType || attribute1 instanceof Long) {
						Long val1 = (Long) attribute1;
						Long val2 = (Long) attribute2;
						returnVal = MathUtl.compare(val1, val2) * orderBy;
					}
					else if (Byte.class == propertyType || attribute1 instanceof Byte) {
						Byte val1 = (Byte) attribute1;
						Byte val2 = (Byte) attribute2;
						returnVal = MathUtl.compare(val1, val2) * orderBy;
					}
					else if (Short.class == propertyType || attribute1 instanceof Short) {
						Short val1 = (Short) attribute1;
						Short val2 = (Short) attribute2;
						returnVal = MathUtl.compare(val1, val2) * orderBy;
					}
					else if (Long.class == propertyType || attribute1 instanceof Long) {
						Long val1 = (Long) attribute1;
						Long val2 = (Long) attribute2;
						returnVal = MathUtl.compare(val1, val2) * orderBy;
					}
					else if (Float.class == propertyType || attribute1 instanceof Float) {
						Float val1 = (Float) attribute1;
						Float val2 = (Float) attribute2;
						returnVal = MathUtl.compare(val1, val2) * orderBy;
					}
					else if (Character.class == propertyType || attribute1 instanceof Character) {
						Character val1 = (Character) attribute1;
						Character val2 = (Character) attribute2;
						returnVal = val1.compareTo(val2);
					}
					else if (BigDecimal.class == propertyType) {
						BigDecimal val1 = (BigDecimal) attribute1;
						BigDecimal val2 = (BigDecimal) attribute2;
						val1.compareTo(val2);
					}
					else {
						/*
						 *  If its not a Pre-defined Class, code tries to look for a compare method in the class and invokes that.
						*/
						Method[] declaredMethods = propertyType.getMethods();
						for (Method method : declaredMethods) {
							String methodName = method.getName();
							if ("compare".equalsIgnoreCase(methodName)) {
								Object[] args = new Object[2];
								args[0] = attribute1;
								args[1] = attribute2;
								try {
									method.invoke(o1, args);
								}
								catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								catch (InvocationTargetException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}

					}
				}
				if (returnVal != 0) {
					return returnVal;
				}
			}
		} // end of outer for-loop
		return 0;
	}

	private int compareBoolean(Boolean b1, Boolean b2) {
		if (b1 == b2) {
			return 0;
		}
		if (b1 == true) {
			return 1;
		}
		return -1;
	}

	private int nullCheck(Object obj1, Object obj2) {
		if (obj1 == null && obj2 == null) {
			return 0;
		}
		if (obj1 == null && obj2 != null) {
			return -1;
		}
		if (obj1 != null && obj2 == null) {
			return 1;
		}
		return 2;
	}

}
