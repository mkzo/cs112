package friends;

import java.util.ArrayList;
import java.util.Arrays;

import structures.Queue;
import structures.Stack;

public class Friends {

	/**
	 * Finds the shortest chain of people from p1 to p2.
	 * Chain is returned as a sequence of names starting with p1,
	 * and ending with p2. Each pair (n1,n2) of consecutive names in
	 * the returned chain is an edge in the graph.
	 * 
	 * @param g Graph for which shortest chain is to be found.
	 * @param p1 Person with whom the chain originates
	 * @param p2 Person at whom the chain terminates
	 * @return The shortest chain from p1 to p2. Null or empty array list if there is no
	 *         path from p1 to p2
	 */
	public static ArrayList<String> shortestChain(Graph g, String p1, String p2) {
		p1 = p1.toLowerCase();
		p2 = p2.toLowerCase();
		if (p1.equals(p2)) {
			return null;
		}
		else {
			int [] visited = new int[g.members.length];
			Arrays.fill(visited, 0);
			Queue <Person> q = new Queue <Person> ();
			q.enqueue(g.members[g.map.get(p1)]); //with cause null pointer exception if name not in graph
			Stack<Person> s = new Stack<Person>();
			Person [] p = new Person [g.members.length];
			ArrayList<String> arra = new ArrayList<String>();
			visited[g.map.get(p1)] = 1;
			while (q.size() != 0) {
				Person ptr = q.dequeue();
				System.out.println(ptr.name);
				if (ptr.name.equals(p2)) {
					while(s.isEmpty() || !s.peek().name.equals(p1)) {
						s.push(ptr);
						ptr = p[g.map.get(ptr.name)];
					}
					while (!s.isEmpty()) {
						arra.add(s.pop().name);
					}
					return arra;
				}
				else {
					Friend f = ptr.first;
					if (f != null) {
						if (visited[f.fnum] == 0) {
							q.enqueue(g.members[f.fnum]);
							visited[f.fnum] = 1;
							System.out.println(g.members[f.fnum].name.toUpperCase());
							p[f.fnum] = ptr;
						}
						while (f.next != null) {
							f = f.next;
							if (visited[f.fnum] == 0) {
								q.enqueue(g.members[f.fnum]);
								visited[f.fnum] = 1;
								System.out.println(g.members[f.fnum].name.toUpperCase());
								p[f.fnum] = ptr;
							}
						}
					}
				}
			}
			return null;
		}
	}
	
	
	/**
	 * Finds all cliques of students in a given school.
	 * 
	 * Returns an array list of array lists - each constituent array list contains
	 * the names of all students in a clique.
	 * 
	 * @param g Graph for which cliques are to be found.
	 * @param school Name of school
	 * @return Array list of clique array lists. Null or empty array list if there is no student in the
	 *         given school
	 */
	public static ArrayList<ArrayList<String>> cliques(Graph g, String school) {
		ArrayList<ArrayList<String>> cliquelist = new ArrayList<ArrayList<String>>();
		int [] visited = new int[g.members.length];
		Arrays.fill(visited, 0);
		for (int i = 0; i < g.members.length; i++) {
			if (g.members[i].school == null || !g.members[i].school.equals(school)) {
				visited[i] = 1;
			}
			else if (visited[i] == 0) {
				//BFS and add all rutgers friends, make sure no non rutgers connectors
				Queue <Person> q = new Queue <Person> ();
				ArrayList <String> a = new ArrayList <String> ();
				q.enqueue(g.members[i]);
				visited[i] = 1;
				a.add(g.members[i].name);
				while (q.size() != 0) {
					Person ptr = q.dequeue();
					System.out.println(ptr.name.toLowerCase());
					Friend f = ptr.first;
					if (f != null) { //wait what happens if f is null?!!?
						if (g.members[f.fnum].school != null) {
							if (visited[f.fnum] == 0 && school.equals(g.members[f.fnum].school)) {
								System.out.println(g.members[f.fnum].name.toUpperCase()); //
								q.enqueue(g.members[f.fnum]);
								visited[f.fnum] = 1;
								a.add(g.members[f.fnum].name);
							}
						}
						while (f.next != null) {
							f = f.next;
							if (g.members[f.fnum].school != null) {
								if (visited[f.fnum] == 0 && g.members[f.fnum].school.equals(school)) {
									System.out.println(g.members[f.fnum].name.toUpperCase()); //
									q.enqueue(g.members[f.fnum]);
									visited[f.fnum] = 1;
									a.add(g.members[f.fnum].name);
								}
							}
						}
					}
				}
				cliquelist.add(a);
			}
		}
		return cliquelist;
		
	}
	
	/**
	 * Finds and returns all connectors in the graph.
	 * 
	 * @param g Graph for which connectors needs to be found.
	 * @return Names of all connectors. Null or empty array list if there are no connectors.
	 */
	public static ArrayList<String> connectors(Graph g) {
		//DFS
		ArrayList<String> answer = new ArrayList<String>();
		int[] dfsnum = new int[g.members.length];
		int[] back = new int[g.members.length];
		int[] visited = new int[g.members.length];
		int[] origin = new int[g.members.length];
		int[] connector = new int[g.members.length];
		int[] count = new int[] {1};
		Arrays.fill(dfsnum, 0);
		Arrays.fill(back, 0);
		Arrays.fill(visited, 0);
		for (int i=0; i < origin.length; i++) {
			origin[i] = -1;
		}
		Arrays.fill(connector, 0);
		for (int i = 0; i < g.members.length; i++) {
			if (visited[i] == 0) {
				System.out.println("New: "+g.members[i].name);
				dfs(i, visited, origin, g, count, dfsnum, back, connector);
			}
		}
		for (int i = 0; i < g.members.length; i++) {
			if (connector[i] == 1) {
				answer.add(g.members[i].name);
			}
		}
		for (int i = 0; i < g.members.length; i++) {
			System.out.println(i + " " + dfsnum[i]);
		}
		return answer;	
	}
	

	private static void dfs(int v, int [] visited, int [] origin, Graph g, int [] count, int [] dfsnum, int [] back, int [] connector) {		
		visited[v] = 1;
		dfsnum[v] = count[0];
		back[v] = count[0];
		count[0]++;
		int newContacts = 0;
		System.out.println(g.members[v].name.toUpperCase());
		for (Friend f = g.members[v].first; f != null; f = f.next) {
			if (visited[f.fnum] == 0) {
				origin[f.fnum] = v;
				newContacts++;
				dfs(f.fnum, visited, origin, g, count, dfsnum, back, connector);
				back[v] = Math.min(back[v], back[f.fnum]);
				if ((origin[v] != -1) && (back[f.fnum] >= dfsnum[v])) {
					connector[v] = 1;
				}
				System.out.println(g.members[f.fnum].name.toLowerCase());
			}
			else if (origin[v] != f.fnum) { 
				back[v] = Math.min(back[v], dfsnum[f.fnum]);
			}
		}
		if (origin[v] == -1 && newContacts >= 2) {
			connector[v] = 1;
		}
	}

}

