/**
 *
 * Subclass from this to create format objects for the ColumnTag
 *
 **/
package com.addval.taglib;

public abstract class ColumnDecorator extends com.addval.taglib.TableTag.Decorator
{
   public ColumnDecorator()
   {
      super();
   }

   public abstract String decorate( Object columnValue );
}
