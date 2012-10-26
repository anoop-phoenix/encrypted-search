
public class main {
	/**
	 * @param args
	 */
	public static int n = 32;
	public static int m = 16;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String plaintext;
		plaintext = "crypto";
		long key1 = 10000;
		long key2 = 10001;
		//Encryption
		Enc enc = new Enc();
		byte[][] wordArray = enc.toBlocks(plaintext);
		byte[][] S = enc.streamCipher();
		for (int i = 0; i < wordArray.length/n ;i++){
			byte[] Xi = enc.preEnc(wordArray, i,key2);
			byte[] ki = enc.getPubkey(Xi, key1);
			
			byte[] Ti = enc.getT(i, S, ki);
			byte[] Ci = enc.getC(Xi, Ti);
			// send Ci to server
		}
		

	}

}
