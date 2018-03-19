
package com.addval.springstruts;

import java.util.Comparator;
import org.apache.struts.util.LabelValueBean;

public class LabelValueBeanComparator implements Comparator
{

   public LabelValueBeanComparator()
   {
   }

   public int compare(Object arg0, Object arg1)
   {
		LabelValueBean l1 = (LabelValueBean)arg0;
		LabelValueBean l2 = (LabelValueBean)arg1;
		try{
			//First we parse the label as a double value and sort accordingly. If it throws excpn then
			//string compare is done.( because string sorting and double sorting are not equal )
			//eg: 	Number 100 > 20 = true
			//		but String "100" is < String "20"

			double lvb1 = Double.parseDouble( l1.getLabel() );
			double lvb2 = Double.parseDouble( l2.getLabel() );

			if(lvb1 < lvb2) return -1;
			if(lvb1 > lvb2) return +1;
			return 0;
		}
		catch(Exception e){
			//e.printStackTrace();
			return l1.getLabel().compareTo( l2.getLabel() );
			//NumberFormatException is thrown if string is parsed as double. Incase we will do a string compare and return
		}
   }

   public boolean equals(Object arg0)
   {
      return false;
   }

}
