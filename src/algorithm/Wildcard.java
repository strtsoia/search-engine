package algorithm;


import java.util.Vector;
import java.util.Iterator;
import java.util.Set;

import dict.Dictionary;

public class Wildcard {

	private Vector<String> grams = new Vector<String>();
	private Vector<String> candidate = new Vector<String>();
	private Vector<String> matches = new Vector<String>();
	private String wildcardStr = "";
	
	public Wildcard(String query)
	{
		wildcardStr = query;
		makeGrams();
		getCandidate();
		checkOrder();
	}
	
	//returns the 2 grams of the wildcard phrase
	public Vector<String> GetGrams()
	{
		return grams;
	}
	//returns the words that match the wildcard phrase
	public Vector<String> GetMatches()
	{
		return matches;
	}

	// get one-gram and two-gram
	private void makeGrams()
	{
		String str = "$" + wildcardStr + "$";
		String[] gram = null;
		
		gram = str.split("\\*");
		for(int i = 0; i < gram.length; i++){
			if(gram[i].length() == 1 || gram[i].length() == 2)
				grams.add(gram[i]);
			else if(gram[i].length() >= 3)
					DivideToTwoGram(gram[i]);	
		}
		grams.remove("$");
	}
	
	private void DivideToTwoGram(String gram)
	{
		for(int i = 0; i < gram.length() - 1; i++)
		{
			String g = gram.substring(i, i+2);
			grams.add(g);
		}
	}
	
	private void getCandidate()
	{
		boolean bCandidate = true; // checks if the type is a candidate
		Set<String> kset = Dictionary.typeDict.keySet();
		Iterator<String> iter = kset.iterator();
		
		// iterator all types
		while(iter.hasNext()){
			String type = iter.next().toString();
			type = "$" + type + "$";
			String temp = type;
			
			// iterates grams
			Iterator<String> it = grams.iterator();
			while(it.hasNext()){
				String gram = it.next().toString();
				if(temp.contains(gram))
					temp.replaceFirst(gram, "");
				else{
					bCandidate = false;
					break;
				}	
			}
			
			// add this type to candidate
			if(bCandidate)
				candidate.add(type);
			else
				bCandidate = true;
		}	
	}
	
	private void checkOrder()
	{
		Iterator<String> iter = candidate.iterator();
		
		// iterate all candidates
		while(iter.hasNext()){
			String type = iter.next().toString();
			int index = 0;
			boolean bCandidate = true;
			
			// compare gram in order
			for(int i = 0; i < grams.size(); i++)
			{
				boolean bMatch = false;
				String gram = grams.elementAt(i);
				int len = gram.length();
				 
				while((index + len) <= type.length()){
					String st = type.substring(index, index+len);
					if(st.equals(gram)){
						bMatch = true;
						index++;
						break;
					}else{
						index++;
					}
				}
				
				if(!bMatch){
					bCandidate = false;
					break;
				}
			}
			
			if(bCandidate)
			{
				type = type.substring(1, type.length()-1);
				matches.add(type);
			}
		}
	}
	
}
