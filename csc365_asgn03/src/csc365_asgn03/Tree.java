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
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Paint;

class Tree {
	private Node root;
	private static final int TEXT_WIDTH = 100;
	private static final int Y_STEP = 50;

	/**
	 * Node class. Stores a Node's value and its children.
	 */
	private static final class Node {
		String value;
		ArrayList<Node> children = new ArrayList<>();

		public Node(String v) {
			value = v;
		}

		@Override
		public boolean equals(Object obj) {
			return (obj instanceof Node) && value.equals(((Node) obj).value);
		}

		@Override
		public int hashCode() {
			return value.hashCode();
		}
	}

	/**
	 * Creates a new Tree with the given root.
	 */
	Tree(String rootID) {
		root = new Node(rootID);
	}

	/**
	 * Adds a children to an already existing Node.
	 * Returns true if a new Node was added, else false.
	 */
	boolean addChildren(String from, String id) {
		Node x = find(root, from);
		if (x == null) {
			return false;
		}

		Node z = new Node(id);
		if (x.children.contains(z)) {
			return false;
		}

		x.children.add(z);
		return true;
	}

	/**
	 * Helper method. Finds a Node in the Tree given its id.
	 */
	private Node find(Node e, String id) {
		if (e.value.equals(id)) {
			return e;
		}

		for (Node x : e.children) {
			Node f = find(x, id);
			if (f != null) {
				return f;
			}
		}

		return null;
	}

	/**
	 * Cleans a URL
	 */
	private static String clean(String s) {
		s = s.replace("https://en.wikipedia.org/wiki/", "");
		s = s.replaceAll("_", " ");

		return s;
	}

	/**
	 * Draws a Tree in the given context, with the given max width.
	 */
	void draw(GraphicsContext ctx, int width) {
		ctx.setStroke(Paint.valueOf("#000000"));
		draw(root, ctx, 0, width, 1);
	}

	/**
	 * Draws subtrees.
	 */
	private void draw(Node e, GraphicsContext ctx, int minX, int maxX, int y) {
		int x = (minX + maxX) / 2;

		if (!e.children.isEmpty()) {
			int ny = y + 1;
			int width = (maxX - minX) / e.children.size();
			int nmx = minX;
			int nMx = nmx + width;
			for (Node c : e.children) {
				draw(c, ctx, nmx, nMx, ny);
				ctx.strokeLine(x + (TEXT_WIDTH / 2), y * Y_STEP + 2, (nmx + nMx) / 2 + (TEXT_WIDTH / 2), ny * Y_STEP + 2);
				nmx += width;
				nMx += width;
			}
		} else {
			int ly = y * Y_STEP + 5;
			ctx.strokeLine(x, ly, x + TEXT_WIDTH, ly);
		}

		ctx.fillText(clean(e.value), x, y * Y_STEP, TEXT_WIDTH);
	}
}
