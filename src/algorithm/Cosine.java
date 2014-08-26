package algorithm;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import dict.Dictionary;

public class Cosine {

	private double totalCollection;
	Hashtable<String, Double> vectorDoc1 = new Hashtable<String, Double>();
	Hashtable<String, Double> vectorDoc2 = new Hashtable<String, Double>();
	
	ArrayList<String> doc1Term = new ArrayList<String>();
	ArrayList<String> doc2Term = new ArrayList<String>();
	
	String doc1;
	String doc2;
	
	Porterstem pstem = new Porterstem();
	final String puncReg = "[;]|[?]|,|!|[.]|[']|[)]|[(]|[:]|[-]|[ ]|[\"]"; // regex	
	
	double cosinResult = 0.0;
	
	public Cosine(int docID1, int docID2)
	{
		doc1 = Dictionary.documents.get(29);
		doc2 = Dictionary.documents.get(72);
		totalCollection = (double)Dictionary.documents.size();
		
		BuildVector1(doc1);
		BuildVector2(doc2);

		System.out.println("AAA: " + vectorDoc1.size());
		GenerateCosine();
		System.out.println("The similarity is: " + this.cosinResult);
	}
	
	public double getSimilarity(){
		return cosinResult;
	}
	
	private void BuildVector1(String doc)
	{
		StringTokenizer st = new StringTokenizer(doc);
		while(st.hasMoreTokens()){
			String token = st.nextToken();
			token = token.toLowerCase();
			token = removePunctuation(token);
			token = pstem.applyPorterStem(token);
			doc1Term.add(token);
			
			double idft = GenerateIdft(token);
			vectorDoc1.put(token, idft);
			if(vectorDoc1.contains(token)){
				double value = vectorDoc1.get(token);
				value += GenerateIdft(token);
				vectorDoc1.put(token, value);
			}else{
				double value = GenerateIdft(token);
				System.out.println("The value is: " + value);
				vectorDoc1.put(token, value);
			}
		}
	}
	
	private void BuildVector2(String doc)
	{
		StringTokenizer st = new StringTokenizer(doc);
		while(st.hasMoreTokens()){
			String token = st.nextToken();
			token = token.toLowerCase();
			token = removePunctuation(token);
			token = pstem.applyPorterStem(token);
			doc2Term.add(token);
			
			double idft = GenerateIdft(token);
			vectorDoc2.put(token, idft);
			if(vectorDoc1.contains(token)){
				double value = vectorDoc2.get(token);
				value += GenerateIdft(token);
				vectorDoc2.put(token, value);
			}else{
				double value = GenerateIdft(token);
				System.out.println("The value is: " + value);
				vectorDoc2.put(token, value);
			}
		}
	}
	
	
	
	private Double GenerateIdft(String token)
	{
		Hashtable<Integer, ArrayList<Integer>> table = Dictionary.termDict.get(token);
		if(table != null){
			Set<Integer> kSet = table.keySet();
			
			// the number of documents for which t occurs in
			double totalTimes = kSet.size();
			
			double idft = Math.log(totalCollection / totalTimes);
			return idft;
		}
		
		return 0.0;
		
	}
	
	private void GenerateCosine()
	{
		double absDoc1 = 0.0;
		double absDoc2 = 0.0;
		double vMv = 0.0;
		
		Set<String> kSet1 = this.vectorDoc1.keySet();
		Iterator<String> iter1 = kSet1.iterator();
		while(iter1.hasNext()){
			String token = iter1.next();
			double value = this.vectorDoc1.get(token);
			absDoc1 += Math.pow(value, 2);
		}
		absDoc1 = Math.sqrt(absDoc1);
		
		Set<String> kSet2 = this.vectorDoc2.keySet();
		Iterator<String> iter2 = kSet2.iterator();
		while(iter2.hasNext()){
			String token = iter2.next();
			double value = this.vectorDoc2.get(token);
			absDoc2 += Math.pow(value, 2);
		}
		
		absDoc2 = Math.sqrt(absDoc2);
		
		for(int index = 0; index < doc1Term.size(); index++)
		{
			if(doc2Term.contains(doc1Term.get(index)))
			{
				String token = doc1Term.get(index);
				vMv += vectorDoc1.get(token);
			}
		}
		
		cosinResult = vMv / (absDoc1 * absDoc2);
		
	}
	
	// Remove punctuations from the query
    private String removePunctuation(String token)
    {
    	System.out.println(token);
        Pattern p = Pattern.compile(puncReg);
        Matcher m = p.matcher(token);
        return m.replaceAll("");
        
    }
}
