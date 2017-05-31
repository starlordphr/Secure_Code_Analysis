package com.google.errorprone.bugpatterns.testdata;

import java.util.Scanner;

public class NullPointerExceptionCaughtNegativeCases {

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
