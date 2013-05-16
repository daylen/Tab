package com.daylenyang.tab;

public class NullTeam extends Team {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2862393441294637993L;
	public static final NullTeam INSTANCE = new NullTeam();

	public static NullTeam getInstance() {
		return INSTANCE;
	}

	private NullTeam() {
		super(null, null, null);
	}

	@Override
	public School getSchool() {
		return NullSchool.getInstance();
	}

	@Override
	public Student[] getStudents() {
		Student[] temp = { NullStudent.getInstance(), NullStudent.getInstance() };
		return temp;
	}

	@Override
	public String getInitials() {
		return "";
	}

	@Override
	public String toString() {
		return "Nobody";
	}
	
	@Override
	public boolean equals(Object obj) {
		return (obj instanceof NullTeam);

	}


}
