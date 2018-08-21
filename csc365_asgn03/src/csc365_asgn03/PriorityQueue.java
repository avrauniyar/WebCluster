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
import java.util.NoSuchElementException;

class PriorityQueue {

	private Node[] array;
	private int count = 0;

	/**
	 * Creates a new PriorityQueue with the given initial capacity.
	 */
	PriorityQueue(int capacity) {

		int c = 1;
		while (c < capacity) {
			c <<= 1;
		}

		array = new Node[c];
	}

	/**
	 * Creates a new PriorityQueue with the default capacity of 16.
	 */
	PriorityQueue() {
		this(16);
	}

	/**
	 * Returns the index of the parent of the Node at the given position.
	 */
	private static int parentOf(int k) {
		return (k - 1) >>> 1;
	}

	/**
	 * Returns the index of the left child of the Node at the given position.
	 */
	private static int leftOf(int k) {
		return (k << 1) + 1;
	}

	/**
	 * Returns the index of the right child of the Node at the given position.
	 */
	private static int rightOf(int k) {
		return (k << 1) + 2;
	}

	/**
	 * Swaps two nodes at the given positions.
	 */
	private void swap(int k, int p) {
		Node t = array[k];
		array[k] = array[p];
		array[p] = t;

		array[p].pqindex = p;
		array[k].pqindex = k;
	}

	/**
	 * Adds a Node to the PriorityQueue. Resizes if necessary.
	 */
	void add(Node e, double w) {
		if (count == array.length) {
			resize();
		}

		int k = count;

		e.cost = w;
		array[count++] = e;

		shiftDown(k);

		e.pqindex = k;
	}

	/**
	 * Finds the first Node, returns it without removing it.
	 */
	Node min() {
		if (count == 0) {
			throw new NoSuchElementException();
		}

		return array[0];
	}

	/**
	 * Finds the Node with the smallest cost, returns it and removes it.
	 */
	Node remove() {
		if (count == 0) {
			throw new NoSuchElementException();
		}

		Node x = array[0];
		array[0] = array[--count];
		int k = 0;

		shiftUp(k);

		return x;
	}

	/**
	 * Helper method. Loop to keep swapping with parent if the cost is smaller.
	 */
	private void shiftDown(int k) {
		while (k != 0) {
			int p = parentOf(k);

			if (array[k].cost < array[p].cost) {
				swap(k, p);
				k = p;
			} else {
				break;
			}
		}
	}

	/**
	 * Helper method. Loop to keep swapping with children if the cost is smaller.
	 */
	private void shiftUp(int k) {
		while (true) {
			int l = leftOf(k);
			int r = l + 1;

			if (l >= count) {
				break;
			}

			if (r >= count) {
				if (array[l].cost < array[k].cost) {
					swap(l, k);
				}

				break;
			}

			int least = (array[r].cost > array[l].cost) ? l : r;
			if (array[least].cost < array[k].cost) {
				swap(least, k);
				k = least;
			} else {
				break;
			}
		}
	}

	/**
	 * Checks if the Priority Queue is empty.
	 */
	public boolean isEmpty() {
		return count == 0;
	}

	/**
	 * Helper method. Resizes the array to double its size.
	 */
	private void resize() {
		Node[] old = array;
		array = new Node[array.length << 1];

		System.arraycopy(old, 0, array, 0, count);
	}

	/**
	 * Reweighs a Node at the given position.
	 */
	void reweight(int k, double w) {
		double old = array[k].cost;
		array[k].cost = w;

		if (w > old) {
			shiftUp(k);//change to shiftup
		} else {
			shiftDown(k);
		}
	}

	/**
	 * Reweighs a given Node.
	 */
	void reweight(Node e, double w) {
		reweight(e.pqindex, w);
	}

}

