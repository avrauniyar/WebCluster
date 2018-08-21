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
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class ArrayList<T> extends java.util.ArrayList<T> {

	/**
	 * Returns a copy of the original List, where the element for which the predicate is false have been removed.
	 */
	public ArrayList<T> filter(Predicate<T> fn) {
		ArrayList<T> copy = new ArrayList<>();
		forEach(t -> {
			if (fn.test(t)) {
				copy.add(t);
			}
		});

		return copy;
	}

	public ArrayList() {
		super();
	}

        public ArrayList(int size) {
		super(size);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("(");
		forEach(k -> {
			sb.append(k).append(", ");
		});

		if (sb.length() < 2) {
			return "()";
		}

		sb.delete(sb.length() - 2, sb.length()).append(")");
		return sb.toString();
	}
}
