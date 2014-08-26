//GUI interface for testing the wildcard function
package gui;

import algorithm.Wildcard;
import main.Program;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import java.lang.Short;
import java.util.*;

public class WildcardWindow implements Runnable{

    ///Event Handlers///

    //callback for click event on the search button
    class FindButtonListener implements ActionListener{
	    //reference to input
	    JTextField textField;
        //reference to output
	    DefaultListModel closestMatchesModel;
	    JTextField gramsField;

	    //constructor
	    public FindButtonListener(JTextField textFieldIn,
	    						DefaultListModel closestMatchesModelIn,
	    						JTextField gramsFieldIn){
		    textField = textFieldIn;
            closestMatchesModel = closestMatchesModelIn;
            gramsField = gramsFieldIn;
	    }
	    //callback handler
	    public void actionPerformed(ActionEvent e){
		    //get the wildcard text in the text field
		    String word = textField.getText();

            //clear previous matches
            closestMatchesModel.clear();

            //perform wildcard search algorithm and return results
            Wildcard wildcard = new Wildcard(word);
            
            Vector<String> grams = wildcard.GetGrams();
            String gramsStr = "";
            for(int i=0; i<grams.size(); ++i)
            	gramsStr += grams.elementAt(i) + " ";
            gramsField.setText(gramsStr);
            
            Vector<String> matches = wildcard.GetMatches();
            if(matches.size() < 1)
                closestMatchesModel.addElement("No Candidates found");
            else
                for(int i=0; i<matches.size(); ++i)
                    closestMatchesModel.addElement(matches.elementAt(i));

	    }
    }

    //callback for enter key event on the text entry field
    private class EntryEnterKeyListener extends KeyAdapter{
        FindButtonListener correctButtonListener;

        public EntryEnterKeyListener(FindButtonListener sblIn){
            correctButtonListener = sblIn;
        }

        public void keyReleased(KeyEvent e){
            if(e.getKeyCode() == KeyEvent.VK_ENTER)
                correctButtonListener.actionPerformed(
                    new ActionEvent(this, 0, ""));
        }
    }
    
    ///class attributes///

    String selectedFile = "None";
    Vector<Integer> docIDs = new Vector<Integer>();

    //returned documents list model
    DefaultListModel matchesModel = new DefaultListModel();

	//window components
    JFrame f = new JFrame("Wildcard Test");
    JLabel wordFieldLabel = new JLabel("Wildcard Query:");
	JTextField textField = new JTextField("");
	JLabel gramsLabel = new JLabel("2 Grams:");
	JTextField grams = new JTextField("");
	JButton correctButton = new JButton("Find");
    JLabel matchesLabel = new JLabel("Matches:");
	JList matchesList = new JList(matchesModel);
    JScrollPane scrollPane = new JScrollPane(matchesList);
	JProgressBar progressBar = new JProgressBar(0,100);
    Box wordLabelBox = new Box(BoxLayout.Y_AXIS);
    Box wordBox = new Box(BoxLayout.X_AXIS);
    Box matchesLabelBox = new Box(BoxLayout.X_AXIS);
    Box mainBox = new Box(BoxLayout.Y_AXIS);

    //callback handlers
    FindButtonListener fbListener =
            new FindButtonListener(textField, matchesModel, grams);
	
	public void run(){
        //set the icon image and layout manager of the rootPane
        f.setIconImage(Program.windowIcon);
        f.setLayout(new BorderLayout());
        
        //configure the result table
        matchesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Dimension minDim = new Dimension(25, 10);
        Dimension prefDim = new Dimension(150, 10);
        Dimension maxDim = new Dimension(Short.MAX_VALUE, 10);

        //configure components
        textField.setMaximumSize(new Dimension(500, 30));
        textField.setPreferredSize(new Dimension(500, 30));
        textField.setMinimumSize(new Dimension(50, 30));
        mainBox.setBorder(new EmptyBorder(new Insets(10,10,10,10)));
        wordFieldLabel.setBorder(new EmptyBorder(new Insets(0,0,5,0)));
        gramsLabel.setBorder(new EmptyBorder(new Insets(5,0,5,0)));
        matchesLabel.setBorder(new EmptyBorder(new Insets(5,0,5,0)));
        matchesLabelBox.setBorder(new EmptyBorder(new Insets(0,0,0,10)));
        grams.setMaximumSize(new Dimension(500, 30));
        grams.setPreferredSize(new Dimension(50, 30));
        grams.setMinimumSize(new Dimension(50, 30));
        grams.setEditable(false);
        
        wordLabelBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        wordBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        gramsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        grams.setAlignmentX(Component.LEFT_ALIGNMENT);
        matchesLabelBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        //connect all of the window components
        wordLabelBox.add(wordFieldLabel);

        wordBox.add(textField);
        wordBox.add(new Box.Filler(minDim, prefDim, maxDim));
        wordBox.add(correctButton);

        matchesLabelBox.add(matchesLabel);

        mainBox.add(wordLabelBox);
        mainBox.add(wordBox);
        mainBox.add(gramsLabel);
        mainBox.add(grams);
        mainBox.add(matchesLabelBox);
		mainBox.add(scrollPane);

        f.add(mainBox, BorderLayout.CENTER);
		
		//register event listeners
        textField.addKeyListener(new EntryEnterKeyListener(fbListener));
		correctButton.addActionListener(fbListener);
		
		//finish setting up the window
		f.pack();
        f.setSize(400, 300);
        f.setLocationByPlatform(true);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setVisible(true);
	}
}

