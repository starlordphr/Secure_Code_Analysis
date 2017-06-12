import java.io.IOException;
import java.io.FileReader;

class CompliantTest2 {

   public static void main(String[] args) throws IOException {
      FileReader fis = null;
      int i = 0;
      char c;

      try {

         // create new file input stream
         fis = new FileReader("test.txt");

         // read till the end of the file
         while((i = fis.read())!=-1) {

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
