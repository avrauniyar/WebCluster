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
import java.io.IOException;
import java.io.Serializable;

public class Pair<A, B> implements Serializable {
	private A a;
	private B b;

	/**
	 * Returns the first element of the pair.
	 */
	public A a() {
		return a;
	}

	/**
	 * Returns the second element of the pair.
	 *
	 * @return the second element of the pair.
	 */
	public B b() {
		return b;
	}


	/**
	 * Creates a new pair with the given elements.
	 */
	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Pair) {
			Pair other = (Pair) obj;
			return a.equals(other.a) && b.equals(other.b);
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 13;
		hash = hash * 31 + a.hashCode();
		hash = hash * 31 + b.hashCode();

		return hash;
	}

	@Override
	public String toString() {
		return "(" + a + ", " + b + ")";
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(a);
		out.writeObject(b);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		a = (A) in.readObject();
		b = (B) in.readObject();
	}
}
