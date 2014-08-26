package algorithm;

import dict.Dictionary;

import java.util.Iterator;
import java.util.Vector;
import java.util.Hashtable;
import java.util.Set;
import java.util.Collection;

public class SpellCorrect {
 
 private String misspellStr = ""; // Misspelled string
 private Vector<String> grams = new Vector<String>(); 
 private Vector<String> candidate = new Vector<String>();
 private Vector<Integer> editDis = new Vector<Integer>();
 
 // 2-gram distance of each candidate from misspelled string.
 private Hashtable<String, Integer> distanceTable = new Hashtable<String, Integer>();
 private Hashtable<String, Integer> editDisTable = new Hashtable<String, Integer>();
 
 // constructor calls all the methods and get collection of candidates
 public SpellCorrect(String str)
 {
	 misspellStr = "$" + str + "$";
	 makeGram();
	 buildTable();
	 findCandidate();
	 EditDistance();
 }
 
 // Get collection of candidates
    public Vector<String> getCandidates()
    {
        return candidate;
    }
    
    // return edit distance of top 20 candidate
    public Hashtable<String, Integer> getEditDistances()
    {
     return editDisTable;
    }
    
    // Get the 2-gram distance of particular candidate
    public Integer get2GramDistance(String candidate)
    {
     return distanceTable.get(candidate);
    }
 
    // Creates the 2-grams of the misspelled string
 private void makeGram()
 {
  for(int index = 0; index < misspellStr.length() - 1; index++)
  {
   String gram = misspellStr.substring(index, index+2);
   grams.add(gram);
  }
 }
 
 // Build the 2-gram distance table
 private void buildTable()
 {
  distanceTable.clear();
  Set<String> kset = Dictionary.typeDict.keySet();
  Iterator<String> types = kset.iterator();
  
  // for each 2-gram of misSpelStr
  for(int index = 0; index < grams.size(); index++, types = kset.iterator())
  {
   // Iterate through all the types in the type dictionary
   while(types.hasNext()){
    String type = types.next();
    String t = "$" + type + "$";
    // for each type that contains gram
    if(t.contains(grams.elementAt(index)))
    {
     // if type is not in the distanceTable, insert into
     // the table with its worst-case 2-gram distance
     if(!distanceTable.containsKey(type))
     {
      int disType = type.length() + 1; 
      int disMisSpel = misspellStr.length() - 1;
      int distance = disType + disMisSpel - 2;
      distanceTable.put(type, distance);
     }else{ // type is found, decrease its worst-case distance by 2
      int distance = distanceTable.get(type) - 2;
      distanceTable.put(type, distance);
     }
    }
   }
  }
  
 }
 
 private void findCandidate()
 {
  Collection<Integer> vDis = distanceTable.values();
  Integer[] values = (Integer[])vDis.toArray(new Integer[vDis.size()]);
  
  int[] minDis = new int[20];
  // find the top 20 minimum distances
  for(int round = 0; round < 20 && round < values.length; round++){
   int min = values[round];
            //if a not yet top 20 distance is shorter than the current
            //swap it with current
   for(int index = round; index < values.length; index++){
    if(values[index] < min){
     min = values[index];
     values[index] = values[round];
     values[round] = min;
    }
   }
            //copy the value into minDis
   minDis[round] = values[round];
  }
  
  // get candidate according to minDis, only 20 candidate
  int numOfCandidate = 0;
  Set<String> kset = distanceTable.keySet();
  for(int index = 0; index < 20; index++)
  {
   Iterator<String> iter = kset.iterator();
   while(iter.hasNext())
   {
    String type = iter.next();
    int dis = distanceTable.get(type);
    if(dis == minDis[index] && !candidate.contains(type)){
     candidate.add(type);
     numOfCandidate++;
     if(numOfCandidate == 20)
      return;
    }
   }
  }
 }
 
 private void EditDistance()
 {
  editDisTable.clear();
  // twenty return candidates
  for(int index = 0; index < candidate.size(); index++)
  {
   // element of candidates
   String c = candidate.elementAt(index);
   String e = misspellStr;
   e = e.substring(1, e.length() -1);	// $tu$ -> tu
   editDisTable.put(c, calcDistance(e, c));
   //System.out.println(calcDistance(e,c));
  }
 }
 
 private int calcDistance(String err, String can)
 {
  String u = "#" + err.trim();
  String v = "#" + can.trim();
  
  int row = u.length();
  int col = v.length();
  
  int[][] matrix = new int[row][col];
  
  for(int j = 1; j < col; j++)
	  matrix[0][j] = j;
  for(int i = 1; i < row; i++)
	  matrix[i][0] = i;
  matrix[0][0] = 0;
  
  for(int i = 1; i < row; i++){
   for(int j = 1; j < col; j++)
   {
    if(u.charAt(i) == v.charAt(j))
    {
     matrix[i][j] = matrix[i-1][j-1];
    }else if(u.charAt(i) != v.charAt(j)){
     int up = matrix[i-1][j];
     int left = matrix[i][j-1];
     int diagnol = matrix[i-1][j-1];
     matrix[i][j] = Math.min(Math.min(up, left), diagnol) + 1;
    }
   }
  }
  
  return matrix[row - 1][col - 1];
 }
}
