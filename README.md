encrypted-search
================

Implementation of a client-server scheme for searches on encrypted data. CSE 667 project.

Peter McLarnan, Benjamin Rogers, Jun Wang

Walkthrough
--

1. First, adjust the working directory settings in `client.config` and `server.config`, to point to 

2. Run Server and ClientGUI.  On first run, the client generates keys and stores them in its config file.

3. Type `example.txt` into the third box on the left, and press the *Upload File* button. This will encrypt the file and send it to the server.

4. When you have finished uploading files to the server, search for word matches by typing in a search word in the second box on the left, and press the  *Search* button. This will send the search word to the server wich will search all documents for a matching word, returning the names of the files with matches.

5. The return dialog will list the files and ask if you would like to download them. If you choose download, the files will be sent to the client and then decrypted.