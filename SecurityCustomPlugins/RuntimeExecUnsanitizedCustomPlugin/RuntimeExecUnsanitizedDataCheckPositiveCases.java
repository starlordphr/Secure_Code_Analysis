package com.google.errorprone.bugpatterns.testdata;

import java.io.IOException;
import java.io.InputStream;

public class RuntimeExecUnsanitizedDataCheckPositiveCases {

	// BUG: Diagnostic contains: Parameters for Runtime.exec() unsanitized
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
	
	// BUG: Diagnostic contains: Parameters for Runtime.exec() unsanitized
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

}
