import java.io.IOException;
import java.io.FileInputStream;

public class NonCompliantTest1 {

   public static void main(String[] args) throws IOException {
      FileInputStream fis = null;
      byte i;
      char c;

      try {

         // create new file input stream
         fis = new FileInputStream("test.txt");

         // read till the end of the file
         while((i = (byte) fis.read())!=-1) {

            // converts integer to character
            c = (char)i;

            // prints character
            System.out.print(c);
         }

      } catch(Exception ex) {

         // if any error occurs
         ex.printStackTrace();
      } finally {

         // releases all system resources from the streams
         if(fis!=null)
            fis.close();
      }
   }
}
