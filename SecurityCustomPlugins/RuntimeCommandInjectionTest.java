import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.regex.Pattern;

/* Test Cases for Unsantized Untrusted Data passed to Runtime.exec() Check
 * 
 * Issue stems that the variable can be extended and joined to have alternate
 * commands be run such that sensitive data ends up leaked or held hostage
 * 
 * Examples are mostly from the text book CERT Java Secure Coding
 */

public class RuntimeCommandInjectionTest {
	
	public RuntimeCommandInjectionTest()
	{
		
	}
	
	//dir is not validated here
	public void windowsViolation() throws IOException, InterruptedException
	{
		String dir = System.getProperty("dir");
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec("cmd.exe /C dir " + dir);
		int result = proc.waitFor();
		if (result != 0) {
		System.out.println("process error: " + result);
		}
		
		InputStream in;
		if(result == 0)
		{
			in = proc.getInputStream();
		}
		else
		{
			in = proc.getErrorStream();
		}
		int c;
		while ((c = in.read()) != -1) {
		System.out.print((char) c);
		}
		
		in.close();
	}
	
	//the nice linux/posix version
	public void posixViolation() throws IOException, InterruptedException
	{
		String dir = System.getProperty("dir");
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec(new String[] {"sh", "-c", "ls " + dir});
		int result = proc.waitFor();
		if (result != 0) {
		System.out.println("process error: " + result);
		}
		InputStream in = (result == 0) ? proc.getInputStream() :
		proc.getErrorStream();
		int c;
		while ((c = in.read()) != -1) {
		System.out.print((char) c);
		}
		
		in.close();
	}
	
	public void ifElseStatementPass() throws IOException
	{
		String dir = System.getProperty("dir");
		
		Runtime rt = Runtime.getRuntime();
		Process proc = rt.exec("cmd.exe /C dir " + dir);
		
		if (!Pattern.matches("[0-9A-Za-z@.]+", dir)) {
			// Handle error
			}
		// . ..
	}
	
	public void caseStatementPass() throws IOException
	{
		Runtime rt = Runtime.getRuntime();
		
		Scanner inputScanner = new Scanner(System.in);
		int userPick = inputScanner.nextInt();
		inputScanner.close();
		
		String dir = "";
		switch(userPick)
		{
		//you get the idea...
		case 0:
			dir = "textdirectory.dir";
			break;
		case 1:
			dir = "stringdirectory.dir";
			break;
		case 2:
			dir = "pictureDirectory.dir";
			break;
		default:
			System.out.println("BAD PICK!");
		}
		
		if(dir.equals(""))
		{
			//error
		}
		else
		{
			//execute statement
			Process proc = rt.exec("cmd.exe /C dir " + dir);
		}
	}
}
