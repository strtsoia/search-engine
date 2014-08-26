//Dictionary - the collection of search engine components
package dict;

import gui.MainWindow;

import java.util.*;
import java.math.*;

//static class
public final class Dictionary {
	//search index data
	// args: key = type, value = term
	public static Hashtable<String, String> typeDict = new Hashtable<String, String>();
	// args: key = term, value = posting, postings = (docID, positions the term appeared)
	public static Hashtable<String, Hashtable<Integer, ArrayList<Integer>>> termDict = 
				new Hashtable<String, Hashtable<Integer, ArrayList<Integer>>>();
	 // list of all documents
    public static Vector<String> documents = new Vector<String>();
	
	//memory statistics data
	// args: key = gap element in gap list,
	//     value = how many times this element appears in all gap lists
	//a hash of the number of occurrences of each gapSize in the dictionary
	private static Hashtable<Integer, Integer> gapTable =
										new Hashtable<Integer, Integer>();
	//the total number of gaps in the dictionary
	private static int totalNumOfGap = 0;
	public static Vector<Integer> tokenTermRatio = new Vector<Integer>();
	private static BigDecimal[] termFrequency;
	
	
	//dissalow class instantiation (static class)
	private Dictionary(){
        //incase someone calls from inside Dictionary
        throw new AssertionError();
    }
	
	public static BigDecimal[] GetTermFrequencies(){
		return termFrequency;
	}
	
	public static void addType(String type, String term)
	{	
		// if new type, add to type dictionary
		if(!typeDict.containsKey(type)) 
			typeDict.put(type, term);
	}
	
	public static void addTerm(String term, int docID, int posID)
	{
		// list of documents and positions the term appeared in.
		Hashtable<Integer, ArrayList<Integer>> postings =
							new Hashtable<Integer, ArrayList<Integer>>();
		// list of positions the term appeared in.
		ArrayList<Integer> pos = new ArrayList<Integer>(); 
		
		if(!termDict.containsKey(term)) { 	// if new term, add to dictionary
			pos.add(posID); 
			postings.put(docID, pos); 
			termDict.put(term, postings);
		} else { 						   // else...	
			postings = termDict.get(term); // get the existing postings for the term
			if (postings.get(docID) != null) // if term has previously appeared in the document
				pos = postings.get(docID); // get the existing list of positions the term appeared in.
			pos.add(posID); // append the latest position to the end of the list.
			postings.put(docID, pos); 
			termDict.put(term, postings); // update term dictionary.
		}
		
		// update tokens/terms ratio
		tokenTermRatio.add(termDict.size());
	}

	// Prints all the TYPES in the dictionary
	public static void displayTypes()
	{
		Set<String> s = typeDict.keySet();
		Iterator<String> it = s.iterator();
		
		int k = 1;
		
		while (it.hasNext())
		{
			System.out.println(k + ": " + it.next());
			k++;
		}
	}
	
	// Prints all the TERMS in the dictionary
	public static void displayTerms()
	{
		Set<String> s = termDict.keySet();
		Iterator<String> it = s.iterator();

		int k = 1;

		while(it.hasNext())
		{	
			String term = it.next().toString();
			System.out.print(k + ": " + term + " "); // Print document ID.
			
			// Acquire the list of positions for each document ID.
			Hashtable<Integer, ArrayList<Integer>> postings = termDict.get(term);
			ArrayList<Integer> docIDs = new ArrayList<Integer>(postings.keySet());
			Collections.sort(docIDs); // Sort the document IDs before printing
			Iterator<Integer> it2 = docIDs.iterator();
			
			// Print positions for each document ID.
			while(it2.hasNext()){
				int docID = (Integer)it2.next();			
				System.out.print(docID + ":" + postings.get(docID) + ", ");
			}	
			System.out.println();
			k++;
		}
	}
	
    //clear the dictionary (called when opening a new file)
    public static void clear(){
        typeDict.clear();
        termDict.clear();
        tokenTermRatio.clear();
        gapTable.clear();
        documents.clear();
    }
    
	// calculate term frequency
	public static void calcTermFrequency()
	{
		termFrequency = new BigDecimal[termDict.size()];
		BigDecimal totalDoc = new BigDecimal(documents.size());
		int index = 0;
		
		Set<String> kSet = termDict.keySet();
		Iterator<String> iter = kSet.iterator();
		
		// iterate all key
		while(iter.hasNext()){
			String key = iter.next();
			Hashtable<Integer, ArrayList<Integer>> tb = termDict.get(key);
			// how many documents this term occurs in
			Set<Integer> docIDSet = tb.keySet();
			BigDecimal numOfDoc = new BigDecimal(docIDSet.size());
			termFrequency[index] = numOfDoc.divide(totalDoc, 4, RoundingMode.HALF_UP);
			index++;
		}
		
		// sort term frequency
		Arrays.sort(termFrequency);
		Collections.reverse(Arrays.asList(termFrequency));
	}

