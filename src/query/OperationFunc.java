package query;

import algorithm.Porterstem;

import java.util.ArrayList;
import java.util.Set;
import java.util.Vector;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.StringTokenizer;

import dict.Dictionary;

public class OperationFunc {
	Porterstem pStem = new Porterstem();
	
	// AND operation
	public ArrayList<Integer> AndOp(ArrayList<Integer> a, ArrayList<Integer> b)
	{
		if(a.size() == 0 && b.size() != 0)
			return b;
		else if(a.size() != 0 && b.size() == 0)
			return a;
		else if(a.size() == 0 && b.size() == 0)
			return null;
		
		Set<Integer> Res = new HashSet<Integer>();
		ArrayList<Integer> vRes = new ArrayList<Integer>();
		
		Iterator<Integer> iterA = a.iterator();
		while(iterA.hasNext())
		 	Res.add((Integer)iterA.next());
		
		Iterator<Integer> iterB = b.iterator();
		while(iterB.hasNext()){
			int docID = (Integer)iterB.next();
			if(Res.contains(docID))
				vRes.add(docID);
		}
		 
		return vRes;
	}
	
	// OR operation
	public ArrayList<Integer> OrOp(ArrayList<Integer> a, ArrayList<Integer> b)
	{
		if(a.size() == 0 && b.size() != 0)
			return b;
		else if(a.size() != 0 && b.size() == 0)
			return a;
		else if(a.size() == 0 && b.size() == 0)
			return null;
		
		Set<Integer> Res = new HashSet<Integer>();
		
		Iterator<Integer> iterA = a.iterator();
		while(iterA.hasNext()){
			Res.add((Integer)iterA.next());
		}
		
		Iterator<Integer> iterB = b.iterator();
		while(iterB.hasNext()){
			Res.add((Integer)iterB.next());
		}
		
		ArrayList<Integer> vRes = new ArrayList<Integer>(Res);
		return vRes;
	}

	// NOT operation (L1 and (not L2))
	public ArrayList<Integer> NotOp1(ArrayList<Integer> a, ArrayList<Integer> b)
	{
		if(a.size() == 0)
			return null;
		
		Set<Integer> Res = new HashSet<Integer>();
		
		Iterator<Integer> iterA = a.iterator();
		while(iterA.hasNext()){
			Res.add((Integer)iterA.next());
		}
		
		Iterator<Integer> iterB = b.iterator();
		while(iterB.hasNext()){
			int docID = (Integer)iterB.next();
			if(Res.contains(docID))
				Res.remove(docID);
		}
		
		ArrayList<Integer> vRes = new ArrayList<Integer>(Res);
		return vRes;
		
	}
	
	// NOT operation ((not L1) and L2)
	public ArrayList<Integer> NotOp2(ArrayList<Integer> a, ArrayList<Integer> b)
	{
		if(b.size()== 0)
			return null;
		
		Set<Integer> Res = new HashSet<Integer>();
		
		Iterator<Integer> iterB = b.iterator();
		while(iterB.hasNext()){
			Res.add((Integer)iterB.next());
		}
		
		Iterator<Integer> iterA = b.iterator();
		while(iterA.hasNext()){
			int docID = (Integer)iterA.next();
			if(Res.contains(docID))
				Res.remove(docID);
		}
		
		ArrayList<Integer> vRes = new ArrayList<Integer>(Res);
		return vRes;
	}

	
	// Find document matches exact string
	public ArrayList<Integer> ExactOp(String keyWord)
	{
		Hashtable<Integer, ArrayList<Integer>> result = new Hashtable<Integer, ArrayList<Integer>>();
		Vector<String> tokens = new Vector<String>();
		
		// split whole phrase into several tokens
		StringTokenizer tk = new StringTokenizer(keyWord);
		while(tk.hasMoreTokens()){
			String token = tk.nextToken();
			token = pStem.applyPorterStem(token);
			tokens.add(token);
		}
		
		// if only one phrase contains no token
		if(tokens.size() == 0)
			return null;
		// if only one phrase contains only one token
		if(tokens.size() == 1){
			Set<Integer> set = Dictionary.termDict.get(tokens.elementAt(0)).keySet();
			ArrayList<Integer> list = new ArrayList<Integer>(set);
			return list;
		}
		
		String token1 = tokens.elementAt(0);
		String token2 = tokens.elementAt(1);
		
		Hashtable<Integer, ArrayList<Integer>> table1 = new Hashtable<Integer, ArrayList<Integer>>();
		Hashtable<Integer, ArrayList<Integer>> table2 = new Hashtable<Integer, ArrayList<Integer>>();
		table1 = (Hashtable<Integer, ArrayList<Integer>>)Dictionary.termDict.get(token1);
		table2 = (Hashtable<Integer, ArrayList<Integer>>)Dictionary.termDict.get(token2);
		
		if(table1 == null || table2 == null)
			return null;
		
		// docID
		Set<Integer> set1 = table1.keySet();
		Set<Integer> set2 = table2.keySet();
		
		// whether two tokens occur in the same document
		Iterator<Integer> iter1 = set1.iterator();
		while(iter1.hasNext()){
			int docID = (Integer)iter1.next();
			if(set2.contains(docID)){
				ArrayList<Integer> al1 = table1.get(docID);
				ArrayList<Integer> al2 = table2.get(docID);
				Iterator<Integer> itAl1 = al1.iterator();
				ArrayList<Integer> posID = new ArrayList<Integer>();
				while(itAl1.hasNext()){
					int pos = itAl1.next();
					// whether the token is right after this pos
					if(al2.contains(pos + 1)){
						posID.add(pos + 1);
					}
				}
				if(posID.size() > 0)
					result.put(docID, posID);
			}
		}
		
		// no match
		if(result.size() == 0)
			return null;
		
		// more than two tokens
		for(int index = 2; index < tokens.size(); index++)
		{
			String token = tokens.elementAt(index);
			Hashtable<Integer, ArrayList<Integer>> table = new Hashtable<Integer, ArrayList<Integer>>();
			table = (Hashtable<Integer, ArrayList<Integer>>)Dictionary.termDict.get(token);
			if(table == null)
				return null;
			
			Hashtable<Integer, ArrayList<Integer>> temp = new Hashtable<Integer, ArrayList<Integer>>();
			Set<Integer> kSet = table.keySet();
			Iterator<Integer> iter = kSet.iterator();
			Set<Integer> resSet = result.keySet();
			
			while(iter.hasNext()){
				int docID = (Integer)iter.next();
				if(resSet.contains(docID)){
					ArrayList<Integer> list = table.get(docID);
					ArrayList<Integer> resList = result.get(docID);
					Iterator<Integer> it = list.iterator();
					ArrayList<Integer> match = new ArrayList<Integer>();
					while(it.hasNext()){
						int pos = (Integer)it.next();
						if(resList.contains(pos - 1)){
							match.add(pos);
						}
					}
					if(match.size() > 0)
						temp.put(docID, match);
				}
			}
			
			result = temp;
		}
		
		Set<Integer> vSet = result.keySet();
		ArrayList<Integer> vRes = new ArrayList<Integer>(vSet);
		return vRes;
	}
}