import java.util.Arrays;

public class main {
	/**
	 * @param args
	 */
	public static int n = 32;
	public static int m = 20;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String plaintext;
		plaintext = "crypto";
		// TODO generate randomly, get from config, etc
		byte[] key1 = "this can be any size we want".getBytes();
		byte[] key2 = "16 chars exactly".getBytes();
		byte[] streamKey = "This one is twenty-four!".getBytes();
		//Encryption
		Enc enc = new Enc();
		StreamChunker stream = new StreamChunker(streamKey);

		byte[][] wordArray = enc.toBlocks(plaintext);
		byte[][] cipherText = new byte[wordArray.length][];
		for (int i = 0; i < wordArray.length ;i++){
			byte[] Xi = enc.preEnc(wordArray, i,key2);
			byte[] ki = enc.getPubkey(Xi, key1);
			byte[] Si = stream.getChunk();
			
			byte[] Ti = enc.getT(Si, ki);
			byte[] Ci = enc.xor(Xi, Ti);
			// send Ci to server
			cipherText[i] = Ci;
		}
		System.out.println(new String(cipherText[0]));
		// now let's try decrypting
		// reset the stream
		StreamChunker stream2 = new StreamChunker(streamKey);
		byte[][] decrypted = new byte[cipherText.length][];
		for (int i = 0; i < cipherText.length; i++) {
			byte[] Ci = cipherText[i];
			byte[] Yi = Arrays.copyOfRange(Ci,0,n-m);
			//byte[] Zi = Arrays.copyOfRange(cipherText[i],n-m,n);
			byte[] Si = stream2.getChunk();
			byte[] Li = enc.xor(Si, Yi);
			byte[] ki = enc.getPubkey(Li, key1);
			byte[] Ti = enc.getT(Si, ki);
			
			byte[] Xi = enc.xor(Ci, Ti);
			byte[] Wi = TwoBlockEncrypt.decrypt(Xi, key2);
			decrypted[i] = Wi;
		}

		System.out.println(new String(decrypted[0]));


		

	}

}
