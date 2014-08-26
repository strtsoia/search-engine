//The inverted Index statistics window
package gui;

import main.Program;

import javax.swing.*;

import dict.Dictionary;

import java.awt.*;
import java.util.*;

public class InvertedIndexWindow implements Runnable{
    //list models
    DefaultListModel invIdxModel = new DefaultListModel();

    //window components
    JFrame f = new JFrame("Inverted Index Contents Window");
    JLabel invIdxListLabel = new JLabel("Dictionary Terms: ");
    JList invIdxList = new JList(invIdxModel);
    JScrollPane invIdxScrollPane = new JScrollPane(invIdxList);
    Box mainBox = new Box(BoxLayout.Y_AXIS);

    public InvertedIndexWindow(){
        //fill invIdxModel with the items in the terms in the dictionary
		Set<String> s = Dictionary.termDict.keySet();
		Iterator<String> it = s.iterator();

        //for each key in the dictionary's keySet
        int k = 1;
		while(it.hasNext())
		{	
            String listEntry = "";
			String term = it.next().toString();
			listEntry += (k + ": " + term + " "); // Print document ID.
			
			// Acquire the list of positions for each document ID.
			Hashtable<Integer, ArrayList<Integer>> postings =
                                            Dictionary.termDict.get(term);
			ArrayList<Integer> docIDs = new ArrayList<Integer>(postings.keySet());
			Collections.sort(docIDs); // Sort the document IDs before printing
			Iterator<Integer> it2 = docIDs.iterator();
			
			// Print positions for each document ID.
			while(it2.hasNext()){
				int docID = (Integer)it2.next();			
				listEntry += (docID + ":" + postings.get(docID) + ", ");
			}	
			invIdxModel.addElement(listEntry);
			k++;
		}
    }

    public void run(){
        //set the icon image and layout manager of the rootPane
        f.setIconImage(Program.windowIcon);
        f.setLayout(new BorderLayout());

        //configure window components
        invIdxListLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        invIdxScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        //connect all of the window components
        mainBox.add(invIdxListLabel);
        mainBox.add(invIdxScrollPane);

        f.add(mainBox, BorderLayout.CENTER);

        //finish setting up the window
        f.pack();
        f.setSize(350, 300);
        f.setLocationByPlatform(true);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setVisible(true);
    }
}

