package com.daylenyang.tab;

public class NullJudge extends Judge {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1076082962592256702L;
	public static final NullJudge INSTANCE = new NullJudge();

	public static NullJudge getInstance() {
		return INSTANCE;
	}

	private NullJudge() {
		super(null, null);
	}

	@Override
	public School getSchool() {
		return NullSchool.getInstance();
	}

	@Override
	public String toString() {
		return "";
	}

}
