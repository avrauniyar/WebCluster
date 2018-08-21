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

public class ResizableHashTable<K, V> implements Iterable<K>, Serializable {

	static final float maxLoadFactor = .75f;
	private static final int defaultSize = 16;
	Entry[] table;
	int count = 0;

	/**
	 * Returns an Iterator over all the keys present in the map.
	 */
	@Override
	public Iterator<K> iterator() {
		return new HashTableIterator<K>(table);
	}

	/**
	 * Helper Entry (Node) class. Stores a key and a value.
	 */
	static final class Entry<K, V> {
		final K key;
		V value;
		Entry<K, V> next;

		Entry(K k, V v, Entry<K, V> n) {
			key = k;
			value = v;
			next = n;
		}
	}

	/**
	 * Creates a new HashTable with the default size.
	 */
	public ResizableHashTable() {
		table = new Entry[defaultSize];
	}

	/**
	 * Inserts a key/value pair in the table. If the key is already present, overrides the value.
	 */
	public void insert(K key, V value) {

		if (((float) (count + 1) / (float) table.length) > maxLoadFactor) {
			resize();
		}

		int hash = key.hashCode();
		int i = hash & (table.length - 1); // == % table.length, so long table.length is a power of 2

		for (Entry e = table[i]; e != null; e = e.next) {
			if (key.equals(e.key)) {
				e.value = value;
				return;
			}
		}

		table[i] = new Entry<K, V>(key, value, table[i]);
		++count;
	}

	/**
	 * Returns the value associated with a key, or null if not found.
	 */
	public V get(String key) {
		int hash = key.hashCode();
		int i = hash & (table.length - 1); // == % table.length, so long table.length is a power of 2

		for (Entry<K, V> e = table[i]; e != null; e = e.next) {
			if (key.equals(e.key)) {
				return e.value;
			}
		}

		return null;
	}

	/**
	 * Returns the number of elements in the table.
	 */
	public int size() {
		return count;
	}

	/**
	 * Clears all contents of the HashTable.
	 */
	public void clear() {
		table = new Entry[defaultSize];
		count = 0;
	}

	/**
	 * Doubles the table's size and reinserts all the key/value pairs.
	 */
	void resize() {
		Entry[] oldTable = table;
		table = new Entry[table.length * 2];
		count = 0;

		for (Entry<K, V> oldTableEntry : oldTable) {
			for (Entry<K, V> e = oldTableEntry; e != null; e = e.next) {
				insert(e.key, e.value);
			}
		}
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(table.length);
		out.writeInt(count);
		for (int i = 0; i < table.length; ++i) {
			for (Entry<String, Integer> e = table[i]; e != null; e = e.next) {
				out.writeObject(e.key);
				out.writeObject(e.value);
			}
		}
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		table = new Entry[in.readInt()];
		int c = in.readInt();
		for (int i = 0; i < c; ++i) {
			insert((K) in.readObject(), (V) in.readObject());
		}
	}

	/**
	 * Prints all key/value pairs. Debugging.
	 */
	public void printAll() {
		for (Entry<K, V> tableNode : table) {
			for (Entry<K, V> e = tableNode; e != null; e = e.next) {
				System.out.println(e.key + ": " + e.value);
			}
		}
	}

	/**
	 * Debugging only.
	
	public void printData() {
		System.out.println();
		System.out.println("Number of bins: " + table.length);
		System.out.println("Number of elements: " + count);
		System.out.println("Load factor: " + (count / (float) table.length));
	}
        */
}

