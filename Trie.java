package trie;

import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * 
 * @author Sesh Venugopal
 *
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) {
		TrieNode root = new TrieNode(null, null, null);
		for (int i = 0; i < allWords.length; i++) {
			searchTrie(root, allWords[i], allWords, i, 0, allWords[i].length()-1); //searches and inserts
		}
		return root;
	}
	
	private static Indexes makeCode(String word, String[] allWords, int i) {
		short a = (short)(word.length()-1);
		short b = (short)(allWords[i].length()-1);
		while (a >= 0 && b >= 0 && word.charAt(a) == allWords[i].charAt(b)) {
			a--;
			b--;
		}
		return new Indexes(i, (short)(b+1), (short)(allWords[i].length()-1));
	}
	
	private static void searchTrie(TrieNode root, String word, String [] allWords, int i, int a, int b) {
		if (root.firstChild == null) {
			root.firstChild = new TrieNode(makeCode(word, allWords, i), null, null);
		}
		else if (prefixOf(root.firstChild, word, allWords)) {
			a = 1+root.firstChild.substr.endIndex-root.firstChild.substr.startIndex;
			searchTrie(root.firstChild, word.substring(a), allWords, i, a, b);
		}
		else if (common(root.firstChild, word, allWords) != 0) {
			TrieNode sibling = new TrieNode(makeCode(word.substring(common(root.firstChild, word, allWords)), allWords, i), null, null);
			Indexes sub = new Indexes(root.firstChild.substr.wordIndex, root.firstChild.substr.startIndex, root.firstChild.substr.endIndex);
			TrieNode child = new TrieNode(sub, root.firstChild.firstChild, sibling);
			child.substr.startIndex = (short)(child.substr.startIndex + common(root.firstChild, word, allWords));
			root.firstChild.substr.endIndex = (short)(root.firstChild.substr.startIndex + common(root.firstChild, word, allWords) - 1);
			root.firstChild.firstChild = child;
		}
		else {
			TrieNode present = root.firstChild; //check and see if this solves our error in case of multiple siblings that dont work
			boolean found = false;
			while (!(present.sibling == null)) {
				if (prefixOf(present.sibling, word, allWords)) {
					a = 1+present.sibling.substr.endIndex-present.sibling.substr.startIndex;
					searchTrie(present.sibling, word.substring(a), allWords, i, a, b);
					found = true;
					break;
				}
				else if (common(present.sibling, word, allWords) != 0) {
					TrieNode sibling = new TrieNode(makeCode(word.substring(common(present.sibling, word, allWords)), allWords, i), null, null);
					Indexes sub = new Indexes(present.sibling.substr.wordIndex, present.sibling.substr.startIndex, present.sibling.substr.endIndex);
					TrieNode child = new TrieNode(sub, present.sibling.firstChild, sibling);
					child.substr.startIndex = (short)(child.substr.startIndex + common(present.sibling, word, allWords));
					present.sibling.substr.endIndex = (short)(present.sibling.substr.startIndex + common(present.sibling, word, allWords) - 1);
					present.sibling.firstChild = child; //what if already firstChild??
					found = true;
				}
				present = present.sibling;
			}
			if (!found) {
				present.sibling = new TrieNode(makeCode(word, allWords, i), null, null); //is this present the new present??
			}
		}
	}
	
	private static short common(TrieNode node, String word, String [] allWords) { // ALWAYS CHECK FOR PREFIXES B4 THIS return 0 is nothing shared, index of first char not shared otherwise
		short a = 0;
		while (wordTrie(node, allWords).charAt(a) == word.charAt(a)) {
			a++;
		}
		return a;
	}
	private static boolean prefixOf(TrieNode node, String word, String [] allWords) {
		String a = wordTrie(node, allWords);
		String b = word;
		boolean result = true;
		for (int i = 0; i < a.length(); i++) {
			if (a.charAt(i) != b.charAt(i)) {
				result = false;
				break;
			}
		}
		return result;
	}
	private static int pprefixOf(TrieNode node, String word, String [] allWords) {
		String a = wordTrie(node, allWords);
		String b = word;
		int result = 1;
		for (int i = 0; i < a.length(); i++) {
			if (a.charAt(i) != b.charAt(i)) {
				result = 0;
				break;
			}
			if (i+1 == b.length()) {
				result = 2;
				break;
			}
		}
		return result;
	}
	private static String wordTrie(TrieNode node, String [] allWords) {
		int a = node.substr.wordIndex;
		int b = node.substr.startIndex;
		int c = node.substr.endIndex;
		return allWords[a].substring(b, c+1);
	}
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root, String[] allWords, String prefix) {
		ArrayList<TrieNode> list = new ArrayList<TrieNode>(1);
		TrieNode n = findNode(root, allWords, prefix);
		if (n == null) {
			return null;
		}
		else {
			return printAll(n, list);
		}
	}
	
	private static ArrayList<TrieNode> printAll (TrieNode n, ArrayList<TrieNode> list) {
		if (n.firstChild == null) {
			list.add(n);
			return list;
		}
		else {
			TrieNode present = n.firstChild;
			list = printAll(present, list);
			while (present.sibling != null) {
				list = printAll(present.sibling, list);
				present = present.sibling;
			}
			return list;
		}
	}
	
	private static TrieNode findNode(TrieNode root, String[] allWords, String prefix) {
		if (prefix.length() == 0) {
			return root;
		}
		else if (root.firstChild == null) {
			return null; //is this right?? does this only happen with 1 thing in the trie?
		}
		else if (pprefixOf(root.firstChild, prefix, allWords) == 1) {
			int a = 1+root.firstChild.substr.endIndex-root.firstChild.substr.startIndex;
			return findNode(root.firstChild, allWords, prefix.substring(a));
		}
		else if (pprefixOf(root.firstChild, prefix, allWords) == 2) {
			return root.firstChild;
		}
		else {
			TrieNode present = root.firstChild; //check and see if this solves our error in case of multiple siblings that dont work
			while (!(present.sibling == null)) {
				if (pprefixOf(present.sibling, prefix, allWords) == 1) {
					int a = 1+present.sibling.substr.endIndex-present.sibling.substr.startIndex;
					return findNode(present.sibling, allWords, prefix.substring(a));
				}
				else if (pprefixOf(present.sibling, prefix, allWords) == 2) {
					return present.sibling;
				}
				present = present.sibling;
			}
			return root;
		}
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
