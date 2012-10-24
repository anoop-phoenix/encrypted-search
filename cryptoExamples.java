import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.crypto.params.KeyParameter;
import java.util.Arrays;
import java.security.SecureRandom;
import org.bouncycastle.util.encoders.Hex;

class CipherTest {
  static byte[] key = "abcdefhijklmnopq".getBytes();

  public static void main(String[] args) {
    byte[] input = "This is a test.@".getBytes();
    byte[] encrypted;
    byte[] decrypted;

    encrypted = encryptTest(input);
    decrypted = decryptTest(encrypted);

    if(Arrays.equals(input,decrypted)) {
      System.out.println("Success!");
    }
    else {
      System.out.println(new String(decrypted));
    }
  }

  static byte[] concat(byte[] left, byte[] right) {
    byte[] out = new byte[left.length + right.length];
    System.arraycopy(left, 0, out, 0, left.length);
    System.arraycopy(right, 0, out, left.length, right.length);

    return out;
  }


  static byte[] encryptTest(byte[] in) {
  // use this, or Buffered / Padded cipher?
    CBCBlockCipher cipher = new CBCBlockCipher(new AESEngine());

    // get a random IV
    SecureRandom random = new SecureRandom();
    byte[] IV = new byte[16];
    random.nextBytes(IV);
    byte[] ciphertext = new byte[16];

    cipher.init(true, new ParametersWithIV(new KeyParameter(key), IV));
    cipher.processBlock(in, 0, ciphertext, 0);

    return concat(IV, ciphertext);
  }


  static byte[] decryptTest(byte[] in) {
    CBCBlockCipher cipher = new CBCBlockCipher(new AESEngine());

    byte[] IV = Arrays.copyOfRange(in, 0, 16);
    byte[] out = new byte[16];

    cipher.init(false, new ParametersWithIV(new KeyParameter(key), IV));
    cipher.processBlock(in, 16, out, 0);

    return out;
  }
}
