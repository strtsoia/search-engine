// GUI to display Term Frequency Chart
package gui;

import main.Program;

import java.awt.BorderLayout;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import dict.Dictionary;

public class TermFrequencyChart implements Runnable{
	//window components
	JFrame f = new JFrame("Term Frequency Chart");

	public TermFrequencyChart() {
		//build the chart
        final XYSeries series = new XYSeries("");
        for(int index = 0; index < Dictionary.GetTermFrequencies().length; index++)
        {
        	series.add(index, Dictionary.GetTermFrequencies()[index]);
        }
        final XYSeriesCollection data = new XYSeriesCollection(series);
        final JFreeChart chart = ChartFactory.createXYLineChart(
            "Term Frequency Chart",
            "The number of terms", 
            "probability", 
            data,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
        
        //adjust layout and add to rootPane
        chartPanel.setBorder(new EmptyBorder(new Insets(10,10,10,10)));
        f.setLayout(new BorderLayout());
        f.add(chartPanel, BorderLayout.CENTER);
    }
	
	public void run(){
		//set the icon image of the rootPane
        f.setIconImage(Program.windowIcon);
        
		//finish setting up the window
		f.pack();
        f.setLocationByPlatform(true);
		f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		f.setVisible(true);
	}
}
