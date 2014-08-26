package io;
import java.io.*;

public class IOConnection{
	
	// Default Constructor
	public IOConnection(){}
	
	// open file and return IO
	public FileReader openFile(String fName) {
		
		File f = new File(fName);
		
		// Print an error message and exit if file does not exist
		if( !f.exists()) 
		{
			System.err.println("ERROR: File does not exist!");
			System.exit(0);
		}
		
		try {
			FileReader fr = new FileReader(fName);
			return fr;
		} catch(IOException e){
			e.printStackTrace();
		}
		
		return null;
	}
	
}
