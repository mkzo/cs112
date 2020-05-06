package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
	throws FileNotFoundException {
		if (docFile == null) {
			throw new FileNotFoundException();
		}
		Scanner sc = new Scanner(new File(docFile));
//		sc.useDelimiter(" |\\r|\\n");
		HashMap<String, Occurrence> kws = new HashMap<String, Occurrence>(1000,2.0f);
		while (sc.hasNext()) {
			String s = getKeyword(sc.next());
			if (s == null) {
				//nothing
			}
			else if (!kws.containsKey(s)) {
				kws.put(s, new Occurrence(docFile, 1));
			}
			else if (kws.containsKey(s)) {
				kws.get(s).frequency++;
			}
		}
		sc.close();
		return kws;
	}
	
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		for (Map.Entry<String,Occurrence> entry : kws.entrySet()) {
			String key = entry.getKey();
			Occurrence value = entry.getValue();
			if (!keywordsIndex.containsKey(key)) { //word is not in hashmap
				ArrayList<Occurrence> a = new ArrayList<Occurrence>();
				a.add(value);
				keywordsIndex.put(key, a);
			}
			else {  //file is NOT in arraylist but word is
				ArrayList<Occurrence> a = new ArrayList<Occurrence>();
				a = keywordsIndex.get(entry.getKey());
				a.add(value);
				insertLastOccurrence(keywordsIndex.get(entry.getKey()));
				keywordsIndex.put(key, a);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	
	public String getKeyword(String word) {
		word = word.toLowerCase();
		boolean flawless = true;
		for (int i = 0; i < word.length(); i++) {  //careful that word.length should change with the changing length of word
			if (!flawless && "abcdefghijklmnopqrstuvwxyz".contains(word.substring(i,i+1))) {
				return null;
			}
			else if (!("abcdefghijklmnopqrstuvwxyz".contains(word.substring(i,i+1)))) {
				if (".,?:;!".contains(word.substring(i,i+1))) {
					word = word.substring(0, i) + word.substring(i+1);
					flawless = false;
					i--;
				}
				else {
					return null;
				}
			}
		}
		if (noiseWords.contains(word) || word.length() == 0 || word == " ") {
			return null;
		}
		else {
			return word;
		}
	}
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		int high, low;
		high = occs.size() - 2; //last number doesnt count
		low = 0;
		ArrayList<Integer> list = new ArrayList<Integer>();
		return recursiveInsert(occs, high, low, list);
	}
	
	
	private ArrayList<Integer> recursiveInsert(ArrayList <Occurrence> occs, int high, int low, ArrayList<Integer> list) {
		if (occs.size() == 0 || occs.size() == 1) {
			return null;
		}
		if (high < low) {
			Occurrence o = occs.get(occs.size()-1);
			occs.add(low, o);
			occs.remove(occs.size()-1);
			return list;
		}
		int mid = (high+low) / 2;
		list.add(mid);
		if (occs.get(mid).frequency < occs.get(occs.size()-1).frequency) {
			return recursiveInsert(occs, mid-1, low, list);
		}
		else if (occs.get(mid).frequency > occs.get(occs.size()-1).frequency) {
			return recursiveInsert(occs, high, mid+1, list);
		}
		else if (occs.get(mid).frequency == occs.get(occs.size()-1).frequency) {
			Occurrence o = occs.get(occs.size()-1);
			occs.add(mid, o);
			occs.remove(occs.size()-1);
			return list;
		}
		else {
			return null;
		}
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	private ArrayList<String> add(ArrayList <String> c, ArrayList<Occurrence> a, int i) {
		if (c.contains(a.get(i).document)) {
		}
		else {
			c.add(a.get(i).document);
		}
		return c;
	}
	public ArrayList<String> top5search(String kw1, String kw2) {
		ArrayList <Occurrence> a = null;
		ArrayList <Occurrence> b = null;
		ArrayList <String> c = new ArrayList <String>();
		if (keywordsIndex.containsKey(kw1)) {
			a = keywordsIndex.get(kw1);
			System.out.println(kw1);
			System.out.println(a);
		}
		if (keywordsIndex.containsKey(kw2)) {
			b = keywordsIndex.get(kw2);
			System.out.println(kw2);
			System.out.println(b);
		}
		int i = 0;
		int j = 0;
		while (c.size() < 5) {
			if (a == null && b == null) {
				c = null;
				break;
			}
			else if (a == null) {
				if (b.size() <= j) {
					break;
				}
				c = add(c,b,j);
				j++;
			}
			else if (b == null) {
				if (a.size() <= i) {
					break;
				}
				c = add(c,a,i);
				i++;
			}
			else if (a.size() <= i) {
				if (b.size() <= j) {
					break;
				}
				c = add(c,b,j);
				j++;
			}
			else if (b.size() <= j) {
				if (a.size() <= i) {
					break;
				}
				c = add(c,a,i);
				i++;
			}
			else if (a.get(i).frequency > b.get(j).frequency) {
				c = add(c,a,i);
				i++;
			}
			else if (a.get(i).frequency < b.get(j).frequency) {
				c = add(c,b,j);
				j++;
			}
			else if (a.get(i).frequency == b.get(j).frequency) {
				if (c.size() < 4) {
					c = add(c,a,i);
					i++;
					c = add(c,b,j);
					j++;
				}
				else {
					c = add(c,a,i);
					i++;
				}
			}
				
		}
		return c;
	}
}
