package com.techland.training.krishna;


    import java.io.*;
    import java.util.*;
    import java.text.*;
    import java.math.*;
    import java.util.regex.*;

    public class Introduction_Ifelseif {

        public static void main(String[] args) {

//            Scanner sc=new Scanner(System.in);
//            int n=sc.nextInt();            
//            String ans="";
//            if(n%2==1){
//              ans = "Weird";
//            }
//            else{
//            	if (n>2 && n<5){
//            		ans = "Not Weird";
//            	}else if (n>=6 && n<20){
//            		ans = "Weird";
//            	}else{
//            		ans = "Not Weird"; 
//            	}
//                
//            }
//            System.out.println(ans);
//        	String s = "";
            Scanner scan = new Scanner(System.in);
            int i = scan.nextInt();
            double d = scan.nextDouble();
            String s = scan.nextLine();
            if (scan.hasNextLine()){
            	s=s.concat(scan.nextLine());
            }
            
            System.out.println("String: " + s);
            System.out.println("Double: " + d);
            System.out.println("Int: " + i);
        }
            
        }
//    }

