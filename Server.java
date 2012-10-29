import java.io.*;
import java.net.*;
import java.util.*;

/** 
    Server for CSE 667 Project 2
    Team 4:
    Peter McLarhan
    Benjamin Rogers
    Jun Wang
 **/
class Server {

    static int SEARCH_FLAG = 0;
    static int FILE_FLAG = 1;
    static int ERROR_FLAG = -1;
    static String CONFIG_FILE = "server.config";
    static int BLOCK_SIZE = 32;
    static int BYTES_IN_CHAR = 2;

    // Search handles searches of "searchString" and outputs a string
    public static String Search(String searchString, String directory, String key) {

	searchString = searchString.substring(0, searchString.indexOf("\n"));

	ArrayList<String> matchingFiles = new ArrayList<String>();
	
	String retString = "";
	
	try {
	    // get all text files in directory
	    File folder = new File(directory);
	    File[] listOfFiles = folder.listFiles();
	    
	    for (int i = 0; i < listOfFiles.length; i++) {
		if (listOfFiles[i].isFile() && listOfFiles[i].toString().endsWith(".txt")) {
		    // Search File
		    FileInputStream fstream = new FileInputStream(listOfFiles[i]);
		    DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    String strLine;
		    while (br.ready()) {
			strLine = br.readLine();
			if (compare(strLine, searchString, key)) {
			    // store only file name, not path
			    strLine = listOfFiles[i].toString();
			    if (strLine.indexOf("\\") >= 0) {
				matchingFiles.add(strLine.substring(strLine.lastIndexOf("\\") + 1));
			    } else {
				if (strLine.indexOf("/") >= 0) {
				    matchingFiles.add(strLine.substring(strLine.lastIndexOf("/") + 1));
				}
			    }

			    break;
			}
		    }
		}
	    }

	    retString += SEARCH_FLAG + "\n" + SEARCH_FLAG + "\n" + matchingFiles.size() + "\n";
	    for (int i = 0; i < matchingFiles.size(); i++) {
		retString += matchingFiles.get(i) + "\n";
	    }

	    return retString;

	} catch (Exception e) {
	    System.err.println("Execption: " + e.getMessage());
	    e.printStackTrace();
	    return SEARCH_FLAG + "\n" + ERROR_FLAG + "\n";
	}
    }

    // WriteFile  handles the writing of files passed from a client to a directory
    public static String WriteFile(String fileString, String directory) {
	System.out.println(fileString);
	String filename, fileNextLine;
	fileString += "\n";
	filename = fileString.substring(0, fileString.indexOf("\n"));
	filename = decodeBlock(filename);
	filename = filename.substring(0, filename.indexOf(" "));
	fileString = fileString.substring(BLOCK_SIZE);
	try { 
	    FileWriter fstream = new FileWriter(directory + filename);
	    BufferedWriter out = new BufferedWriter(fstream);
	    out.write(fileString);
	    out.close();
	    
	    return FILE_FLAG + "\n" + FILE_FLAG + "\n";
	} catch (Exception e) {
	    System.err.println("Error :" + e.getMessage());
	    return FILE_FLAG+ "\n" + ERROR_FLAG + "\n";
	}
    }

    // byte array to string of ints seperated by commas
    public static String bytesToIntStr(byte[] bytes) {
	String str = "";
	for (int i = 0; i < bytes.length; i++) {
	    str += Byte.toString(bytes[i]) + ",";
	}
	str = str.substring(0, str.length()-1);
	System.out.println(str);
	return str;
    }

    // compares blocks for searches
    public static boolean compare(String str1, String str2, String key) {
	Integer temp;
	String[] strs1 = str1.split(",");
	byte[] block1 = new byte[strs1.length];
	String[] strs2 = str2.split(",");
	byte[] block2 = new byte[strs2.length];
	String[] keys = key.split(",");
	byte[] key1 = new byte[keys.length];
	for (int i = 0; i < block1.length; i++) {
	    if (!strs1[i].equals("")) {
		temp = Integer.parseInt(strs1[i]);
		block1[i] = temp.byteValue();
	    }
	}
	for (int i = 0; i < block2.length; i++) {
	    if (!strs2[i].equals("")) {
		temp = Integer.parseInt(strs2[i]);
		block2[i] = temp.byteValue();
	    }
	}
	for (int i = 0; i < key1.length; i++) {
	    if (!keys[i].equals("")) {
		temp = Integer.parseInt(keys[i]);
		key1[i] = temp.byteValue();
	    }
	}
	if (block1.length == block2.length) {
	    Enc enc = new Enc();
	    //byte[] key1 = "this can be any size we want".getBytes();
	    byte[] Ci = Enc.xor(block1, block2);
	    byte[] pubk = Enc.getPubkey(block2, key1);
	
	    System.out.println(bytesToIntStr(block1) + "\n" + bytesToIntStr(block2) + "\n" + bytesToIntStr(Enc.getT(Enc.getNM(Ci), pubk)) + "\n" + bytesToIntStr(Ci) + "\n");
	    return (bytesToIntStr(Enc.getT(Enc.getNM(Ci), pubk)).equals(bytesToIntStr(Ci)));
	} else {
	    return false;
	}
    }


