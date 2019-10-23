package com.techmania.designprinciples.designs;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class SelectToInsert {
	public static void main(String[] args) throws IOException {
		String data=FileUtils.readFileToString(new File("/home/YUME.COM/vrengasamy/Desktop/DistinctFilter"), "UTF-8");
		String[] dDP=data.split("SPLIT");
		for(String d:dDP) {
			StringBuilder builder=new StringBuilder();
			builder.append("INSERT INTO report_type_filter VALUES (");
			for(String q1:d.split("\n")) {
				q1=q1.trim();
				if(!q1.equals("")) {
					String[] s1=q1.split(":");
					if(s1.length==2) {
						builder.append("'"+s1[1].trim()+"'").append(",");
					} else {
						builder.append("''").append(",");
					}
				}
			}
			builder.deleteCharAt(builder.toString().length()-1);
			builder.append(");");
			System.out.println(builder.toString());
		}
	}
}
