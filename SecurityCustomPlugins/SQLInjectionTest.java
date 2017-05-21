import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/*Sanitize Untrusted Data Check SQL Test Cases
    Issue stems from creating a string that does not validate that the parameters
    being used in the SQL string.
    
    Possible fix: validate the field or have it fed into an alternate method
    
    SQL Syntax overview: 
    Dangerous SQL strings start with SELECT, INSERT, DELETE, ALTER, DROP, CREATE, 
    USE, SHOW, ALTER
    By Dangerous, these key words allow syntax after it that can enable the attacker
    to access sensitive information or destroy sensitive information.
    
    E.g. Select * From db Where username = var
    
    var can be replaced with 'randomString OR '1 = 1' ' This will allow the attacker
    to be able to access the entire table b/c the new string will therefore return
    the whole table regardless of username b/c of the always true statement.
*/
public class SQLInjectionTest
{
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