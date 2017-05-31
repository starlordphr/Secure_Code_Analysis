package com.google.errorprone.bugpatterns.testdata;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SQLUnsanitizedDataCheckPositiveCases {

	   private String pwd;
	   private String username;
	   private String fieldName;
	   private String fieldValue;
	   
	   private void readInStrings()
	   {
		   Scanner scan = new Scanner(System.in);
	       username = scan.next();
	       pwd = scan.next();
	       fieldName = scan.next();
	       fieldValue = scan.next();
	       scan.close();
	   }
	
	   // BUG: Diagnostic contains: Parameters passed to SQL unsanitized
	   public void violation1(Connection conn) throws SQLException
	   {
	       readInStrings();
	       String sqlString = "SELECT * from db where username= " + username 
	        + "and password = " + pwd;
	       Statement stmt = conn.createStatement();
	       ResultSet rs = stmt.executeQuery(sqlString);
	   }
	   
	// BUG: Diagnostic contains: Parameters passed to SQL unsanitized
	   public void violation2(Connection conn) throws SQLException
	   {
	       readInStrings();
	       String sqlString = "Select * from db where " + fieldName + "= " + fieldValue;
	       Statement stmt = conn.createStatement();
	       ResultSet rs = stmt.executeQuery(sqlString);
	   }
}
