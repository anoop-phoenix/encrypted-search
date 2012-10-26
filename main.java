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
		plaintext = "crypto is fun. but it's hard.";
		// TODO generate randomly, get from config, etc
		byte[] key1 = "this can be any size we want".getBytes();
		byte[] key2 = "16 chars exactly".getBytes();
		byte[] streamKey = "This one is twenty-four!".getBytes();
		//Encryption
		Enc enc = new Enc();
		StreamChunker stream = new StreamChunker(streamKey);

		byte[][] wordArray = enc.toBlocks(plaintext);
		printByteArrayArray(wordArray);
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
		printByteArrayArray(cipherText);
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

		printByteArrayArray(decrypted);

		// TODO refactor much
		// make query
		String query = "but";
		byte[] queryBlock = enc.toBlocks(query)[0];
		byte[] X = TwoBlockEncrypt.encrypt(queryBlock, key2);
		byte[] K = enc.getPubkey(X, key1);

		// do search
		for (int i = 0; i < cipherText.length; i++) {
			byte[] Ci = cipherText[i];
			byte[] Ti = enc.xor(X, Ci);
			byte[] Si = enc.getLeft(Ti);
			byte[] hash = PRF.PRF(Si, K);
			if (Arrays.equals(hash, enc.getRight(Ti))) {
				System.out.println("match at " + i);
			}
		}


		

	}

	static void printByteArrayArray(byte[][] input) {
		for (int i = 0; i < input.length; i++) {
			System.out.println(new String(input[i]));
		}
	}


}
