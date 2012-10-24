import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import java.util.Arrays;

class CipherTest {
  public static void main(String[] args) {
    byte[] input = "This is a test.@".getBytes();
    byte[] encrypted = new byte[16];
    byte[] decrypted = new byte[16];

    encryptTest(input, encrypted);
    decryptTest(encrypted, decrypted);

    if(Arrays.equals(input,decrypted)) {
      System.out.println("Success!");
    }
  }

  static byte[] concat(byte[] left, byte[] right) {
    byte[] out = new byte[left.length + right.length];
    System.arraycopy(left, 0, out, 0, left.length);
    System.arraycopy(right, 0, out, left.length, right.length);

    return out;
  }


  static void encryptTest(byte[] in, byte[] out) {
  // use this, or Buffered / Padded cipher?
    byte[] key = "abcdefhijklmnopq".getBytes();
    CBCBlockCipher cipher = new CBCBlockCipher(new AESEngine());

    // IV is probably important. change to ParameterWithIV eventually
    cipher.init(true, new KeyParameter(key));
    cipher.processBlock(in, 0, out, 0);
  }


  static void decryptTest(byte[] in, byte[] out) {
    byte[] key = "abcdefhijklmnopq".getBytes();
    CBCBlockCipher cipher = new CBCBlockCipher(new AESEngine());

    // IV is probably important. change to ParameterWithIV eventually
    cipher.init(false, new KeyParameter(key));
    cipher.processBlock(in, 0, out, 0);
  }
}
