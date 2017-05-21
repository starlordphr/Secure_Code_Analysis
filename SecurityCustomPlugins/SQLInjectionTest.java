import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class SQLInjectionTest
{
   //code test cases for Sanitize untrusted data across trust boundaray (#7 on our list)
   private String pwd;
   private String username;
   private String fieldName;
   private String fieldValue;
   
   public SQLInjectionTest()
   {
       
   }
   
   private void readInStrings()
   {
	   Scanner scan = new Scanner(System.in);
       username = scan.next();
       pwd = scan.next();
       fieldName = scan.next();
       fieldValue = scan.next();
       scan.close();
   }

   //snipped example from the CERT Java Secure Coding book
   public void violation1(Connection conn) throws SQLException
   {
       readInStrings();
       String sqlString = "SELECT * from db where username= " + username 
        + "and password = " + pwd;
       Statement stmt = conn.createStatement();
       ResultSet rs = stmt.executeQuery(sqlString);
   }

   //a little bit more generic and too open example where we allow a custom query
   //but have no check on how long the field can be or if it matches a single query op
   public void violation2(Connection conn) throws SQLException
   {
       readInStrings();
       String sqlString = "Select * from db where " + fieldName + "= " + fieldValue;
       Statement stmt = conn.createStatement();
       ResultSet rs = stmt.executeQuery(sqlString);
   }
   
   private String hash(String str)
   {
	   //hash the string
	   return "&&&&&";
   }

   //the correct way according to the book.
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