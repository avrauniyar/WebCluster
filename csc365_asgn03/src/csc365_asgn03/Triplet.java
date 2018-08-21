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

public class Triplet<A, B, C> implements Serializable {
	private A a;
	private B b;
	private C c;

	/**
	 * Returns the first element of the triplet.
	 */
	public A a() {
		return a;
	}

	/**
	 * Returns the second element of the triplet.
	 */
	public B b() {
		return b;
	}

	/**
	 * Returns the third element of the triplet.
	 */
	public C c() {
		return c;
	}


	/**
	 * Creates a new triplet with the given elements.
	 */
	public Triplet(A a, B b, C c) {
		this.a = a;
		this.b = b;
		this.c = c;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Triplet) {
			Triplet other = (Triplet) obj;
			return a.equals(other.a) && b.equals(other.b) && c.equals(other.c);
		}

		return false;
	}

	@Override
	public int hashCode() {
		int hash = 13;
		hash = hash * 31 + a.hashCode();
		hash = hash * 31 + b.hashCode();
		hash = hash * 31 + c.hashCode();

		return hash;
	}

	@Override
	public String toString() {
		return "(" + a + ", " + b + ", " + c + ")";
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(a);
		out.writeObject(b);
		out.writeObject(c);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		a = (A) in.readObject();
		b = (B) in.readObject();
		c = (C) in.readObject();
	}
}

