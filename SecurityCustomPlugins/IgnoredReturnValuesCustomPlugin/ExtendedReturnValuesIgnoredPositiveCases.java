package com.google.errorprone.bugpatterns.testdata;

import java.io.File;

public class ExtendedReturnValuesIgnoredPositiveCases {

	// BUG: Diagnostic contains: Return value ignored
	public void fileDeleteViolation()
	{
		File someFile = new File("someFileName.txt");
		someFile.delete();
	}
	
	// BUG: Diagnostic contains: Return value ignored
	public void stringReplacementViolation()
	{
		String original = "insecure";
		original.replace('i', '9');
		System.out.println(original);
	}
}
