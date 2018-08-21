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
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;

public class LZWCompression {

	/**
	 * Helper class. Basically a byte[] with `hashCode` and `equals` methods.
	 */
	private static final class BArray {
		byte[] a;

		BArray(byte[] b) {
			this.a = b;
		}

		BArray(byte b) {
			this(new byte[] { b });
		}

		BArray() {
			this(new byte[0]);
		}

		/**
		 * Adds the given bytes to the BArray
		 * @param bs the bytes to be added.
		 * @return the newly created BArray
		 */
		BArray addBytes(byte[] bs) {
			BArray n = new BArray(new byte[a.length + bs.length]);

			int i = 0;
			for (; i < a.length; ++i) {
				n.a[i] = a[i];
			}

			for (; i < n.a.length; ++i) {
				n.a[i] = bs[i - a.length];
			}

			return n;
		}

		BArray addBytes(BArray o) {
			return addBytes(o.a);
		}

		BArray addByte(byte b) {
			BArray n = new BArray(new byte[a.length + 1]);

			for (int i = 0; i < a.length; ++i) {
				n.a[i] = a[i];
			}

			n.a[a.length] = b;

			return n;
		}

		@Override
		public int hashCode() {
			int hash = 13;
			for (byte b : a) {
				hash = (hash * 31) << b;
			}

			return hash;
		}

		@Override
		public boolean equals(Object obj) {
			return obj instanceof BArray && Arrays.equals(a, ((BArray) obj).a);
		}
	}

	/**
	 * Compresses a byte array using the LZW algorithm.
	 */
	public static byte[] compress(byte[] input) {
		// create dictionary
		HashMap<BArray, Integer> dictionary = new HashMap<>();
		for (byte i = -128; i < 127; ++i) {
			dictionary.put(new BArray(i), 128 + i);
		}

		BArray w = new BArray();
		ArrayList<Integer> output = new ArrayList<>();
		for (byte b : input) {
			BArray wc = w.addByte(b);
			if (dictionary.containsKey(wc)) {
				w = wc;
			} else {
				output.add(dictionary.get(w));
				dictionary.put(wc, dictionary.size());
				w = new BArray(b);
			}
		}

		if (w.a.length != 0) {
			output.add(dictionary.get(w));
		}

		return toByteArray(output);
	}

	public static byte[] decompress(byte[] raw) {
		// TO BE DONE
		return new byte[0];
	}

	/**
	 * Transforms and ArrayList of Integer into a byte[]
	 */
	private static byte[] toByteArray(ArrayList<Integer> list) {
		ByteBuffer buffer = ByteBuffer.allocate(list.size() * 4);
		for (Integer a : list) {
			buffer.putInt(a);
		}

		buffer.flip();
		return buffer.array();
	}

	private static ArrayDeque<Integer> fromByteArray(byte[] raw) {
		ByteBuffer buffer = ByteBuffer.allocate(raw.length);
		buffer.put(raw);
		buffer.flip();

		ArrayDeque<Integer> result = new ArrayDeque<>();
		for (int i = 0; i < (raw.length / 4); ++i) {
			result.add(buffer.getInt());
		}

		return result;
	}

}

