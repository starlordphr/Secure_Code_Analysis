package com.google.errorprone.bugpatterns.testdata;

import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class RuntimeExecUnsanitizedDataCheckNegativeCases {

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
