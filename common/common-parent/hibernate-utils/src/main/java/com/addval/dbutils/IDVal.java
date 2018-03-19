package com.addval.dbutils;


public class IDVal {
   private String column_name;
   private int _id = 0;

   public IDVal() {}


   public IDVal(String col_name, int v) {
      column_name = col_name;
      _id = v;
   }

   public IDVal(String col_name) {
      column_name = col_name;
   }

   public IDVal(int v) {
      _id = v;
   }


   public int getId() {
      return _id;
   }


   public void setId(int v) {
      _id = v;
   }

   public String getColName() {
      return column_name;
   }


   public void setColName(String col_name) {
      column_name = col_name;
   }


   public String toString() {
      return "" + _id;
   }
}
