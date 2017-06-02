import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

public final class CompliantTest {
  public static void main (String args[]) {
  try {
  SecureRandom number = SecureRandom.getInstance("SHA1PRNG");
  // Generate 20 integers 0..20
  for (int i = 0; i < 20; i++) {
  System.out.println(number.nextInt(21));
  }
  } catch (NoSuchAlgorithmException nsae) {
  // Forward to handler
  }
  }
}
