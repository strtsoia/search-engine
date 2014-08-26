//main program
package main;

import gui.*;

import java.io.File;
import java.awt.image.BufferedImage;
import javax.swing.*;
import javax.imageio.ImageIO;

public class Program {
    //icon image for windows
    public static BufferedImage windowIcon = null;
	
	public static void main(String args[])
	{
        //create the main program window
        try{
            //set the look and feel to the system default
            UIManager.setLookAndFeel(
                 UIManager.getSystemLookAndFeelClassName());
            //load the images
            windowIcon = ImageIO.read(new File("src/images/mochaLogoOnlySmall.png"));
            //open the main window
		    SwingUtilities.invokeLater(MainWindow.GetInstance());
        }
        catch(Exception e){
            System.out.println("Unhandled Exception in main: " + e);
        }
	}
}
