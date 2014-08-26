//GUI window for testing the porter stemmer
package gui;

import algorithm.Porterstem;
import main.Program;

import java.awt.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.Box.Filler;
import javax.swing.border.EmptyBorder;

class PorterstemWindow implements Runnable{

//callback for click event on the stem button
class StemButtonListener implements ActionListener{
	//references to stemmer input
	JTextField textField;
    //references to stemmer output
    JTextField resultField;

	//constructor
	public StemButtonListener(JTextField textFieldIn,
                                JTextField resultFieldIn){
		textField = textFieldIn;
		resultField = resultFieldIn;
	}
	//callback handler (when the user clicks on the stem button)
	public void actionPerformed(ActionEvent e){
		//now that the button has been clicked, get the text in the text field
		String text = textField.getText();

        //create a new stemmer
        Porterstem pStem = new Porterstem();
        String result = pStem.applyPorterStem(text);
        resultField.setText(result);
	}
}

//callback for enter key event on the text entry field
private class EntryEnterKeyListener extends KeyAdapter{
    StemButtonListener stemButtonListener;

    public EntryEnterKeyListener(StemButtonListener sblIn){
        stemButtonListener = sblIn;
    }

    public void keyReleased(KeyEvent e){
        if(e.getKeyCode() == KeyEvent.VK_ENTER)
            stemButtonListener.actionPerformed(
                new ActionEvent(this, 0, ""));
    }
}

    //attributes of the window
    JFrame f = new JFrame("Porter Stemmer Test");

    JLabel stemFieldLabel = new JLabel("Stemer Input:");
    JTextField textField = new JTextField("");
	JButton StemButton = new JButton("Stem");
    JLabel resultLabel = new JLabel("Stemmed Result:");
    JTextField resultField = new JTextField("");

    Box mainBox = new Box(BoxLayout.Y_AXIS);
    Box resultBox = new Box(BoxLayout.Y_AXIS);
    Box entryBox = new Box(BoxLayout.X_AXIS);

    //actionListeners
    StemButtonListener stemButtonListener =
        new StemButtonListener(textField, resultField);

    public void run(){
        //set the icon image and layout manager of the rootPane
        f.setIconImage(Program.windowIcon);
        f.setLayout(new BorderLayout());
        
        //dimensions used for empty fillers
        Dimension minDim = new Dimension(25, 0);
        Dimension prefDim = new Dimension(50, 10);
        Dimension maxDim = new Dimension(Short.MAX_VALUE, 10);

        //configure window components
        mainBox.setBorder(new EmptyBorder(new Insets(10,10,10,10)));
        textField.setPreferredSize(new Dimension(75, 30));
        resultField.setPreferredSize(new Dimension(150, 30));
        stemFieldLabel.setBorder(new EmptyBorder(new Insets(0,0,5,0)));
        resultLabel.setBorder(new EmptyBorder(new Insets(5,0,5,0)));

        //connect the window components
        stemFieldLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultField.setAlignmentX(Component.LEFT_ALIGNMENT);

        entryBox.add(textField);
        entryBox.add(new Box.Filler(minDim, prefDim, maxDim));
        entryBox.add(StemButton);
        entryBox.setAlignmentX(Component.LEFT_ALIGNMENT);

        resultBox.add(resultLabel);
        resultBox.add(resultField);
        resultBox.setAlignmentX(Component.LEFT_ALIGNMENT);
    
        Filler filler = new Box.Filler(minDim, new Dimension(300,0), maxDim);
        filler.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainBox.add(filler);
        mainBox.add(stemFieldLabel);
        mainBox.add(entryBox);
        mainBox.add(resultBox);

        f.add(mainBox, BorderLayout.CENTER);

        //register event listeners
        textField.addKeyListener(new EntryEnterKeyListener(stemButtonListener));

        StemButton.addActionListener(stemButtonListener);

        //final setup and show of the window
        f.pack();
        f.setLocationByPlatform(true);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setVisible(true);

    }
}
