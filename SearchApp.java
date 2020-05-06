package lse;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.ArrayList;
import java.util.Scanner;
public class SearchApp {
	public static void main (String [] args) throws FileNotFoundException {
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter keyword 1");
		String kw1 = sc.nextLine();
		System.out.println("Enter keyword 2");
		String kw2 = sc.nextLine();
		System.out.println("Now enter file name for the list of files to be analyzed");
		String docFile = sc.nextLine();
		System.out.println("Now enter file name for the list of noise words");
		String noisewordsFile = sc.nextLine();
		LittleSearchEngine lse = new LittleSearchEngine();
		lse.makeIndex(docFile, noisewordsFile);
		ArrayList <String> result = lse.top5search(kw1, kw2);
		if (result == null) {
			System.out.println("No results found.");
		}
		else {
			for (int i = 0; i < result.size(); i++) {
				System.out.println(result.get(i));
			}
		}
		sc.close();
	}
}
