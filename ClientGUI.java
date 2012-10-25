import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

/** 
    Client for CSE 667 Project 2
    Team 4:
    Peter McLarhan
    Benjamin Rogers
    Jun Wang
 **/
class ClientGUI extends JFrame implements ActionListener{

    static int SEARCH_FLAG = 0;
    static int FILE_FLAG = 1;
 
    String host = "localhost";
    int sock = 6789;

    JPanel pane = new JPanel();
    JTextField searchText = new JTextField(20);
    JButton searchButton = new JButton("Search");
    JTextField fileText = new JTextField(20);
    JButton fileButton = new JButton("Upload File");

    // sets up a very basic GUI
    ClientGUI() {
	super("Remote Storage Client");
	setBounds(100,100,300,200);
	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	Container con = this.getContentPane();
	con.add(pane);
	searchText.addActionListener(this);
	searchButton.addActionListener(this);
	fileText.addActionListener(this);
	fileButton.addActionListener(this);
	pane.add(searchText);
	pane.add(searchButton);
	pane.add(fileText);
	pane.add(fileButton);
	setVisible(true);
    }

    // When buttons are pressed
    public void actionPerformed(ActionEvent event) {

	ArrayList<String> message = new ArrayList<String>();
	message.clear();
	
	Object source = event.getSource();

	// if a search is requested
	// TODO
	if (source == searchButton) {

	    message.add(searchText.getText());
	    
	    // Send the search text
	    try {
		connect(host, sock, message, SEARCH_FLAG);
	    } catch (Exception e) {
		System.err.println("Execption: " + e.getMessage());
		JOptionPane.showMessageDialog(null, e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
	    }
	     setVisible(true);
	}

	// if a file upload is requested
	// TODO
	if (source == fileButton) {

	    // Read in the file
	    try {
		ReadFile(fileText.getText(), message);
	    } catch (Exception e) {
		System.err.println("Execption: " + e.getMessage());
		JOptionPane.showMessageDialog(null, e.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
		message.add("ERROR");
	    }
	    
	    // Send the file text
	    try {
		connect(host, sock, message, FILE_FLAG);
	    } catch (Exception e) {
		System.err.println("Execption: " + e.getMessage());
		JOptionPane.showMessageDialog(null, e.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
	    }
	     setVisible(true);
	}
    }

    // reads in a file, the lines are added the the message arraylist
    public void ReadFile(String filename, ArrayList<String> message) throws Exception{

	message.add(filename);

	FileInputStream fstream = new FileInputStream(filename);
	DataInputStream in = new DataInputStream(fstream);
	BufferedReader br = new BufferedReader(new InputStreamReader(in));
	String strLine;
	while ((strLine = br.readLine()) != null) {
	    message.add(strLine);
	}
    }

    // connects to the server and sends the message
    public void connect(String host, int sock, ArrayList<String> message, int flag) throws Exception {

	String sentence;
	String modifiedSentence;
	BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
	Socket clientSocket = new Socket(host, sock);
	DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
	BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

	sentence = flag + "\n" + message.size() + "\n";
	for (int i = 0; i < message.size(); i++) {
	    sentence += message.get(i) + "\n";
	}

	outToServer.writeBytes(sentence);
	modifiedSentence = inFromServer.readLine();
	System.out.println("FROM SERVER: " + modifiedSentence);
	clientSocket.close();

    }

    // initializes the GUI
    public static void main(String argv[]){

	new ClientGUI();

    }
}