import org.bouncycastle.util.encoders.Hex;

class StreamChunkerExample {
  public static void main(String[] in) {
    byte[] key = "0123456789ABCDEF01234567".getBytes();
    StreamChunker chunker = new StreamChunker(key);

    byte[] chunk;

    for(int i = 0; i < 4; i++) {
      chunk = chunker.getChunk();
      System.out.println(new String(Hex.encode(chunk)));
    }
  }

}
