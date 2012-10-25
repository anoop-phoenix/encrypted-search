import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import java.util.Arrays;
/*
 * TwoBlockEncrypt
 *
 *   encrypt(in, key)
 * This takes an input byte array of length <= 32,
 * and a key of length 16.
 * Input is null-padded to 32 bytes and encrypted deterministically.
 * Thus, output is 32 bytes.
 *
 *   decrypt(in, key)
 * This reverses the above.
 * If the plaintext ended with a null it will be trimmed. Not bothering with
 * fancier padding because we don't expect to have non-English inputs.
 *
 */
public class TwoBlockEncrypt {

  static byte[] nullPad(byte[] in) throws RuntimeException {
    if(32 < in.length) {
      throw new RuntimeException("we don't support length > 32");
    }

    return Arrays.copyOf(in, 32);
  }

  static byte[] unPad(byte[] in) {
    // assumes input has no null bytes
    int i = findIndex(in, (byte) 0);
    if(i == -1) {
      return in;
    }

    return Arrays.copyOf(in, i);
  }

  static int findIndex(byte[] array, byte elt) {
    for(int i = 0; i < array.length; i++) {
      if(array[i] == elt) {
        return i;
      }
    }
    return -1;
  }


  public static byte[] encrypt(byte[] in, byte[] key) {
  // use this, or Buffered / Padded cipher?
    CBCBlockCipher cipher = new CBCBlockCipher(new AESEngine());

    byte[] out = new byte[32];

    byte[] padded = nullPad(in);

    // we need it to be deterministic, so use default 0 IV
    cipher.init(true, new KeyParameter(key));
    cipher.processBlock(padded, 0, out, 0);
    cipher.processBlock(padded, 16, out, 16);

    return out;
  }


  public static byte[] decrypt(byte[] in, byte[] key) {
    CBCBlockCipher cipher = new CBCBlockCipher(new AESEngine());

    byte[] out = new byte[32];

    cipher.init(false, new KeyParameter(key));
    cipher.processBlock(in, 0, out, 0);
    cipher.processBlock(in, 16, out, 16);

    byte[] unpadded = unPad(out);

    return unpadded;
  }
}

