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
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator class that allows to iterator over all the keys in a HashTable.
 *
 * @param <T> The type of the keys in the HashTable.
 */
class HashTableIterator<T> implements Iterator<T> {

	private ResizableHashTable.Entry<T, Object> next;
	private int i = 0;
	private ResizableHashTable.Entry[] table;

	/**
	 * Initializes the iterator.
	 *
	 * @param table The entry table to iterate over.
	 */
	public HashTableIterator(ResizableHashTable.Entry[] table) {
		this.table = table;
		findNext();
	}

	/**
	 * Finds the next element, or sets it to null if there is none.
	 */
	private void findNext() {
		next = (next == null) ? null : next.next;

		for (; next == null && i < table.length; ++i) {
			next = table[i];
		}
	}

	/**
	 * Returns true if there is more elements, else false.
	 *
	 * @return true if there is more elements, else false.
	 */
	@Override
	public boolean hasNext() {
		return next != null;
	}

	/**
	 * Returns the next key to iterate over.
	 *
	 * @return the next key.
	 */
	@Override
	public T next() {
		if (next == null) {
			throw new NoSuchElementException();
		}

		ResizableHashTable.Entry<T, Object> ret = next;
		findNext();
		return ret.key;
	}
}
