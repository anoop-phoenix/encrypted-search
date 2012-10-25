import java.util.Arrays;
import java.security.SecureRandom;

class CipherTest {

  public static void main(String[] args) {
    byte[] input = "This is a test. Under 32.".getBytes();
    SecureRandom randy = new SecureRandom();
    byte[] key = new byte[16];
    randy.nextBytes(key);

    byte[] encrypted = TwoBlockEncrypt.encrypt(input, key);
    byte[] decrypted = TwoBlockEncrypt.decrypt(encrypted, key);
    // do we even use decryption in our program?

    if(Arrays.equals(input,decrypted)) {
      System.out.println("Success!");
    }
    else {
      System.out.println(new String(decrypted));
    }
  }
}
