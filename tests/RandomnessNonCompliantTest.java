import java.util.Random;
class Test {
  public static void main(String[] args) {
    Random r = new Random();
    // BUG: Diagnostic contains:
    System.err.println(r.nextInt());
  }
}
