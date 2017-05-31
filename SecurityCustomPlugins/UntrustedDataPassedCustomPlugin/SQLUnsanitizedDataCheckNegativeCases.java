package com.google.errorprone.bugpatterns.testdata;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class SQLUnsanitizedDataCheckNegativeCases {

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
	   
	   private String hash(String str)
	   {
		   //hash the string
		   return "&&&&&";
	   }
	   
	   public void pass(Connection conn) throws SQLException
	   {
	       readInStrings();
	       String hashedpw = hash(pwd);
	       if(username.length() > 8)
	       {
	           //throw error
	       }
	       else
	       {
	           String sqlString = "SELECT * from db where username=? and password = ?";
	           PreparedStatement stmt = conn.prepareStatement(sqlString);
	           stmt.setString(1,username);
	           stmt.setString(2,hashedpw);
	       }
	       
	   }

}
