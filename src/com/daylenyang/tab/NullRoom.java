package com.daylenyang.tab;

public class NullRoom extends Room {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2008004283974631773L;
	public static final NullRoom INSTANCE = new NullRoom();

	public static NullRoom getInstance() {
		return INSTANCE;
	}

	private NullRoom() {
		super(null);
	}

	@Override
	public String toString() {
		return "";
	}

}
