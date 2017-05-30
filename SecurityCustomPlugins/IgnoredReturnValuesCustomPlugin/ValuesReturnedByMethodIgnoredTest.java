import java.io.File;

/*
 * Test Cases for Ignored values returned by some selected methods Check
 * 
 * Reason is because you aren't validating that the operation was done correctly that it
 * can be exploited to access a control flow in the code that you don't want users to
 * get to
 * 
 * Examples are from the CERT Java Secure Coding book
 */

public class ValuesReturnedByMethodIgnoredTest {
	
	public ValuesReturnedByMethodIgnoredTest()
	{
		
	}
	
	public void fileDeleteViolation()
	{
		File someFile = new File("someFileName.txt");
		someFile.delete();
	}
	
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

	public void stringReplacementViolation()
	{
		String original = "insecure";
		original.replace('i', '9');
		System.out.println(original);
	}
	
	public void stringReplacementPass()
	{
		String original = "insecure";
		original = original.replace('i', '9');
		System.out.println(original);
	}

}
