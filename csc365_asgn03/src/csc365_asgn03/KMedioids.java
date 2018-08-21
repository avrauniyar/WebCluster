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
import java.util.HashMap;

/**
 * Class specific to the project.
 */
public class KMedioids {

	/**
	 * Helper class for caching. FrequencyTables are interchangeable (distance(A, B) == distance(B, A)).
	 */
	private static final class FTPair extends Pair<WordFrequency, WordFrequency> {
		FTPair(WordFrequency ft1, WordFrequency ft2) {
			super(ft1, ft2);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof FTPair) {
				FTPair other = (FTPair) obj;

				return (a().equals(other.a()) && b().equals(other.b())) ||
						(a().equals(other.b()) && b().equals(other.a()));
			}

			return false;
		}

		@Override
		public int hashCode() {
			int a = a().hashCode();
			int b = b().hashCode();

			int hash = 13;
			hash = hash * 31 + Math.min(a, b);
			hash = hash * 31 + Math.max(a, b);

			return hash;
		}
	}

	/**
	 * Used for caching distances.
	 */
	private static HashMap<FTPair, Double> distances = new HashMap<>();

	/**
	 * Performs K-Medioids clustering on the data.
         */
	public static Cluster<String>[] cluster(Graph g, String[] centers, int repetitions) throws IOException, ClassNotFoundException {
		// long startTime = System.nanoTime();

		Cluster<String>[] clusters = init(centers, g);
		double configuration = Double.MAX_VALUE;

		while (--repetitions > -1) {
			for (Cluster<String> c : clusters) {
				for (Cluster<String> d : clusters) {
					ArrayList<String> data = d.getData();
					for (int i = 0; i < data.size(); ++i) {
						String m = c.getCenter();
						String o = data.get(i);
						c.setCenter(o);
						data.set(i, m);

						double alt = computeCost(clusters, g);
						if (alt < configuration) {
							configuration = alt;
						} else {
							c.setCenter(m);
							data.set(i, o);
						}
					}
				}
			}
		}

		distances.clear();

		return clusters;
	}

	/**
	 * Computes the cost of a given configuration.
	 */
	private static double computeCost(Cluster<String>[] clusters, Graph g) throws IOException, ClassNotFoundException {
		double sum = 0;
		for (Cluster<String> c : clusters) {
			String center = c.getCenter();
			WordFrequency ft1 = g.get(center).getFT();

			for (String d : c.getData()) {
				WordFrequency ft2 = g.get(d).getFT();

				FTPair p = new FTPair(ft1, ft2);
				if (distances.containsKey(p)) {
					sum += distances.get(p);
				} else {
					double distance = ft1.computeDistance(ft2);
					distances.put(p, distance);
					sum += distance;
				}
			}
		}

		return sum;
	}

	/**
	 * Creates an array of Clusters from the initial centers.
	 */
	private static Cluster<String>[] init(String[] centers, Graph g) throws IOException, ClassNotFoundException {
		Cluster<String>[] clusters = new Cluster[centers.length];
		ArrayList<String> data = g.keySet();

		for (int i = 0; i < centers.length; ++i) {
			clusters[i] = new Cluster<>(centers[i]);
			data.remove(centers[i]);
		}

		// Assign each point to the closest cluster
		for (String point : data) {
			WordFrequency ft1 = g.get(point).getFT();
			double min = Double.MAX_VALUE;
			int minindex = 0;

			for (int i = 0; i < centers.length; ++i) {

				WordFrequency ft2 = g.get(centers[i]).getFT();
				double alt = ft1.computeDistance(ft2);

				distances.put(new FTPair(ft1, ft2), alt);

				if (alt < min) {
					min = alt;
					minindex = i;
				}
			}

			clusters[minindex].add(point);
		}

		return clusters;
	}
}
