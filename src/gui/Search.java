//Search - the swing worker class that runs in a separate thread when the user
//runs a search query
package gui;

import query.RQueryEx;

import java.util.*;

import javax.swing.*;

class DocScore{
	public Integer docID;
	public Double score;
	public DocScore(Integer docID, Double score){
		this.docID = docID;
		this.score = score;
	}
}

//SwingWorker<T,V> where
//T is the type returned by doInBackground and get
//V is the type returned by publish received by the process methods
//(not using either of these, so just set these to generic object)
class Search extends SwingWorker<Object, Object>{
	String searchString;
	boolean reqAllToks;
	
	//constructor, receive the string to search for
	public Search(String in, boolean reqAllToks){
		searchString = in;
		this.reqAllToks = reqAllToks;
	}
	
    //the function scheduled for asynchronous execution by the execute call in
    //MainWindow
	@Override
	public Object doInBackground(){
		try{
			RQueryEx q = new RQueryEx(searchString, reqAllToks);
            Vector<Integer> queryResult = new Vector<Integer>();
            
            queryResult = q.getResult();
            System.out.println(queryResult);
            q.GenerateScore();
            Hashtable<Integer, Double> scoreTable = q.getScoreTable();

            //return a negative 1 if no documents were found
            if(queryResult.size()<1)
            {
            	DocScore ret = new DocScore(-1, -1.0);
                firePropertyChange("document", "", ret);
            }
			
			//asynchronously return the docID results (ints)
			for(int i=0; i<queryResult.size(); ++i)
			{
				firePropertyChange("progress", "",
                                (int)(100.0*(i/(float)queryResult.size())));
				
				int docID = queryResult.elementAt(i);
				double score = scoreTable.get(docID);
				DocScore ret = new DocScore(docID, score);
                firePropertyChange("document", "", ret);
			}
		}
		catch(Exception e){
			System.out.println("exception during Search: " + e);
            //return that no documents were returned
			DocScore ret = new DocScore(-1, -1.0);
            firePropertyChange("document", "", ret);
		}

        //100% done
        firePropertyChange("progress", "", 100);
        MainWindow.GetInstance().SetStatusIdle();
		return null;
	}
}

