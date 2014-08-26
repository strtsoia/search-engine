//MemWindow - window to display what the dictionary memory usage would be
//using; gap encoding, Huffman encoded gap encoding,
//and variable byte encoded gap encoding

package gui;

import main.Program;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import dict.Dictionary;


public class MemUsageWindow implements Runnable {
	
	JFrame f = new JFrame("Estimated Memory Usages");
	JLabel rawEncLabel = new JLabel("Raw: 0.00 KB");
	JLabel gapEncLabel = new JLabel("Gap Encoding: 0.00 KB");
	JLabel vbEncLabel = new JLabel("VB Gap Encoding: 0.00 KB");
	JLabel huffEncLabel = new JLabel("Huffman Gap Encoding: 0.00 KB");
	
	Box mainBox = new Box(BoxLayout.Y_AXIS);
	
	public MemUsageWindow(){
		//fill out the memory usages when the window is built
		
		//get the estimates from the dictionary
		double rawMem = Dictionary.GetMemUsageRaw();
		double gapMem = Dictionary.GetMemUsageGapEnc();
		double huffMem = Dictionary.GetMemUsageHuffman();
		double vbMem = Dictionary.GetMemUsageVBEnc();
		
		//set the labels
		rawEncLabel.setText("Raw: " +
					String.format("%(,.2f", rawMem)+ " KB");
		gapEncLabel.setText("Gap Encoding: " +
					String.format("%(,.2f", gapMem)+ " KB");
		vbEncLabel.setText("VB Gap Encoding: " +
					String.format("%(,.2f", vbMem)+ " KB");
		huffEncLabel.setText("Huffman Gap Encoding: " +
					String.format("%(,.2f", huffMem)+ " KB");
		
	}
	
	public void run(){
        //set the icon image and layout manager of the rootPane
        f.setIconImage(Program.windowIcon);
        f.setLayout(new BorderLayout());
        
        //set component borders
        mainBox.setBorder(new EmptyBorder(new Insets(5,10,10,5)));
		
        //set component alignment
        rawEncLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        gapEncLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        huffEncLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        vbEncLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        mainBox.add(rawEncLabel);
        mainBox.add(gapEncLabel);
        mainBox.add(vbEncLabel);
        mainBox.add(huffEncLabel);
        
        f.add(mainBox, BorderLayout.CENTER);
		
        //final setup and show of the window
        f.pack();
        f.setSize(250, f.getSize().height);
        f.setLocationByPlatform(true);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setVisible(true);
	}

}
