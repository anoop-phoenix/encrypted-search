import java.util.Arrays;

public class Enc {
	// n is the block size, m is R's size
	// These should probably be project-global somewhere
	static int n = 32;
	static int m = 20;

	static byte[][] toBlocks(String plaintext)
	{
		//Let's go for perfect decryption, rather than case-insensitive
		//plaintext=plaintext.toLowerCase();
		String[] strArray = plaintext.split("\\b");
		int remainder;

		// find total number of blocks
		int blockNum = strArray.length;
		
		// convert string to byte array
		byte[][] wordArray = new byte[blockNum][];
		for (int i = 0; i < blockNum; i++){
			wordArray[i] = strArray[i].getBytes();
			//System.out.println(new String(wordArray[i]));
		}
		return wordArray;
	}
	
	static byte[] preEnc(byte[][] wordArray, int blockIndex, byte[] key2)
	{	
		byte[] word = wordArray[blockIndex];
		//encrypt Word with E(key2)
		return TwoBlockEncrypt.encrypt(word, key2);
	}
	
	static byte[] getPubkey(byte[] X, byte[] key1)
	{
		// pass first n-m bytes of X to L
		byte[] L = new byte[n-m];
		for(int i = 0; i < n-m; i++){
			L[i] = X[i];
		}
		//generate public key using f and key1, k = fkey1(L);
		byte[] ki = PRF.PRF(L, key1);
		return ki;
	}

	static byte[] getLeft(byte[] in) {
		return Arrays.copyOfRange(in,0,n-m);
	}

	static byte[] getRight(byte[] in) {
		return Arrays.copyOfRange(in,n-m,n);
	}
	
	static byte[] getT(byte[] Si, byte[] key){
		byte[] FkeySi = PRF.PRF(Si, key);
		byte[] Ti = new byte[n];
		// Ti = <Si, Fkey(si)>
		System.arraycopy(Si, 0, Ti, 0, n-m);
		System.arraycopy(FkeySi, 0, Ti, n-m, m);
				
		return Ti;
	}
	
	static byte[] xor(byte[]Xi, byte[]Ti){
		byte[] Ci = new byte[Xi.length];
		for(int k = 0; k < Xi.length; k++) {
			Ci[k] = (byte) (Xi[k] ^Ti[k]);
		}
		return Ci;
	}

	public static byte[] getNM(byte[] S){
		byte[] Si = new byte[n-m];
		for (int k = 0; k < Si.length; k++) {
			Si[k] = S[k];
		}
		return Si;
	}	

/* Encrypt a ciphertext for transmission to the server. */
	public static byte[][] encrypt(String plaintext, byte[] streamKey, byte[] key1, byte[] key2) {
		StreamChunker stream = new StreamChunker(streamKey);

		byte[][] wordArray = Enc.toBlocks(plaintext);
		byte[][] cipherText = new byte[wordArray.length][];
		for (int i = 0; i < wordArray.length ;i++){
			byte[] Xi = Enc.preEnc(wordArray, i,key2);
			byte[] ki = Enc.getPubkey(Xi, key1);
			byte[] Si = stream.getChunk();
			
			byte[] Ti = Enc.getT(Si, ki);
			byte[] Ci = Enc.xor(Xi, Ti);
			cipherText[i] = Ci;
		}
		return cipherText;
	}

/* Decrypt a ciphertext. */
	public static byte[][] decrypt(byte[][] cipherText, byte[] streamKey, byte[] key1, byte[] key2) {
		StreamChunker stream = new StreamChunker(streamKey);
		byte[][] decrypted = new byte[cipherText.length][];
		for (int i = 0; i < cipherText.length; i++) {
			byte[] Ci = cipherText[i];
			byte[] Yi = Arrays.copyOfRange(Ci,0,n-m);
			byte[] Si = stream.getChunk();
			byte[] Li = Enc.xor(Si, Yi);
			byte[] ki = Enc.getPubkey(Li, key1);
			byte[] Ti = Enc.getT(Si, ki);
			
			byte[] Xi = Enc.xor(Ci, Ti);
			byte[] Wi = TwoBlockEncrypt.decrypt(Xi, key2);
			decrypted[i] = Wi;
		}
		return decrypted;
	}

/* Construct a query object given a word and the two keys (stream key is not needed).
 * This query can be passed on to the server to do the search.
 */
	public static Query makeQuery(String word, byte[] key1, byte[] key2) {
		byte[] queryBlock = word.getBytes();
		byte[] X = TwoBlockEncrypt.encrypt(queryBlock, key2);
		byte[] K = Enc.getPubkey(X, key1);

		return new Query(X, K);
	}

/* Search for the first match of a query in a ciphertext.
 * Returns the index of the first match, or -1 if not found.
 *
 * Do we want more results (list of offsets) or fewer (bool)?
 */
	public static int search(Query q, byte[][] cipherText) {
		byte[] X = q.getWordBlock();
		byte[] K = q.getKey();

		for (int i = 0; i < cipherText.length; i++) {
			byte[] Ci = cipherText[i];
			byte[] Ti = Enc.xor(X, Ci);
			byte[] Si = Enc.getLeft(Ti);
			byte[] hash = PRF.PRF(Si, K);
			if (Arrays.equals(hash, Enc.getRight(Ti))) {
				return i;
			}
		}

		return (-1);
	}
}
