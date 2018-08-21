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
import java.io.*;
import java.util.NoSuchElementException;

public class Node implements Serializable {
	String id;
	HashSet<Edge> edges = new HashSet<>();


	double cost;
	int pqindex;
	Node parent;
	HashSet<Node> children = new HashSet<>();
	WordFrequency ft;
	long marker;

	Node(String id) {
		this.id = id;
	}


	static final class Edge {
		double weight;
		Node src, dest;

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof Edge) {
				Edge other = (Edge) obj;
				return src.equals(other.src) && dest.equals(other.dest);
			}

			return false;
		}

		@Override
		public int hashCode() {
			int hash = 13;
			hash = hash * 31 + src.hashCode();
			hash = hash * 31 + dest.hashCode();

			return hash;
		}

		@Override
		public String toString() {
			return "[" + src + " -- " + weight + " --> " + dest + "]";
		}
	}

	/**
	 * Returns the FrequencyTable associated with the Node.
	 */
	public WordFrequency getFT() throws IOException, ClassNotFoundException {
		if (ft == null) {
			ObjectInputStream o = new ObjectInputStream(new FileInputStream("data/tables/" + id.hashCode() + ".data"));
			ft = (WordFrequency) o.readObject();
			o.close();
		}

		return ft;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof Node && id.equals(((Node) obj).id);
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	void addEdge(Node dest, double w) {
		Edge e = new Edge();
		e.weight = w;
		e.src = this;
		e.dest = dest;
		edges.add(e);
	}

	double findEdge(Node dst) {
		for (Edge e : edges) {
			if (e.dest.equals(dst)) {
				return e.weight;
			}
		}

		throw new NoSuchElementException();
	}

	@Override
	public String toString() {
		return id;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(id);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		edges = new HashSet<>();
		children = new HashSet<>();
		id = (String) in.readObject();
	}
}
