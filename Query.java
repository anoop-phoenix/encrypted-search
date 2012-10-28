import java.util.Arrays;
// A simple wrapper class for two byte arrays.
// Used for queries, which contain an encrypted word and a key.
class Query {
  // n is the block size, m is R's size
  // These should probably be project-global somewhere
  static int n = 32;
  static int m = 20;
  byte[] wordBlock;
  byte[] key;

  public byte[] getWordBlock() {
    return this.wordBlock;
  }

  public byte[] getKey() {
    return this.key;
  }

  public Query(byte[] wordBlock, byte[] key) {
    this.wordBlock = wordBlock;
    this.key = key;
  }

  public static Query fromBytes(byte[] input) {
    byte[] word = Arrays.copyOfRange(input, 0, n);
    byte[] key = Arrays.copyOfRange(input, n, n+m);

    return new Query(word, key);
  }

  public byte[] getBytes() {
    byte[] output = new byte[n+m];
    System.arraycopy(wordBlock, 0, output, 0, n);
    System.arraycopy(key, 0, output, n, m);

    return output;
  }


}
