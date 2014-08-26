package algorithm;
import java.util.*;

public class Parser {
	
	final String puncReg = "[;]|[?]|,|!|[.]|[']|[)]|[(]|[:]|[-]|[ ]|[\"]"; // regex	
	private static Vector<String> tokens = new Vector<String>(); // list of tokens
	
	// Default constructor
	public Parser() {}

	// Parse the passed document into tokens.
	public void parse(String document) 
	{
		tokens.clear(); // Empty list before parsing
		String[] array = (document.split(puncReg)); // split lines into appropriate tokens
		
		for(int i=0; i < array.length; i++) {
			array[i] = array[i].toLowerCase(); // lowercase each token
			tokens.add(array[i]); // then add token into the list. 
		}
		tokens.remove(""); // remove empty tokens (possibly) generated after tokenization.
	}
	
	// Return the list of tokens for a particular document
	public Vector<String> getTokens()
	{
		return tokens;
	}
}
