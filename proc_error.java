import java.io.*;

public class proc_error {

public static void main(String[] args) throws IOException {
    File dir = new File(".");

    String source = dir.getCanonicalPath() + File.separator + "custom_error_log.txt";
    String dest = dir.getCanonicalPath() + File.separator + "output.txt";

    File fin = new File(source);
    FileInputStream fis = new FileInputStream(fin);
    BufferedReader in = new BufferedReader(new InputStreamReader(fis));

    FileWriter fstream = new FileWriter(dest, true);
    BufferedWriter out = new BufferedWriter(fstream);

    String aLine = null;
	int number = 1;
    while ((aLine = in.readLine()) != null) {
        //Process each line and add output to output.txt file
		if(aLine.contains("warning"))
		{
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

    // do not forget to close the buffer reader
    in.close();

    // close buffer writer
    out.close();
}
}