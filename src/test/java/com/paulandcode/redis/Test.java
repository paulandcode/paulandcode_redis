package com.paulandcode.redis;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {	
	public static void main(String[] args) {
		
		
		String sql = "LIKE '#{12}#' {12} {12} {sdf}";
		System.out.println(search(sql, "\\{{1}.*?\\}{1}"));
	}
	
	public static int search(String str, String regex) {
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(str);
		int count = 0;
		while (m.find()) {
			count++;
		}
		return count;
	}
}