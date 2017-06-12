import java.io.*;

public class proc_error {

public static void main(String[] args) throws IOException {
    File dir = new File(".");
	String fileName = "ivy_error_log.txt";
    String source = dir.getCanonicalPath() + File.separator + fileName;
    String dest = dir.getCanonicalPath() + File.separator + fileName + "_output.txt";

    File fin = new File(source);
    FileInputStream fis = new FileInputStream(fin);
    BufferedReader in = new BufferedReader(new InputStreamReader(fis));

    FileWriter fstream = new FileWriter(dest, true);
    BufferedWriter out = new BufferedWriter(fstream);

    String aLine = null;
	int number = 1;


/*
Checks we look for:
FailSecurelyCheck
ExtendedReturnValueIgnoredCheck
InsecureRandomCheck
NPEAndParentsCaughtCheck
CaptureReturnValueofRead
SQLUnsanitizedDataCheck
ConstantOverflow
ReturnValueIgnored
MustBeClosed
InsecureCipherMode
*/
	int FailSecurelyCheck = 0;
	int ExtendedReturnValueIgnoredCheck = 0;
	int InsecureRandomCheck = 0;
	int NPEAndParentsCaughtCheck = 0;
	int CaptureReturnValueofRead = 0;
	int SQLUnsanitizedDataCheck = 0;
	int ConstantOverflow = 0;
	int ReturnValueIgnored = 0;
	int MustBeClosed = 0;
	int InsecureCipherMode = 0;

    while ((aLine = in.readLine()) != null) {
        //Process each line and add output to output.txt file
		if(aLine.contains("warning") & aLine.contains(".java"))
		{
			if(aLine.contains("ExtendedReturnValueIgnoredCheck"))
			{
				ExtendedReturnValueIgnoredCheck++;
			}
			else if(aLine.contains("InsecureRandomCheck"))
			{
				InsecureRandomCheck++;
			}
			else if(aLine.contains("NPEAndParentsCaughtCheck"))
			{
				NPEAndParentsCaughtCheck++;
			}
			else if(aLine.contains("CaptureReturnValueofRead"))
			{
				CaptureReturnValueofRead++;
			}
			else if(aLine.contains("SQLUnsanitizedDataCheck"))
			{
				SQLUnsanitizedDataCheck++;
			}
			else if(aLine.contains("SQLUnsanitizedDataCheck"))
			{
				SQLUnsanitizedDataCheck++;
			}
			else if(aLine.contains("ConstantOverflow"))
			{
				ConstantOverflow++;
			}
			else if(aLine.contains("ReturnValueIgnored"))
			{
				ReturnValueIgnored++;
			}
			else if(aLine.contains("MustBeClosed"))
			{
				MustBeClosed++;
			}
			else if(aLine.contains("InsecureCipherMode"))
			{
				InsecureCipherMode++;
			}
			else if(aLine.contains("FailSecurelyCheck"))
			{
				FailSecurelyCheck++;
			}
			
			out.write(number + ".  ");
			int i = 0;
			int ccount = 0;
			String path = "";
			String error = "";
			for(; i < aLine.length()-1; i++)
			{
				if(aLine.charAt(i) == ':')
				{
					ccount++;
					if(ccount == 2)
						break;
				}
				path = path + aLine.charAt(i);
			}
			for(; aLine.charAt(i) != '['; i++)
			{
				//skip
			}
			out.write("\t");
			for(i = i+1; i < aLine.length()-1; i++)
			{
				if(aLine.charAt(i) == ']')
					break;
				error = error + aLine.charAt(i);
			}
			out.write(error);
			out.write(path);
			out.newLine();
			number++;
			out.flush();
			
		}
    }
	System.out.println(fileName + ":");
	System.out.println("ExtendedReturnValueIgnoredCheck: " + ExtendedReturnValueIgnoredCheck
	+ ", InsecureRandomCheck: " + InsecureRandomCheck
	+ ", NPEAndParentsCaughtCheck: " + NPEAndParentsCaughtCheck
	+ ", CaptureReturnValueofRead: " + CaptureReturnValueofRead
	+ ", SQLUnsanitizedDataCheck: " + SQLUnsanitizedDataCheck
	+ ", ConstantOverflow: " + ConstantOverflow
	+ ", ReturnValueIgnored: " + ReturnValueIgnored
	+ ", MustBeClosed: " + MustBeClosed
	+ ", InsecureCipherMode: " + InsecureCipherMode
	+ ", FailSecurelyCheck: " + FailSecurelyCheck);
	
    // do not forget to close the buffer reader
    in.close();

    // close buffer writer
    out.close();
}
}