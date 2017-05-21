import java.util.Scanner;

/*
 * Test Cases for the NullPointerException Caught Checker
 * 
 * NullPointerException and its ancestors are too generic such that the attacker can trigger
 * the control flow that should not be accessible by other exceptions
 * 
 * Ancestors are: Throwable, RuntimeException, Exception
 * 
 * In fact, it's a good idea not to use NullPointerException to test for null conditions...
 */
public class NullPointerExceptionCaughtTest {
	
	public NullPointerExceptionCaughtTest()
	{
		
	}
	

	public void nullPointerViolation()
	{
		Scanner scan = new Scanner(System.in);
		
		int createScanner = scan.nextInt();
		
		if(createScanner == 0)
		{
			scan.close();
			scan = null;
		}
		
		try
		{
			scan.next();
			scan.close();
		}
		catch(NullPointerException e)
		{
			//ignore
		}
		
	}
	
	//examples from the book start here...
	private void division(int totalSum, int totalNumber)
			throws ArithmeticException
	{
		int average = totalSum / totalNumber;
		System.out.println("Average: " + average);
	}
	
	public void exceptionViolation()
	{
		try {
			division(200, 5);
			division(200, 0); // Divide by zero
			} 
		catch (Exception e) 
		{
			System.out.println("Divide by zero exception : " + e.getMessage());
		}		
	}
	
	public void overcatchExceptionViolation()
	{
		try {
			division(200, 5);
			division(200, 0); // Divide by zero
			} 
		catch (ArithmeticException ae) {
			throw ae;
			} 
		catch (Exception e) {
			System.out.println("Exception occurred :" + e.getMessage());
			}
	}
	
	public void runtimeExceptionViolation()
	{
		
	}
	
	public void nullPointerPass()
	{
		String str = "Hello!";
		
		Scanner strScanner = new Scanner(System.in);
		
		int resetString = strScanner.nextInt();
		
		switch(resetString)
		{
		case 0:
			str = null;
			break;
			
		default:
			str = strScanner.next();
		}
		
		strScanner.close();
		
		if(str == null)
		{
			//don't print
		}
		else
		{
			System.out.println(str);
		}
	}
}
