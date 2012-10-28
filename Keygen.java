import java.security.SecureRandom;

// Get a key of the specified length.
// Example: streamKey = Keygen.gen(24);
class Keygen {
  public static byte[] gen(int i) {
    byte[] out = new byte[i];

    SecureRandom generator = new SecureRandom();
    generator.nextBytes(out);

    return out;
  }

}
