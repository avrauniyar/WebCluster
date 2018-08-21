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
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class WordFrequency extends ResizableHashTable<String, Integer> implements Serializable {

	private long timestamp;
	private String url;

	/**
	 * Creates new FrequencyTable with the default size.
	 */
	public WordFrequency(long timestamp, String url) {
		super();
		this.timestamp = timestamp;
		this.url = url;
	}

	/**
	 * Increments the value associated with key by 1. If the key did not exist,
	 */
	public void increment(String key, int value) {

		if ((count + 1) / (float) table.length > maxLoadFactor) {
			resize();
		}

		int hash = key.hashCode();
		int i = hash & (table.length - 1); // == % table.length, so long table.length is a power of 2

		for (Entry<String, Integer> e = table[i]; e != null; e = e.next) {
			if (key.equals(e.key)) {
				e.value += value;
				return;
			}
		}

		table[i] = new Entry<String, Integer>(key, value, table[i]);
		++count;
	}

	/**
	 * Increments the value associated with the key by 1.
	 */
	public void increment(String key) {
		increment(key, 1);
	}

	/**
	 * Computes the similarity between two tables.
	 */
	public double computeSimilarity(WordFrequency other) {

		if (other == null) {
			return 0;
		}

		// See Wikipedia formula, we need three sums.
		double a = 0, b = 0, c = 0;

		for (int i = 0; i < table.length; ++i) {
			for (Entry<String, Integer> e = table[i]; e != null; e = e.next) {
				int f1 = get(e.key);
				Integer f2 = other.get(e.key);

				if (f2 != null) {
					a += f1 * f2;
					b += f1 * f1;
					c += f2 * f2;
				}
			}
		}

		return a / (Math.sqrt(b) * Math.sqrt(c));
	}

	/**
	 * Computes the euclidean distance between two tables.
	 */
	public double computeDistance(WordFrequency other) {
		if (other == null) {
			return 0;
		}

		// See Wikipedia formula, we need three sums.
		double sum = 0;

		for (int i = 0; i < table.length; ++i) {
			for (Entry<String, Integer> e = table[i]; e != null; e = e.next) {
				int f1 = get(e.key);
				Integer f2 = other.get(e.key);

				if (f2 != null) {
					sum += Math.pow(f2 - f1, 2);
				}
			}
		}

		return Math.sqrt(sum);
	}


	public String getURL() {
		return url;
	}

	/**
	 * Checks if the FrequencyTable is up-to-date. If not, reload it.
	 */
	public void checkIfStillFresh() throws IOException {
		if (timestamp < new URL(url).openConnection().getLastModified()) {
			System.out.println("Reloading " + url);

			Document d = Jsoup.connect(url).get();
			WordFrequency ht = createHashTable(d);

			count = ht.count;
			table = ht.table;
			timestamp = ht.timestamp;
			url = ht.url;

			save();
		}
	}

	/**
	 * Saves the FrequencyTable to file.
	 */
	public void save() throws IOException {
		ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream("data/tables/" + url.hashCode() + ".data"));
		o.writeObject(this);
		o.close();
	}

	/**
	 * Helper method, creates a FrequencyTable from a Jsoup document.
	 */
	public static WordFrequency createHashTable(Document d) throws IOException {
		WordFrequency ht = new WordFrequency(new URL(d.baseUri()).openConnection().getLastModified(), d.baseUri());

		Element body = d.body();
		Element head = d.head();

		String bodyText = body.text();
		String pageTitle = head.getElementsByTag("title").last().text().replace(" - Wikipedia", "");

		Pattern p = Pattern.compile("[\\w']+");
		Matcher m = p.matcher(bodyText);

		while (m.find()) {
			String word = bodyText.substring(m.start(), m.end()).toLowerCase();
			if (word.length() >= 4) {
				ht.increment(word);
			}
		}

		m = p.matcher(pageTitle);

		while (m.find()) {
			String word = bodyText.substring(m.start(), m.end()).toLowerCase();
			if (word.length() >= 4) {
				ht.increment(word, 100); // Increment by 10 title words
			}
		}

		Elements metaTags = head.getElementsByAttribute("content");
		for (Element meta : metaTags) {
			m = p.matcher(meta.attr("content"));
			while (m.find()) {
				String word = bodyText.substring(m.start(), m.end()).toLowerCase();
				ht.increment(word, 10);
			}
		}

		return ht;
	}

	/**
	 * Serialize a FrequencyTable
	 */
	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(table.length);
		out.writeObject(url);
		out.writeLong(timestamp);
		out.writeInt(count);
		for (int i = 0; i < table.length; ++i) {
			for (Entry<String, Integer> e = table[i]; e != null; e = e.next) {
				out.writeObject(e.key);
				out.writeInt(e.value);
			}
		}
	}

	/**
	 * De-serialize a FrequencyTable.
	 */
	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
		table = new Entry[in.readInt()];
		url = (String) in.readObject();
		timestamp = in.readLong();
		int c = in.readInt();
		for (int i = 0; i < c; ++i) {
			insert((String) in.readObject(), in.readInt());
		}
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof WordFrequency) && ((WordFrequency) obj).url.equals(url);
	}
}
