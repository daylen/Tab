package com.daylenyang.tab;

import java.io.Serializable;
import java.util.ArrayList;

public class Round implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2404373186243987262L;
	private ArrayList<Pair> pairs;

	public Round() {
		pairs = new ArrayList<Pair>();
	}

	public void addPair(Pair pair) {
		pairs.add(pair);
	}

	public ArrayList<Pair> getPairs() {
		return pairs;
	}

}
