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

    // Search handles searches of "searchString" and outputs a string
    public static String Search(String searchString, String directory) {

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
		    while ((strLine = br.readLine()) != null) {
			if (strLine.equals(searchString)) {
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
	    return SEARCH_FLAG + "\n" + ERROR_FLAG + "\n";
	}
    }

    // WriteFile  handles the writing of files passed from a client to a directory
    public static String WriteFile(String fileString, String directory) {
	String filename, fileNextLine;
	fileString += "\n";
	filename = fileString.substring(0, fileString.indexOf("\n"));
	fileString = fileString.substring(fileString.indexOf("\n")+1);
	try { 
	    FileWriter fstream = new FileWriter(directory + filename);
	    BufferedWriter out = new BufferedWriter(fstream);
	    while (fileString.length() > 1) {
		out.write(fileString.substring(0, fileString.indexOf("\n")) + "\n");
		fileString = fileString.substring(fileString.indexOf("\n")+1);
	    }
	    out.close();
	    
	    return FILE_FLAG + "\n" + FILE_FLAG + "\n";
	} catch (Exception e) {
	    System.err.println("Error :" + e.getMessage());
	    return FILE_FLAG+ "\n" + ERROR_FLAG + "\n";
	}
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

	String clientSentence;
	String capitalizedSentence;
	int flag;
	int size;
	ArrayList<String> files = new ArrayList<String>();
	ServerSocket welcomeSocket = new ServerSocket(sock);
	
	while(true) {
	    
	    Socket connectionSocket = welcomeSocket.accept();
	    BufferedReader inFromClient =
		new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
	    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

	    clientSentence = inFromClient.readLine();
	    flag = Integer.parseInt(clientSentence);

	    clientSentence = inFromClient.readLine();
	    size = Integer.parseInt(clientSentence);

	    clientSentence = "";
	    for (int i = 0; i < size; i++) {
		clientSentence += inFromClient.readLine() + "\n";
	    }

	    // If client wants to search
	    if (flag == SEARCH_FLAG) {
		files.clear();

		// search
		outToClient.writeBytes(Search(clientSentence, directory));

		// if client wants to download found files
		if (FILE_FLAG == Integer.parseInt(inFromClient.readLine())) {
		    size = Integer.parseInt(inFromClient.readLine());
		    for (int i = 0; i < size; i++) {
			files.add(inFromClient.readLine());
		    }
		    System.out.println(files);
		    
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