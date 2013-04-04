package com.daylenyang.tab;

public class NullSchool extends School {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7183893311996574648L;
	public static final NullSchool INSTANCE = new NullSchool();

	public static NullSchool getInstance() {
		return INSTANCE;
	}

	private NullSchool() {
		super(null);
	}

	@Override
	public String toString() {
		return "";
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof NullSchool);

	}

}
