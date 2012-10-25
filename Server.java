import java.io.*;
import java.net.*;

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

    // Search handles searches of "searchString" and outputs a string
    public static String Search(String searchString) {
	//TODO
	return "Searched for " + searchString + "\n";
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
	} catch (Exception e) {
	    System.err.println("Error :" + e.getMessage());
	}

	return "Wrote file " + fileString + "\n";
    }
    
    // main communicates with the client
    public static void main(String argv[]) throws Exception {
	
	String directory;
	if (argv.length > 0) {
	    directory = argv[0];
	} else {
	    directory = "test/";
	}
	String clientSentence;
	String capitalizedSentence;
	int flag;
	int size;
	ServerSocket welcomeSocket = new ServerSocket(6789);
	
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

	    if (flag == SEARCH_FLAG) {
		outToClient.writeBytes(Search(clientSentence));
	    }

	    if (flag == FILE_FLAG) {
		outToClient.writeBytes(WriteFile(clientSentence, directory));
	    }
	}
    }
}