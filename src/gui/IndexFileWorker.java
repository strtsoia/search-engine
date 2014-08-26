// Builds the term and type dictionaries 
package gui;

import gui.MainWindow;

import algorithm.Parser;
import algorithm.Porterstem;
import io.IOConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.SwingWorker;

import query.RQueryEx;

import dict.Dictionary;

public class IndexFileWorker extends SwingWorker<Object, Object>{
	String fileName;
	
	public IndexFileWorker(String in){
		fileName = in;
		setProgress(0);
	}
	
	@Override
	public Object doInBackground(){
        buildDictionaries(fileName);
        //set the status text in the main window back to idle
		MainWindow.GetInstance().SetStatusIdle();
            
		return null;
	}
	
	private void buildDictionaries(String fileName){
		//define vars needed from main
	    Vector<String> documents = Dictionary.documents;
	    Vector<Integer> tokenTermRatio = Dictionary.tokenTermRatio;
		
		
		IOConnection ioc = new IOConnection(); // used to open document file.
		Parser p = new Parser();
		Porterstem porter = new Porterstem();

        //clear the Dictionary
        Dictionary.clear();
		
        // use for tokens/terms ratio
        int numOfTerms = 0;
        
		// READ FILE THEN PARSE IT INTO A LIST OF DOCUMENTS
		try {
			BufferedReader br = new BufferedReader(ioc.openFile(fileName));
			

			String line = br.readLine(); // Read first line of the file.
			String paragraph = ""; // paragraph represents a document in the file.
			
			while(line != null) // keep going until you reach end of file.
			{
				// Parse file into a list of documents
				if(!line.equals("")){
					paragraph += line + " ";
				}
				else {
					if (!paragraph.equals(""))
						documents.add(paragraph);
					paragraph = "";
				}
				line = br.readLine(); // Read the next line.
			}
			documents.add(paragraph); // Add the last document to the list.
			
			br.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		// PARSE EACH DOCUMENT INTO TOKENS --> TYPES --> TERMS
		// CREATE A TYPE DICTIONARY AND TERM DICTIONARY
		int docID = 0; // Counter for document ID.
		
		Iterator<String> it = documents.iterator();
		
		// Parse each document into tokens and create dictionaries in the process
		while (it.hasNext())
		{
			docID++;
			
			//update the progress bar in the GUI
			firePropertyChange("progress", "",
						(int)(100.0*((float)docID/(float)documents.size())));
			
			int posID = 0; // Counter for position ID.
			p.parse((String)it.next()); // Parse document into tokens
			Vector<String> tokens = p.getTokens(); // get tokens and store it in the list.

			Iterator<String> it2 = tokens.iterator();
	
			// Generate terms from each token
			// Also, build the type and term dictionary.
			while(it2.hasNext())
			{
				posID++; 
				String type = (String)it2.next(); 
				
				// Apply the porter stemmer
				String term = porter.applyPorterStem(type);
				
				Dictionary.addTerm(term, docID, posID); // add term into the term dictionary.
				Dictionary.addType(type, term); // add type into the type dictionary.
			}
		}
		
		//calculate gap table needed for calculating memory usage
		Dictionary.GenerateGapTable();
		
		/*RQueryEx r= new RQueryEx();
		r.GenerateScore();*/
	}

}

