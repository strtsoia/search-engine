package query;

import algorithm.Porterstem;
import algorithm.Wildcard;

import java.util.StringTokenizer;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.Set;

import dict.Dictionary;

public class QueryEx {
    
    final String puncReg = "[;]|[?]|,|!|[.]|[']|[)]|[(]|[:]";    // regex
    private Vector<Integer> matches = new Vector<Integer>(); // docIDs that matches query
    Porterstem pstem = new Porterstem();
    OperationFunc oper = new OperationFunc();
    
    public QueryEx(String str, boolean reqAllToks)
    {
    	// Check if the query contains a wildcard token
        if(!str.contains("*")){
        	makeQuery(str);
        }
        else {
        	wildCardQuery(str);
        }       
    }
    
    // Returns the docIDs that matches the query
    public Vector<Integer> getResult()
    {
    	return matches;
    }
    
    private void makeQuery(String query)
    {
        ArrayList<Integer> list = new ArrayList<Integer>();
        
        if(query.matches("[\\s]+"))
            return;
        
        // split query string by or
        StringTokenizer tk = new StringTokenizer(query, "+");
        while(tk.hasMoreTokens()){
            String group = tk.nextToken();
            group = group.toLowerCase();
            group = removePunctuation(group);
            group = group.trim();
            System.out.print(group + " ");
                
            if(list.size() == 0){
                list = takeQuery(group);
            }else{
                ArrayList<Integer> tempList = takeQuery(group);
                list = oper.OrOp(list, tempList); 
            }
        }
        
        // show the result
        if(list != null){
            Collections.sort(list);
            Iterator<Integer> iter = list.iterator();
            while(iter.hasNext()){
                int docID = (Integer)iter.next();
                System.out.print(docID + ", ");
                matches.add(docID);
            }
            System.out.println();
        }
         
    }
    
    private void wildCardQuery(String query)
    {    
        // other tokens other than wildone
        Vector<String> vt = new Vector<String>();
        StringTokenizer tk = new StringTokenizer(query, " ");
        Vector<String> wildCandidate = new Vector<String>();

        while(tk.hasMoreTokens()){
            String token = tk.nextToken();
            if(token.contains("*")){
                Wildcard w = new Wildcard(token);
                Vector<String> vec = w.GetMatches();
                for(int i = 0; i < vec.size(); i++){
                    token = pstem.applyPorterStem(vec.elementAt(i));
                    wildCandidate.add(token);
                }
            }else{
                token = pstem.applyPorterStem(token);
                vt.add(token);
            }
        }
        
        ArrayList<Integer> res = new ArrayList<Integer>();
        for(int i = 0; i < wildCandidate.size(); i++)
        	System.out.println(wildCandidate.elementAt(i));
        // or Operation on all candidates
        // if only one candidate
        if(wildCandidate.size() == 1){
            Hashtable<Integer, ArrayList<Integer>> table
                = Dictionary.termDict.get(wildCandidate.elementAt(0));
            Set<Integer> kSet = table.keySet();
            res = new ArrayList<Integer>(kSet);
        }else if(wildCandidate.size() > 1) // two candidates
        {
            Hashtable<Integer, ArrayList<Integer>> table1
                = Dictionary.termDict.get(wildCandidate.elementAt(0));
            Hashtable<Integer, ArrayList<Integer>> table2
                = Dictionary.termDict.get(wildCandidate.elementAt(1));
            Set<Integer> kSet1 = table1.keySet();
            Set<Integer> kSet2 = table2.keySet();
            ArrayList<Integer> list1 = new ArrayList<Integer>(kSet1);
            ArrayList<Integer> list2 = new ArrayList<Integer>(kSet2);
            
            res = oper.OrOp(list1, list2);
            
            
            for(int index = 2; index < wildCandidate.size(); index++){
                Hashtable<Integer, ArrayList<Integer>> table
                    = Dictionary.termDict.get(wildCandidate.elementAt(index));
                Set<Integer> kSet = table.keySet();
                ArrayList<Integer> tempList = new ArrayList<Integer>(kSet);
                res = oper.OrOp(res, tempList);
            }
        }
        
        String q = "";
        for(int index = 0; index < vt.size(); index++)
        	q += vt.elementAt(index);
        ArrayList<Integer> l = takeQuery(q);
        res = oper.AndOp(l, res);
        Collections.sort(res);
        matches = new Vector<Integer>(res);   
    }
    
    private ArrayList<Integer> takeQuery(String kword)
    {
        ArrayList<Integer> list = new ArrayList<Integer>();
        Vector<String> negExactStr = new Vector<String>();    // "Barack Obama" 
        Vector<String> exactStr = new Vector<String>();        // -"Barack Obama"
        ArrayList<String> pTokens = new ArrayList<String>();
        ArrayList<String> nTokens = new ArrayList<String>();
        
        
        // get all the neg exact phrase
        Pattern p = Pattern.compile("-\"(.*?)\"");
        Matcher m = p.matcher(kword);
        while(m.find()){
            negExactStr.add(m.group().substring(1));
        }
        kword = m.replaceAll("");
        kword = kword.trim();
        
        // get all the exact phrase
        p = Pattern.compile("\"(.*?)\"");
        m = p.matcher(kword);
        while(m.find())
            exactStr.add(m.group());
        kword = m.replaceAll("");
        kword = kword.trim();
        
        // get positive and negative tokens
        StringTokenizer tk = new StringTokenizer(kword);
        while(tk.hasMoreTokens()){
            String token = tk.nextToken();
            if(token.startsWith("-")){
                token = token.substring(1);
                nTokens.add(token);
            }else{
                pTokens.add(token);
            }
        }
        
        // if has positive exact pharse
        for(int index = 0; index < exactStr.size(); index++){
            String pharse = exactStr.get(index);
            pharse = pharse.substring(1, pharse.length()-1);
            list = oper.ExactOp(pharse);
        }
        
        // look for positive token
        Iterator<String> pIt = pTokens.iterator();
        while(pIt.hasNext()){
            String token = pIt.next().toString();
            token = pstem.applyPorterStem(token);
            Hashtable<Integer, ArrayList<Integer>> table = Dictionary.termDict.get(token);
            
            if(table != null){
                Set<Integer> set = table.keySet();
                if(list.size() == 0){
                    list = new ArrayList<Integer>(set);
                }else
                {
                    ArrayList<Integer> tempList = new ArrayList<Integer>(set);
                    list = oper.AndOp(list, tempList);
                }                    
            }
        }
        
        // no match found
        if(list  == null)
            return null;
        
        // do the negative part, negative pharse
        for(int index = 0; index < negExactStr.size(); index++){
            String pharse = negExactStr.elementAt(index);
            pharse = pharse.substring(1, pharse.length()-1);
            ArrayList<Integer> tempList = oper.ExactOp(pharse);
            if(tempList != null)
                list = oper.NotOp1(list, tempList);
        }
        
        // do the negative tokens
        for(int index = 0; index < nTokens.size(); index++){
            String token = nTokens.get(index);
            token = pstem.applyPorterStem(token);
            Hashtable<Integer, ArrayList<Integer>> table = Dictionary.termDict.get(token);
            if(table != null){
                Set<Integer> set = table.keySet();
                ArrayList<Integer> tempList = new ArrayList<Integer>(set);
                list = oper.NotOp1(list, tempList);
            }
        }
        
        return list;
    }
        
    // Remove punctuations from the query
    private String removePunctuation(String token)
    {
        Pattern p = Pattern.compile(puncReg);
        Matcher m = p.matcher(token);
        return m.replaceAll("");
        
    }
}
