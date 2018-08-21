/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc365_asgn03;

/**
 *
 * @author Avrauniyar03
 */
//import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.*;
import java.util.ArrayDeque;
import java.util.Arrays;

public class Graph implements Serializable {
	private ResizableHashTable<String, Node> nodes = new ResizableHashTable<>();

	// set of edges, dump/un-dump when reading/writing/loading
	private HashSet<Triplet<String, String, Double>> edges = new HashSet<>();

	// Helper field
	private long currentMarker = 0;

	/**
	 * Checks if the graph contains the given URL.
	 *
	 */
	public boolean contains(String url) {
		return nodes.get(url) != null;
	}

	/**
	 * Adds a new Node to the Graph.
	 */
	public void addNode(String url) {
		addNode(url, null);
	}

	/**
	 * Adds a new Node to the Graph, along with an associated FrequencyTable.
	 */
	public void addNode(String url, WordFrequency ft) {
		Node e = new Node(url);
		e.ft = ft;
		nodes.insert(url, e);
	}

	/**
	 * Adds a new Edge between two nodes.
	 */
	public void addEdge(String src, String dst) {
		edges.add(new Triplet<>(src, dst, -1d));
	}

	/**
	 * Builds a depth first spanning tree.
	 */
	private void depthFirstSpanningTree(String r, long marker) {
		Node root = nodes.get(r);
		ArrayDeque<Node> stack = new ArrayDeque<>();
		stack.push(root);

		for (String k : nodes) {
			Node x = nodes.get(k);
			x.children.clear();
			x.parent = null;
		}

		while (!stack.isEmpty()) {
			Node v = stack.pop();
			if (v.marker != marker) {
				v.marker = marker;
				for (Node.Edge e : v.edges) {
					v.children.add(e.dest);
					e.dest.parent = v;
					stack.push(e.dest);
				}
			}
		}
	}

	/**
	 * Finds the shortest path between two nodes.
	 */
	public Pair<String[], Double> dijkstra(String src, String dst) {

		if (src.equals(dst)) {
			return new Pair<>(new String[]{src}, 0d);
		}

		PriorityQueue Q = new PriorityQueue(nodes.size());

		for (String k : nodes) {
			Node v = nodes.get(k);

			if (!k.equals(src)) {
				v.parent = null;
				Q.add(v, Double.MAX_VALUE);
			} else {
				Q.add(v, 0);
			}
		}

		while (!Q.isEmpty()) {
			Node u = Q.remove();

			for (Node.Edge e : u.edges) {
				Node v = e.dest;

				double alt = u.cost + e.weight;
				if (alt < v.cost) {
					v.cost = alt;
					v.parent = u;
					Q.reweight(v, alt);
				}
			}
		}

		ArrayDeque<String> result = new ArrayDeque<>();
		double distance = 0;
		Node x = nodes.get(dst);
		while (!x.id.equals(src)) {
			result.addFirst(x.id);
			if (x.parent != null) {
				distance += x.parent.findEdge(x);
			}
			x = x.parent;

			if (x == null) {
				return null;
			}
		}

		result.addFirst(src);

		String[] r = new String[result.size()];
		result.toArray(r);
		return new Pair<>(r, distance);
	}

	/**
	 * Finds the minimum roots needed to cover the whole graph.
	 */
	public String[] connectivityCheck(String[] roots) {
		int n = roots.length;

		String[] min = new String[roots.length + 1];
		for (int i = 0; i < (1 << n); ++i) {

			ArrayList<String> subset = new ArrayList<>();
			for (int j = 0; j < n; j++) {
				if ((i & (1 << j)) > 0) {
					subset.add(roots[j]);
				}
			}
			++currentMarker;
			for (String s : subset) {
				depthFirstSpanningTree(s, currentMarker);
			}

			boolean flag = true;
			for (String k : nodes) {
				Node e = nodes.get(k);
				if (e.marker != currentMarker) {
					flag = false;
					break;
				}
			}

			if (flag && subset.size() < min.length) {
				min = new String[subset.size()];
				subset.toArray(min);
			}
		}

		return min;
	}

	/**
	 * Finds a Node given its id, or return null.
	 */
	public Node get(String id) {
		return nodes.get(id);
	}

	/**
	 * Returns all the values in the Graph.
	 */
	public ArrayList<String> keySet() {
		ArrayList<String> list = new ArrayList<>(nodes.size());
		for (String k : nodes) {
			list.add(k);
		}
		return list;
	}

	/**
	 * Returns the number of nodes in the graph.
	 *
	 */
	public int size() {
		return nodes.size();
	}

	/**
	 * Undumps all the edges_pairs from the HashSet to the nodes,
	 * calculating the weights in the process.
	 */
	public void calculateWeights() {
		for (String k : nodes) {
			nodes.get(k).edges.clear();
		}

		for (Triplet<String, String, Double> edge : edges) {
			Node src = nodes.get(edge.a());
			Node dst = nodes.get(edge.b());

			if (dst == null) {
				continue;
			}


			double w = src.ft.computeDistance(dst.ft);
			src.addEdge(dst, w);
		}

		edges.clear();
	}

	/**
	 * Undumps all the edges from the HashSet to the nodes.
	 */
	private void undump() {
		for (String k : nodes) {
			nodes.get(k).edges.clear();
		}

		for (Triplet<String, String, Double> edge : edges) {
			Node src = nodes.get(edge.a());
			Node dst = nodes.get(edge.b());
			double w = edge.c();

			src.addEdge(dst, w);
		}

		edges.clear();
	}

	/**
	 * Dumps all the edges from the children to the HashSet.
	 */
	private void dump() {
		edges.clear();

		for (String k : nodes) {
			Node v = nodes.get(k);

			for (Node.Edge e : v.edges) {
				edges.add(new Triplet<>(e.src.id, e.dest.id, e.weight));
			}
		}
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		dump();

		out.writeObject(nodes);
		out.writeObject(edges);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		nodes = (ResizableHashTable<String, Node>) in.readObject();
		edges = (HashSet<Triplet<String, String, Double>>) in.readObject();

		undump();
	}

	/**
	 * Writes the graph to the given file.
	 *
	 * @param filename The file to write the graph to.
	 * @throws IOException if the operation failed.
	 */
	public void write(String filename) throws IOException {
		ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(filename));
		o.writeObject(this);
		o.close();
	}

	/**
	 * Reads a Graph from a file.
	 */
	public static Graph read(String filename) throws IOException, ClassNotFoundException {
		ObjectInputStream o = new ObjectInputStream(new FileInputStream(filename));
		Graph g = (Graph) o.readObject();
		o.close();
		return g;
	}

	/**
	 * Prints all nodes and edges. Debugging.
	 */
	public void printAll() {
		for (String k : nodes) {
			System.out.println(k + ":");

			for (Node.Edge e : nodes.get(k).edges) {
				System.out.println("\t" + k + " -- " + e.weight + " --> " + e.dest);
			}
		}
	}

	/**
	 * Prints the edges from the dump. Debugging.
	 */
	public void printEdges() {
		for (Triplet<String, String, Double> e : edges) {
			if (e.c() != -1)
				System.out.println(e.a() + " -- " + e.c() + " --> " + e.b());
		}
	}
}