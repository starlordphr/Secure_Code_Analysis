package com.google.errorprone.bugpatterns.testdata;

import java.io.File;


public class ExtendedReturnValuesIgnoredNegativeCases {

	public void fileDeletePass()
	{
		File someFile = new File("someFileName.txt");
		// do something with someFile
		boolean successDelete = someFile.delete();
		
		if(!successDelete)
		{
			//error
		}
	}
	
	public void stringReplacementPass()
	{
		String original = "insecure";
		original = original.replace('i', '9');
		System.out.println(original);
	}

}
