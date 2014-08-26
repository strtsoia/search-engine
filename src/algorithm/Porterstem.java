package algorithm;

import java.util.regex.*;

public class Porterstem {
	
	private final String vowel = "[aeiou]"; 
	private final String consonant = "[a-z&&[^aeiouy]]";
	private final String vowelY = "[a-z]y";
	
	//Default Constructor
	public Porterstem(){}
	
	public String applyPorterStem(String token)
	{
		String term = removeSuffix(token);
		String temp = removeSuffix(term);
		while(!temp.equals(term)){
			temp = term;
			term = removeSuffix(temp);
		}
		
		return term;
	}
		
	// The stemming algorithm
	private String removeSuffix(String token)
	{
		token = stepOnea(token);	
		token = stepOneb(token);
		token = stepOnec(token);
		token = stepTwo(token);
		token = stepThree(token);
		token = stepFour(token);
		token = stepFive(token);

		return token;
	}
	
	// Convert token into cv form
	private String convertTocv(String token)
	{
		Pattern p = Pattern.compile(vowel);
		Matcher m = p.matcher(token);
		token = m.replaceAll("#");	// # represent 'v'

		p = Pattern.compile(consonant);
		m = p.matcher(token);
		token = m.replaceAll("c");

		p = Pattern.compile(vowelY);
		m = p.matcher(token);
		token = m.replaceAll("c#");
		
		p = Pattern.compile("y");
		m = p.matcher(token);
		token = m.replaceAll("c");
		
		p = Pattern.compile("#");
		m = p.matcher(token);
		token = m.replaceAll("v");
		
		return token;
	}
	
	// Convert token into CV form
	private String convertToCV(String token)
	{
		// first convert to cv form
		token = convertTocv(token);
		
		Pattern p = Pattern.compile("c+");
		Matcher m = p.matcher(token);
		token = m.replaceAll("C");
		
		p = Pattern.compile("v+");
		m = p.matcher(token);
		token = m.replaceAll("V");
		
		return token;
	}

	// Get the measure of the sequence
	private int getMeasure(String token)
	{
		// first convert to CV form
		token = convertToCV(token);
		int measure = 0;
		
		String reg = "VC";
		Pattern p = Pattern.compile(reg);
		Matcher m = p.matcher(token);
		token = m.replaceAll("m");
		
		for(int i = 0; i < token.length(); i++)
			if(String.valueOf(token.charAt(i)).equals("m"))
				measure++;
		
		return measure;
	}
	
	// Common condition *v* - the stem contains a vowel
	private boolean containsVowel(String token)
	{
		token = convertToCV(token);
		
		if (token.contains("V"))
			return true;
		else 
			return false;
	}
		
	// Common condition *d - the stem ends in a double consonant
	private boolean endDoubleCons(String token)
	{
		if(token.length() < 2) 
			return false;
	
		token = convertTocv(token);

		return token.endsWith("cc");
	}
	
	// Common condition *o - the stem ends in cvc,
	// where the second c is neither W, X, nor Y
	private boolean endcvc(String token)
	{
		if(token.length() < 3)
			return false;
		
		String str = convertTocv(token);
		
		if(!str.endsWith("cvc"))
			return false;

		if(token.endsWith("w") || token.endsWith("x") || token.endsWith("y"))
			return false;
		
		return true;

	}
	
	private String stepOnea(String token)
	{		
		if(token.endsWith("sses")){  // step 1a.1
			token = token.replaceAll("es$", "");
		}
		else if(token.endsWith("ies")){ // step 1a.2
			token = token.replaceAll("es$", "");
		}
		else if(token.endsWith("ss")){ // this step was 
			return token; // accidentally omitted in the notes
		}
		else if(token.endsWith("s")){	// step 1a.3
			token = token.replaceAll("s$", "");
		}
		
		return token;
	}