	public static void GenerateGapTable()
	{
		//generate the gap encoding table (used by the memory usage estimation
		//functions)
		
		//get an iterator for the term dictionary
		Set<String> keySet = termDict.keySet();
		Iterator<String> termIter = keySet.iterator();

		// iterate over all terms
		while(termIter.hasNext()){
			String term = termIter.next();
			//get the postings list for the term
			Hashtable<Integer, ArrayList<Integer>> termPostingList =
														termDict.get(term);
			Set<Integer> docIDSet = termPostingList.keySet();
			Integer[] array = docIDSet.toArray(new Integer[docIDSet.size()]);
			Arrays.sort(array);
			
			// walk through the postings list
			for(int index = 0; index < array.length - 1; index++){
				//increment the total number of gaps found (for percentage later)
				totalNumOfGap++;
				//get the size of the gap between this elm and the next
				int gapSize = array[index + 1] - array[index];
				//insert the gap into the gapTable
				if(!gapTable.containsKey(gapSize)){
					gapTable.put(gapSize, 1);
				}else{
					int num = gapTable.get(gapSize);
					num++;
					gapTable.put(gapSize, num);
				}
			}
		}
	}
	
	//returns value in KB
	public static double GetMemUsageRaw(){
		Collection<Hashtable<Integer, ArrayList<Integer>>> docIDs =
			termDict.values();
		Iterator<Hashtable<Integer, ArrayList<Integer>>> postingListsIter =
			docIDs.iterator();
		
		int totalBitsUsed = 0;
		
		while(postingListsIter.hasNext()){
			Hashtable<Integer, ArrayList<Integer>> postingList = 
				postingListsIter.next();
			totalBitsUsed += postingList.size()*32;
		}
		return totalBitsUsed/8/1024;
	}
	
	//returns value in KB
	public static double GetMemUsageHuffman(){
		//calculate the memory usage using huffman encoding
		Collection<Integer> values = gapTable.values();
		Integer[] v = values.toArray(new Integer[values.size()]);
		double totalBits = 0.0;
		double totalGap = totalNumOfGap;
		
		for(int index = 0; index < v.length; index++)
		{
			double appearTimes = v[index];
			double probability = appearTimes/totalGap;
			totalBits += appearTimes * Math.ceil(-Math.log(probability));
		}
		
		return totalBits/8/1024;
	}
	
	//returns value in KB
	public static double GetMemUsageGapEnc(){
		//calculate the memory usage using gap encoding
		Collection<Integer> values = gapTable.values();
		Integer[] v = values.toArray(new Integer[values.size()]);
		double totalBits = 0;
		//count the number of non 1 sized gaps (start counting at 1)
		for(int index = 1; index < v.length; index++)
		{
			double appearTimes = v[index];
			totalBits += 32*appearTimes; //assuming using 32 bit ints
		}
		return totalBits/8/1024; //convert bits to KB
	}
	
	//returns value in KB
	public static double GetMemUsageVBEnc(){
		//calculate the memory usage using variable byte gap encoding
		Collection<Integer> values = gapTable.values();
		Integer[] v = values.toArray(new Integer[values.size()]);
		double totalBits = 0;
		//count the number of non 1 sized gaps (start counting at 1)
		for(int index = 1; index < v.length; index++)
		{
			//get the number of times the gap size appears
			double appearTimes = v[index];
			//get the length (in bits) of the encoded representation
			int bits = (int)Math.round(Math.log(index)/Math.log(2));
			
			//get the number of vb bytes required
			double tempBytesUsed = ((double)bits)/7.0;
			//round up
			int floor = (int)tempBytesUsed;
			double epsilon = 0.0001;
			int bytesUsed = (int)tempBytesUsed;
			if( (tempBytesUsed - (double)floor) > epsilon )
				bytesUsed = floor + 1; 
			
			//add this many bits to the total bits used * the number of times
			//it appears
			totalBits += 8*bytesUsed*appearTimes;
		}
		return totalBits/8/1024; //convert bits to KB
	}
	
	//returns value in KB
	public static double GetMemUsageGammaEnc(){
		//calculate the memory usage using gap encoding
		Collection<Integer> values = gapTable.values();
		Integer[] v = values.toArray(new Integer[values.size()]);
		double totalBits = 0;
		//count the number of non 1 sized gaps (start counting at 1)
		for(int index = 1; index < v.length; index++)
		{
			//get the number of times the gap size appears
			double appearTimes = v[index];
			//get the length (in bits) of the encoded representation
			int bits = (int)Math.round(Math.log(index)/Math.log(2));
			//stick on the [111....111]'s to the front of the value
			int encodedValueLength = 2*bits;
			
			//add this many bits to the total bits used * the number of times
			//it appears
			totalBits += appearTimes*encodedValueLength;
		}
		return totalBits/8/1024; //convert bits to KB
	}
}
