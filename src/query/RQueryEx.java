package query;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import algorithm.Porterstem;
import dict.Dictionary;

public class RQueryEx {
	
	String queryStr;
	double totalDocNumber;
	ArrayList<String> queryToken = new ArrayList<String>();
	Vector<Integer> queryResult = new Vector<Integer>();
	Porterstem pstem = new Porterstem();
	final String puncReg = "[;]|[?]|,|!|[.]|[']|[)]|[(]|[:]";    // regex
	
	Hashtable<String, Double> idftTable = new Hashtable<String, Double>();
	// Integer is docID, Double is score for this docID
	Hashtable<Integer, Double> scoreTable = new Hashtable<Integer, Double>();
	
	public RQueryEx(String queryStr, boolean reqAllToks)
	{
		this.queryStr = queryStr;
		QueryEx q = new QueryEx(queryStr, reqAllToks);
		
		queryResult = q.getResult();
		totalDocNumber = (double)Dictionary.documents.size();
		spliteQueryString();
	}
	
	public Vector<Integer> getResult()
	{
		return queryResult;
	}
	
	public Hashtable<Integer, Double> getScoreTable()
	{
		return this.scoreTable;
	}
	// splite query string into token
	private void spliteQueryString()
	{
		StringTokenizer st = new StringTokenizer(queryStr);
		while(st.hasMoreTokens()){
			String token = st.nextToken();
			token = token.toLowerCase();
			token = removePunctuation(token);
			token = token.trim();
			token = pstem.applyPorterStem(token);
			queryToken.add(token);
		}
	}
	
	private void GenerateIdft()
	{
		// for each token in query string
		for(int index = 0; index < this.queryToken.size(); index++)
		{
			Hashtable<Integer, ArrayList<Integer>> table = Dictionary.termDict.get(queryToken.get(index));
			if(table.size() != 0)
			{
				Set<Integer> kSet = table.keySet();
				
				// the number of documents for which t occurs in
				double totalTimes = kSet.size();
				
				double idft = Math.log(totalDocNumber / totalTimes);
				idftTable.put(queryToken.get(index), idft);
			}
		}
	}
	
	public void GenerateScore()
	{
		GenerateIdft();
		System.out.println("size is: " + queryResult.size());
		// for each return document
		for(int i = 0; i < queryResult.size(); i++)
		{
			int currDocID = queryResult.get(i);
			double score = 0.0;
			// for each token in query string
			for(int index = 0; index < this.queryToken.size(); index++)
			{
				Hashtable<Integer, ArrayList<Integer>> table = Dictionary.termDict.get(queryToken.get(index));
				int tftd = table.get(currDocID).size();
				double idft = this.idftTable.get(queryToken.get(index));
				score += tftd * idft;
			}
			
			scoreTable.put(currDocID, score);
		}
		
	}
	
	// Remove punctuations from the query
    private String removePunctuation(String token)
    {
        Pattern p = Pattern.compile(puncReg);
        Matcher m = p.matcher(token);
        return m.replaceAll("");
        
    }
	
}