	private String stepOneb(String token)
	{
		String temp;
		
		if(token.endsWith("eed")){	// step 1b.1
			temp = token.replaceAll("eed$", "");
			if(getMeasure(temp) > 0)
				token = temp + "ee";
		}
		else if(token.endsWith("ed")){	// step 1b.2
			temp = token.replaceAll("ed$", "");
			if(containsVowel(temp)){
				token = temp;
				token = stepOnebStar(token);
			}		
		}
		else if(token.endsWith("ing")){	// step 1b.3
			temp = token.replaceAll("ing$", ""); 
			if(containsVowel(temp)){
				token = temp;
				token = stepOnebStar(token);
			}
		}
		
		return token;
	}
	
	private String stepOnebStar(String token)
	{
		if(token.endsWith("at")){	// step 1b*.1
			token = token + "e";
		}
		else if(token.endsWith("bl")){	// step 1b*.2
			token = token + "e";
		}
		else if(token.endsWith("iz")){	// step 1b*.3
			token = token + "e";
		}
		else if(endDoubleCons(token)&&(!(token.endsWith("l") || token.endsWith("s")  
				|| token.endsWith("z")))) {
			if(token.charAt(token.length() - 1) == token.charAt(token.length() - 2)) 
				token = token.replaceAll(".$", ""); 
		}	
		else if(getMeasure(token) == 1 && endcvc(token)){
			token = token + "e";
		}
		
		return token;
	}
	
	private String stepOnec(String token)
	{
		if(token.endsWith("y")){
			String temp = token.replaceAll("y$", "");
			if(containsVowel(temp))
				token = temp + "i";
		}
		
		return token;
	}

	private String stepTwo(String token)
	{
		String temp;
		
		if(token.endsWith("ational")){
			temp = token.replaceAll("ational$", "");
			if(getMeasure(temp) > 0)
				token = temp + "ate";
		}
		else if(token.endsWith("tional")){
			temp = token.replaceAll("tional$", "");
			if(getMeasure(temp) > 0)
				token = temp + "tion";
		}
		else if(token.endsWith("enci")){
			temp = token.replaceAll("enci$", "");
			if(getMeasure(temp) > 0)
				token = temp + "ence";
		}
		else if(token.endsWith("anci")){
			temp = token.replaceAll("anci$", "");
			if(getMeasure(temp) > 0)
				token = temp + "ance";
		}
		else if(token.endsWith("izer")){
			temp = token.replaceAll("izer$", "");
			if(getMeasure(temp) > 0)
				token = temp + "ize";
		}
		else if(token.endsWith("abli")){
			temp = token.replaceAll("abli$", "");
			if(getMeasure(temp) > 0)
				token = temp + "able";
		}
		else if(token.endsWith("alli")){
			temp = token.replaceAll("alli$", "");
			if(getMeasure(temp) > 0)
				token = temp + "al";
		}
		else if(token.endsWith("entli")){
			temp = token.replaceAll("entli$", "");
			if(getMeasure(temp) > 0)
				token = temp + "ent";
		}
		else if(token.endsWith("eli")){
			temp = token.replaceAll("eli$", "");
			if(getMeasure(temp) > 0)
				token = temp + "e";
		}
		else if(token.endsWith("ousli")){
			temp = token.replaceAll("ousli$", "");
			if(getMeasure(temp) > 0)
				token = temp + "ous";
		}
		else if(token.endsWith("ization")){
			temp = token.replaceAll("ization$", "");
			if(getMeasure(temp) > 0)
				token = temp + "ize";
		}
		else if(token.endsWith("ation")){
			temp = token.replaceAll("ation$", "");
			if(getMeasure(temp) > 0)
				token = temp + "ate";
		}
		else if(token.endsWith("ator")){
			temp = token.replaceAll("ator$", "");
			if(getMeasure(temp) > 0)
				token = temp + "ate";
		}
		else if(token.endsWith("alism")){
			temp = token.replaceAll("alism$", "");
			if(getMeasure(temp) > 0)
				token = temp + "al";
		}
		else if(token.endsWith("iveness")){
			temp = token.replaceAll("iveness$", "");
			if(getMeasure(temp) > 0)
				token = temp + "ive";
		}
		else if(token.endsWith("fulness")){
			temp = token.replaceAll("fulness$", "");
			if(getMeasure(temp) > 0)
				token = temp + "ful";
		}
		else if(token.endsWith("ousness")){
			temp = token.replaceAll("ousness$", "");
			if(getMeasure(temp) > 0)
				token = temp + "ous";
		}
		else if(token.endsWith("aliti")){
			temp = token.replaceAll("aliti$", "");
			if(getMeasure(temp) > 0)
				token = temp + "al";
		}
		else if(token.endsWith("aviti")){
			temp = token.replaceAll("aviti$", ""); 
			if(getMeasure(temp) > 0)
				token = temp + "ive";
		}
		else if(token.endsWith("biliti")){
			temp = token.replaceAll("biliti$", "");
			if(getMeasure(temp) > 0)
				token = temp + "ble";
		}
		
		return token;
	}
	
