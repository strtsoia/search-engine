//GUI interface for testing spelling correction functionality
package gui;

import algorithm.SpellCorrect;
import main.Program;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;

import java.lang.Short;
import java.util.*;

public class SpellCorrectWindow implements Runnable{

    ///Event Handlers///

    //callback for click event on the search button
    class CorrectButtonListener implements ActionListener{
	    //reference to input
	    JTextField textField;
        //reference to output
	    SpellCanidatesTable tblMdl;

	    //constructor
	    public CorrectButtonListener(JTextField textFieldIn,
                                    SpellCanidatesTable tblMdlIn){
		    textField = textFieldIn;
		    tblMdl = tblMdlIn;
	    }
	    //callback handler
	    public void actionPerformed(ActionEvent e){
		    //get the text in the text field
		    String word = textField.getText();

            //clear previous table entries
            tblMdl.Clear();

            //perform spelling correction algorithm and return results
            SpellCorrect correction = new SpellCorrect(word);
            Vector<String> candidates = correction.getCandidates();

            if(candidates.size() < 1)
               	tblMdl.AddCanidate("No Candidates found", 0, 0);
            else
            {
                for(int i=0; i<candidates.size(); ++i)
                {
                	String candidate = candidates.elementAt(i);
                	int editDistance = correction.getEditDistances().get(candidate);
                    tblMdl.AddCanidate(candidate,
                    			correction.get2GramDistance(candidate),
                    			editDistance);
                }
            }

	    }
    }

    //callback for enter key event on the text entry field
    private class EntryEnterKeyListener extends KeyAdapter{
        CorrectButtonListener correctButtonListener;

        public EntryEnterKeyListener(CorrectButtonListener sblIn){
            correctButtonListener = sblIn;
        }

        public void keyReleased(KeyEvent e){
            if(e.getKeyCode() == KeyEvent.VK_ENTER)
                correctButtonListener.actionPerformed(
                    new ActionEvent(this, 0, ""));
        }
    }
    
    //table model for the JTable display
    class SpellCanidatesTable extends AbstractTableModel{
    	private static final long serialVersionUID = 1L; //stop warn message
    	public Vector<String> canidates = new Vector<String>();
    	public Vector<Integer> twoGrmDists = new Vector<Integer>();
    	public Vector<Integer> editDists = new Vector<Integer>();
    	public String getColumnName(int col){
    		if(col == 0)
    			return "Canidate";
    		if(col == 1)
    			return "2 Gram Distance";
    		else
    			return "Edit Distance";
    	}
    	public String getValueAt(int row, int col){
    		if(col == 0)
    			return canidates.elementAt(row);
    		if(col == 1)
    			return twoGrmDists.elementAt(row).toString();
    		else
    			return editDists.elementAt(row).toString();
    	}
    	public int getRowCount(){return canidates.size();}
    	public int getColumnCount(){return 3;}
    	public boolean isCellEditable(int row, int col){return false;}
    	public void AddCanidate(String canidate, int twoGrmDistance, int editDist){
			canidates.add(canidate);
			twoGrmDists.add((Integer)twoGrmDistance);
			editDists.add((Integer)editDist);
			int newRow = canidates.size()-1;
			//tell the renderer we've inserted a row
    		fireTableRowsInserted(newRow, newRow);
    	}
    	public void Clear(){
    		canidates.clear();
    		twoGrmDists.clear();
    		editDists.clear();
    		//tell the renderer we've cleared all the rows
    		fireTableDataChanged();
    	}
    }

    ///class attributes///

    String selectedFile = "None";
    Vector<Integer> docIDs = new Vector<Integer>();

    //returned documents list model
    SpellCanidatesTable tblMdl = new SpellCanidatesTable();

	//window components
    JFrame f = new JFrame("Spelling Correction Test");
    JLabel wordFieldLabel = new JLabel("Misspelled word:");
	JTextField textField = new JTextField("");
	JButton correctButton = new JButton("Correct");
    JLabel matchesLabel = new JLabel("Closest Matches:");
    JTable matchesTable = new JTable(tblMdl);
    JScrollPane scrollPane = new JScrollPane(matchesTable);
	JProgressBar progressBar = new JProgressBar(0,100);
    Box wordLabelBox = new Box(BoxLayout.Y_AXIS);
    Box wordBox = new Box(BoxLayout.X_AXIS);
    Box matchesLabelBox = new Box(BoxLayout.X_AXIS);
    Box mainBox = new Box(BoxLayout.Y_AXIS);

    //callback handlers
    CorrectButtonListener cbListener =
            new CorrectButtonListener(textField, tblMdl);
	
	public void run(){
        //set the icon image and layout manager of the rootPane
        f.setIconImage(Program.windowIcon);
        f.setLayout(new BorderLayout());
        
        //configure the result table
        matchesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		matchesTable.getColumnModel().getColumn(0).setPreferredWidth(175);
		matchesTable.getColumnModel().getColumn(1).setPreferredWidth(25);
		matchesTable.setShowGrid(false);
		matchesTable.setFillsViewportHeight(true);

        Dimension minDim = new Dimension(25, 10);
        Dimension prefDim = new Dimension(150, 10);
        Dimension maxDim = new Dimension(Short.MAX_VALUE, 10);

        //configure components
        textField.setMaximumSize(new Dimension(500, 30));
        textField.setPreferredSize(new Dimension(500, 30));
        textField.setMinimumSize(new Dimension(50, 30));
        mainBox.setBorder(new EmptyBorder(new Insets(10,10,10,10)));
        wordFieldLabel.setBorder(new EmptyBorder(new Insets(0,0,5,0)));
        matchesLabel.setBorder(new EmptyBorder(new Insets(5,0,5,0)));
		
		//connect all of the window components
        wordLabelBox.add(wordFieldLabel);
        wordLabelBox.setAlignmentX(Component.LEFT_ALIGNMENT);

		wordBox.add(textField);
        wordBox.add(new Box.Filler(minDim, prefDim, maxDim));
        wordBox.add(correctButton);
        wordBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        matchesLabelBox.add(matchesLabel);
        matchesLabelBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);

        mainBox.add(wordLabelBox);
        mainBox.add(wordBox);
        mainBox.add(matchesLabelBox);
        matchesLabelBox.setBorder(new EmptyBorder(new Insets(0,0,0,10)));
		mainBox.add(scrollPane);

        f.add(mainBox, BorderLayout.CENTER);
		
		//register event listeners
        textField.addKeyListener(
            new EntryEnterKeyListener(cbListener));

		correctButton.addActionListener(cbListener);
		
		//finish setting up the window
		f.pack();
        f.setSize(400, 300);
        f.setLocationByPlatform(true);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setVisible(true);
	}
}

