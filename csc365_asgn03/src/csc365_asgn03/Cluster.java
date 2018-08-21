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

import java.io.Serializable;

public class Cluster<T> implements Serializable {
	private T center;
	private ArrayList<T> data = new ArrayList<>();

	/*
	 * Creates a new Cluster, with the given point as center
	 */
	public Cluster(T center) {
		this.center = center;
	}

	/*
	 * Changes this cluster's center.
	 */
	public void setCenter(T center) {
		this.center = center;
	}

	/*
	 * Returns this cluster's center.
	 */
	public T getCenter() {
		return center;
	}

	/*
	 * Adds a new item as a data point.
	 */
	public void add(T item) {
		data.add(item);
	}

	/**
	 * Removes an item from the Cluster.
	 */
	public void remove(T item) {
		data.remove(item);
	}

	/**
	 * Returns all the data points in the cluster (except the center).
	 */
	public ArrayList<T> getData() {
		return data;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(center + ":\n");
		for (T d : data) {
			sb.append("\t").append(d).append("\n");
		}
		return sb.toString();
	}
}
