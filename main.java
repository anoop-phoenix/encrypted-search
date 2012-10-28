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
		byte[][] cipherText = Enc.encrypt(plaintext, streamKey, key1, key2);

		printByteArrayArray(cipherText);
		// now let's try decrypting
		byte[][] decrypted = Enc.decrypt(cipherText, streamKey, key1, key2);

		printByteArrayArray(decrypted);

		String query = "IT's";
		Query q = Enc.makeQuery(query, key1, key2);

		byte[] msg = q.getBytes();

		// do search
		Query q2 = Query.fromBytes(msg);
		int matchLocation = Enc.search(q2, cipherText);
		if (matchLocation==-1){
			System.out.println("There is no match");
		}else{
			System.out.println(matchLocation);
		}
	}

	static void printByteArrayArray(byte[][] input) {
		for (int i = 0; i < input.length; i++) {
			System.out.println(new String(input[i]));
		}
	}


}
