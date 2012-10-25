import java.util.Arrays;

class CipherTest {
  // horribly low keyspace if we use only printable characters
  // genkey using SecureRandom for real usage
  static byte[] key = "abcdefhijklmnopq".getBytes();

  public static void main(String[] args) {
    byte[] input = "This is a test. Under 32.".getBytes();

    byte[] encrypted = TwoBlockEncrypt.encrypt(input, key);
    byte[] decrypted = TwoBlockEncrypt.decrypt(encrypted, key);

    if(Arrays.equals(input,decrypted)) {
      System.out.println("Success!");
    }
    else {
      System.out.println(new String(decrypted));
    }
  }
}
