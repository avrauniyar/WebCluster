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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;

public class WebCrawler {

	private Graph G;
	private ArrayDeque<String> stack = new ArrayDeque<>();
	private int n;

	private static final int MAX_STACK_SIZE = 1000;

	/**
	 * Creates a new WebCrawler.
	 */
	public WebCrawler() {
		G = new Graph();
	}

	/**
	 * @return the data graph
	 */
	public Graph getGraph() {
		return G;
	}

	/**
	 * Crawls starting at url, with a maximum of n pages.
	 *
	 */
	public void crawl(String url, int count) throws IOException, ClassNotFoundException {
		n = count;
		stack.add(url);

		crawl();

		stack.clear();
	}

	/**
	 * Private Helper method
	 */
	private void crawl() throws IOException, ClassNotFoundException {
		if (stack.size() == 0 || n < 0) {
			return;
		}

		String url = stack.remove();

		if (!checkURL(url) || G.contains(url)) {
			crawl();
		}

		--n;

                Document d;
		WordFrequency ht;
		try {
			d = Jsoup.connect(url).get();
			ht = WordFrequency.createHashTable(d);
		} catch (Exception e) {
			System.out.println("Unable to add " + url + ": " + e.getMessage());
			++n;
			return;
		}

		ht.save();

		G.addNode(d.baseUri(), ht);

		Elements aTags = d.getElementById("content").select("a[href]");
		for (Element aTag : aTags) {
			String href = aTag.attr("href");
			if (href != null) {
				String nURL = "https://en.wikipedia.org" + href;
				if (checkURL(nURL)) {
					if (stack.size() < MAX_STACK_SIZE) {
						stack.add(nURL);
					}
					G.addEdge(url, nURL);
				}
			}
		}
		crawl();
	}

	/**
	 * Checks if we URL meets criteria.
	 */
	private static boolean checkURL(String url) {
      
                return !(!url.startsWith("https://en.wikipedia.org/wiki/") || url.startsWith("https://en.wikipedia.org/wiki/Portal:") || 
                        url.endsWith("(disambiguation)") || url.startsWith("https://en.wikipedia.org/wiki/Wikipedia_talk:") || 
                        url.startsWith("https://en.wikipedia.org/wiki/Help:") || url.startsWith("https://en.wikipedia.org/wiki/Category:") ||
    			url.startsWith("https://en.wikipedia.org/wiki/Main_Page") || url.startsWith("https://en.wikipedia.org/wiki/Template:") ||
			url.startsWith("https://en.wikipedia.org/wiki/Wikipedia:") || url.startsWith("https://en.wikipedia.org/wiki/Special:") ||
			url.startsWith("https://en.wikipedia.org/wiki/File:") || url.startsWith("https://en.wikipedia.org/wiki/Talk:") ||
			url.startsWith("https://en.wikipedia.org/wiki/Portal:") || url.endsWith(".jpg") || url.contains("wikimedia") 
                        );
	}
}
