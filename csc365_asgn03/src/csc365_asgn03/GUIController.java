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

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.controlsfx.control.textfield.TextFields;


import java.io.*;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

public class GUIController implements Initializable {

	private static final int PAGES_TO_PRELOAD = 300;
	private static final int CLUSTER_REPETITIONS = 10;

	// Constants
	private static final String[] ROOTS = {"https://en.wikipedia.org/wiki/Bitcoin", "https://en.wikipedia.org/wiki/Astronomy",
                                                "https://en.wikipedia.org/wiki/Health", "https://en.wikipedia.org/wiki/Wikipedia", 
                                                "https://en.wikipedia.org/wiki/Electronics"
	};

	// FXML variables
	@FXML
	public TextField pageSelect;
	@FXML
	public GridPane mainPanel;
	@FXML
	public Label centers;
	@FXML
	public Label result;
	@FXML
	public Canvas canvas;
	@FXML
	public Label spanningTreesLabel;

	// Other variables
	private Graph g;
	private ArrayList<String> suggestions;
	private Cluster<String>[] clusters;

	/**
	 * Reads the Graph and Clusters from file if they exist.
	 */
	public GUIController() {
		// Read stored graph
		try {
			g = Graph.read("data/graph.data");
			getSuggestions();

			// Read stored clusters
			try {
				ObjectInputStream o = new ObjectInputStream(new FileInputStream("data/clusters.data"));
				clusters = (Cluster<String>[]) o.readObject();
				o.close();
			} catch (IOException | ClassNotFoundException ignored) {

			}
		} catch (IOException | ClassNotFoundException e) {
			g = new Graph();
			suggestions = new ArrayList<>();
		}
	}

	/**
	 * Displays the cluster centers on the GUI.
	 */
	private void displayCenters() {
		if (clusters == null) {
			return;
		}

		StringBuilder sb = new StringBuilder("Centers: ");
		for (Cluster<String> c : clusters) {
			sb.append(clean(c.getCenter())).append(", ");
		}

		sb.delete(sb.length() - 2, sb.length());

		centers.setText(sb.toString());
	}

	/**
	 * Returns a list of the possible inputs. (cleaning them in the process)
	 */
	private void getSuggestions() {
		suggestions = g.keySet();
		for (int i = 0; i < suggestions.size(); ++i) {
			suggestions.set(i, clean(suggestions.get(i)));
		}
	}

	private static String clean(String s) {
		s = s.replace("https://en.wikipedia.org/wiki/", "");
		s = s.replaceAll("_", " ");

		return s;
	}


	private static String unclean(String s) {
		s = s.replaceAll(" ", "_");
		s = "https://en.wikipedia.org/wiki/" + s;

		return s;
	}

	/**
	 * Binds auto-completion, and display centers if they exist.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// All @FXML variables have been initialized

		// Bind TextField autocompletion to the graph's keys
		TextFields.bindAutoCompletion(
				pageSelect,
				t -> suggestions.filter(
						k -> k.toLowerCase().contains(t.getUserText().toLowerCase())
				)
		);

		if (g.size() != 0) {
			String[] cc = g.connectivityCheck(ROOTS);
			spanningTreesLabel.setText("Minimum roots to cover the entire graph: " +
					Arrays.stream(cc).reduce("", (a, b) -> clean(a) + "\n\t" + clean(b)));
		}

		displayCenters();
	}

	/**
	 * Called when the "Go!" button is clicked.
	 * Finds the path from each cluster center to the input, builds the output tree and displays it.
	 */
	public void findClosestCenters(ActionEvent actionEvent) {
		// Sets the input back to proper form.
		String src = unclean(pageSelect.getText());

		if (!g.contains(src) || clusters == null) {
			pageSelect.getStyleClass().add("error");
		} else {
			pageSelect.getStyleClass().remove("error");

			Tree t = new Tree(src);

			for (int i = 0; i < clusters.length; ++i) {
				Cluster<String> c = clusters[i];
				Pair<String[], Double> p = g.dijkstra(src, c.getCenter());
				if (p == null) {
					System.out.println("No path between " + clean(src) + " and " + clean(c.getCenter()));
					continue;
				}

				String[] path = p.a();

				for (int j = 1; j < path.length; ++j) {
					t.addChildren(path[j - 1], path[j]);
				}
			}

			System.out.println();

			canvas.getGraphicsContext2D().clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
			t.draw(canvas.getGraphicsContext2D(), (int) canvas.getWidth());
		}
	}

	/**
	 * Called when the "Preload" button is clicked.
	 * Preload the pages from the roots.
	 */
	public void preload(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
		centers.setText("");
		spanningTreesLabel.setText("");
		mainPanel.setDisable(true);

		WebCrawler wc = new WebCrawler();
		for (String r : ROOTS) {
			wc.crawl(r, PAGES_TO_PRELOAD / ROOTS.length);
		}

		g = wc.getGraph();
		g.calculateWeights();
		g.write("data/graph.data");

		getSuggestions();

		mainPanel.setDisable(false);
	}

	/**
	 * Called when the "Cluster" button is clicked.
	 * Performs K-Medioids clustering on the data.
	 */
	public void cluster(ActionEvent actionEvent) throws IOException, ClassNotFoundException {
		mainPanel.setDisable(true);

		clusters = KMedioids.cluster(g, ROOTS, CLUSTER_REPETITIONS);

		displayCenters();

		ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream("data/clusters.data"));
		o.writeObject(clusters);
		o.close();

		mainPanel.setDisable(false);
	}
}

