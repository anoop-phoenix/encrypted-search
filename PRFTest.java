import org.bouncycastle.util.encoders.Hex;

class PRFTest {

  public static void main(String[] args) {
    byte[] key = "blahblahblah".getBytes();
    byte[] input = "This is a test.".getBytes();

    String hexed = new String(Hex.encode(PRF.PRF(input, key)));

    System.out.println(hexed);
  }

}
