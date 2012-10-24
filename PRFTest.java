import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.encoders.Hex;

class PRFTest {
  static byte[] PRF(byte[] input, byte[] key) {
    HMac hmac = new HMac(new SHA1Digest());
    hmac.init(new KeyParameter(key));
    hmac.update(input, 0, input.length);

    // this is 20 bytes
    byte[] output = new byte[hmac.getUnderlyingDigest().getDigestSize()];

    hmac.doFinal(output, 0);
    
    return output;
  }

  public static void main(String[] args) {
    byte[] key = "blahblahblah".getBytes();
    byte[] input = "This is a test.".getBytes();

    String hexed = new String(Hex.encode(PRF(input, key)));

    System.out.println(hexed);
  }



}
