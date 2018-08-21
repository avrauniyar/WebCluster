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
import java.util.Iterator;

public class HashSet<T> implements Iterable<T>, Serializable {

	private ResizableHashTable<T, Object> map = new ResizableHashTable<>();

	/**
	 * Adds a new key to the HashSet.
	 *
	 * @param k the key to be added.
	 */
	public void add(T k) {
		map.insert(k, null);
	}

	/**
	 * Returns the number of elements in the set.
	 *
	 * @return an Integer.
	 */
	public int size() {
		return map.size();
	}

	/**
	 * Clears all the keys in the HashSet.
	 */
	public void clear() {
		map.clear();
	}


	/**
	 * Iterate over all the keys in the set.
	 *
	 * @return An Iterator over the keys in the set.
	 */
	@Override
	public Iterator<T> iterator() {
		return map.iterator();
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeObject(map);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		map = (ResizableHashTable<T, Object>) in.readObject();
	}

	@Override
	public String toString() {
		if (map.size() == 0) {
			return "()";
		}

		StringBuilder sb = new StringBuilder("(");
		for (T k : map) {
			sb.append(k).append(", ");
		}

		sb.delete(sb.length() - 2, sb.length());
		sb.append(")");

		return sb.toString();
	}
}
