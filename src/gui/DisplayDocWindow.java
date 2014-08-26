//GUI window for displaying a document matching a search query

package gui;

import main.Program;

import java.awt.*;
import javax.swing.*;

class DisplayDocWindow implements Runnable{
    MainWindow mw;

    JFrame f = new JFrame("");
    JTextArea textArea = new JTextArea();
    JScrollPane sp = new JScrollPane(textArea);

    public DisplayDocWindow(String docID, String doc){
        //set the docID and the document text here to avoid extra attributes
        textArea.setText(doc);
        f.setTitle("Document Window: " + docID);
    }

    //swing constructor
    public void run(){
        //set the window icon
        f.setIconImage(Program.windowIcon);

        //set the style of the text area
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);

        //add the text area to the main window
        f.add(sp);

        //pack, set size, let the window manager intelligently position the
        //window set what happens to the window when its closed and show the
        //window
        f.pack();
        f.setSize(new Dimension(500,300));
        f.setLocationByPlatform(true);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        f.setVisible(true);
    }
}