	private String stepThree(String token)
	{
		String temp;
		
		if(token.endsWith("icate")){
			temp = token.replaceAll("icate$", "");
			if(getMeasure(temp) > 0)
				token = temp + "ic";
		}
		else if(token.endsWith("ative")){
			temp = token.replaceAll("ative$", "");
			if(getMeasure(temp) > 0)
				token = temp;
		}
		else if(token.endsWith("alize")){
			temp = token.replaceAll("alize$", "");
			if(getMeasure(temp) > 0)
				token = temp + "al";
		}
		else if(token.endsWith("iciti")){
			temp = token.replaceAll("iciti$", "");
			if(getMeasure(temp) > 0)
				token = temp + "ic";
		}
		else if(token.endsWith("ical")){
			temp = token.replaceAll("ical$", "");
			if(getMeasure(temp) > 0)
				token = temp + "ic";
		}
		else if(token.endsWith("ful")){
			temp = token.replaceAll("ful$", "");
			if(getMeasure(temp) > 0)
				token = temp;
		}
		else if(token.endsWith("ness")){
			temp = token.replaceAll("ness$", "");
			if(getMeasure(temp) > 0)
				token = temp;
		}
		
		return token;
	}
	
	private String stepFour(String token)
	{
		String temp;
		
		if(token.endsWith("al")){
			temp = token.replaceAll("al$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		else if(token.endsWith("ance")){
			temp = token.replaceAll("ance$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		else if(token.endsWith("ence")){
			temp = token.replaceAll("ence$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		else if(token.endsWith("er")){
			temp = token.replaceAll("er$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		else if(token.endsWith("ic")){
			temp = token.replaceAll("ic$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		else if(token.endsWith("able")){
			temp = token.replaceAll("able$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		else if(token.endsWith("ible")){
			temp = token.replaceAll("ible$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		else if(token.endsWith("ant")){
			temp = token.replaceAll("ant$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		else if(token.endsWith("ement")){
			temp = token.replaceAll("ement$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		else if(token.endsWith("ment")){
			temp = token.replaceAll("ment$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		else if(token.endsWith("ent")){
			temp = token.replaceAll("ent$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		else if(token.endsWith("sion")){
			temp = token.replaceAll("ion$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}	
		else if (token.endsWith("tion")){
			temp = token.replaceAll("ion$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		else if(token.endsWith("ou")){
			temp = token.replaceAll("ou$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		else if(token.endsWith("ism")){
			temp = token.replaceAll("ism$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		else if(token.endsWith("ate")){
			temp = token.replaceAll("ate$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		else if(token.endsWith("iti")){
			temp = token.replaceAll("iti$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		else if(token.endsWith("ous")){
			temp = token.replaceAll("ous$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		else if(token.endsWith("ive")){
			temp = token.replaceAll("ive$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		else if(token.endsWith("ize")){
			temp = token.replaceAll("ize$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		
		return token;
	}

	private String stepFive(String token)
	{
		String temp;
		
		if(token.endsWith("e")){
			temp = token.replaceAll("e$", "");
			if(getMeasure(temp) > 1 || (getMeasure(temp) == 1 && !endcvc(temp)))
				token = temp;
		}		
		else if(token.endsWith("ll")){
			temp = token.replaceAll("l$", "");
			if(getMeasure(temp) > 1)
				token = temp;
		}
		
		return token;
	}
}