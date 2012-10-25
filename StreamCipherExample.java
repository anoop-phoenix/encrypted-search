import org.bouncycastle.crypto.engines.Salsa20Engine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.encoders.Hex;
import java.util.Arrays;

class StreamCipherExample {

  public static void main(String[] input) {
    byte[] key = "0123456789ABCDEF".getBytes();
    byte[] iv  = "01234567".getBytes();

    Salsa20Engine engine = new Salsa20Engine();
    engine.init(true, new ParametersWithIV(new KeyParameter(key), iv));

    int blockSize = 12;
    byte[] out = new byte[blockSize];
    byte[] zeros = new byte[blockSize];
    Arrays.fill(zeros, (byte) 0);

    for(int i = 0; i < 4; i++) {
      engine.processBytes(zeros, 0, blockSize, out, 0);
      printHex(out);
    }
  }

  static void printHex(byte[] stuff) {
    System.out.println(new String(Hex.encode(stuff)));
  }
}
