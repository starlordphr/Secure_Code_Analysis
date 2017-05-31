package com.google.errorprone.bugpatterns.testdata;

import java.util.Scanner;


public class NullPointerExceptionCaughtPositiveCases {

	// BUG: Diagnostic contains: Null pointer exception or ancestor caught
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
	
	// BUG: Diagnostic contains: Null pointer exception or ancestor caught
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
	
	// BUG: Diagnostic contains: Null pointer exception or ancestor caught
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
	
	// BUG: Diagnostic contains: Null pointer exception or ancestor caught
	public void runtimeExceptionViolation()
	{
		try {
			division(200, 5);
			division(200, 0); // Divide by zero
			} 
		catch (ArithmeticException ae) {
			throw ae;
			} 
		catch (RuntimeException e) {
			System.out.println("Exception occurred :" + e.getMessage());
			}
	}

}