    // decodes a block
    public static String decodeBlock(String str) {
	String newStr = "";
	Integer temp;
	byte[] b = new byte[1];
	String[] strs = str.split(",");
	for (int i = 0; i < strs.length; i++) {
	    if (!strs[i].equals("")) {
		temp = Integer.parseInt(strs[i]);
		b[0] = temp.byteValue();
		newStr += new String(b);
	    }
	}
	System.out.println(newStr);
	return newStr;
    }

    // reads in a block
    public static String readBlock(BufferedReader br) throws Exception{
	char[] buffer = new char[BLOCK_SIZE];
	int value;
	for (int i = 0; i < BLOCK_SIZE; i++) {
	    br.read();
	    value = br.read();
	    buffer[i] = Character.toChars(value)[0];
	}
	System.out.println(String.copyValueOf(buffer));
	return String.copyValueOf(buffer);
    }

    // reads in a block
    public static String readBlock(BufferedInputStream is, int offset) throws Exception{
	byte[] buffer = new byte[BLOCK_SIZE*2];
	int value;
	is.read(buffer, offset*BLOCK_SIZE*2, buffer.length);
	for (int i = 0; i < buffer.length; i++) {
	    System.out.print((buffer[i]) + ",");
	}
	System.out.println(new String(buffer));
	return new String(buffer);
    }

   // reads in a block
    public static String readBlockClean(BufferedReader br) throws Exception{
	char[] buffer = new char[BLOCK_SIZE];
	int value;
	for (int i = 0; i < BLOCK_SIZE; i++) {
	    value = br.read();
	    buffer[i] = Character.toChars(value)[0];
	}
	System.out.println(String.copyValueOf(buffer));
	return String.copyValueOf(buffer);
    }

    // delete leading zeros
    public static String dezero(String str) {
	String newString = str;

	while (newString.indexOf('0') == 0 && newString.length() > 1) {
	    newString = newString.substring(1);
	}
	return newString.trim();
    }

    // counts the number of given chars in a string
    public static int count(String str, char c) {
	int count = 0;

	for (int i = 0; i < str.length(); i++) {
	    if (str.charAt(i) == c) {
		count++;
	    }
	}
	
	return count;
    }

    
    // main communicates with the client
    public static void main(String argv[]) throws Exception {
	
	String directory;
	int sock;

	// Read in the config file
	try {
	    FileInputStream fstream = new FileInputStream(CONFIG_FILE);
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String strLine;
	    sock = Integer.parseInt(br.readLine().substring(9));
	    directory = br.readLine().substring(12);
	    
	} catch (Exception e) {
	    System.err.println("Execption: " + e.getMessage());
	    sock = 6789;
	    directory = System.getProperty("user.dir");
	}

	Enc enc = new Enc();
	String clientSentence;
	String capitalizedSentence;
	int flag;
	int size;
	ArrayList<String> files = new ArrayList<String>();
	ServerSocket welcomeSocket = new ServerSocket(sock);
	
	while(true) {
	    
	    Socket connectionSocket = welcomeSocket.accept();
	    //InputStream is = connectionSocket.getInputStream();
	    //BufferedInputStream bis = new BufferedInputStream(is);
	    BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
	    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

	    clientSentence = dezero(readBlock(inFromClient));
	    flag = Integer.parseInt(clientSentence);

	    clientSentence = dezero(readBlock(inFromClient));
	    size = Integer.parseInt(clientSentence);
	    System.out.println(flag + ", " + size);

	    clientSentence = "";
	    for (int i = 0; i < size; i++) {
		clientSentence += inFromClient.readLine() + "\n";//readBlock(inFromClient);
	    }

	    // If client wants to search
	    if (flag == SEARCH_FLAG) {
		files.clear();

		// search
		outToClient.writeBytes(Search(clientSentence, directory, inFromClient.readLine()));

		//BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));

		// if client wants to download found files
		while(!inFromClient.ready()) {};
		if (FILE_FLAG == Integer.parseInt(inFromClient.readLine())) {
		    size = Integer.parseInt(inFromClient.readLine());
		    System.out.println(size + "");
		    for (int i = 0; i < size; i++) {
			files.add(inFromClient.readLine());
		    }
		    
		    // send files
		    try {
			for (int i = 0; i < files.size(); i++) {
			    ArrayList<String> fileText = new ArrayList<String>();
			    FileInputStream fstream = new FileInputStream(directory + files.get(i));
			    DataInputStream in = new DataInputStream(fstream);
			    BufferedReader br = new BufferedReader(new InputStreamReader(in));
			    String strLine;
			    while ((strLine = br.readLine()) != null) {
				fileText.add(strLine);
			    }
			    
			    outToClient.writeBytes(fileText.size() + "\n");
			    for (int j = 0; j < fileText.size(); j++) {
				outToClient.writeBytes(fileText.get(j) + "\n");
			    }
			    
			}
		    } catch (Exception e) {
			System.err.println("Execption: " + e.getMessage());
			outToClient.writeBytes(ERROR_FLAG + "\n");
		    }
		}
	    }

	    // If client want to store a file
	    if (flag == FILE_FLAG) {
		outToClient.writeBytes(WriteFile(clientSentence, directory));
	    }
	}
    }
}
