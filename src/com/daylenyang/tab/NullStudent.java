package com.daylenyang.tab;

public class NullStudent extends Student {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8816452103594257405L;
	public static final NullStudent INSTANCE = new NullStudent();

	public static NullStudent getInstance() {
		return INSTANCE;
	}

	private NullStudent() {
		super(null);
	}

	@Override
	public String toString() {
		return "";
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof NullStudent);

	}

}
