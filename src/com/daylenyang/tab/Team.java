package com.daylenyang.tab;

import java.io.Serializable;

public class Team implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1644934850698463541L;
	private School school;
	private Student[] students;
	private int ranking;

	public Team(School school, Student a, Student b, int ranking) {
		this.school = school;
		this.students = new Student[2];
		students[0] = a;
		students[1] = b;
		this.ranking = ranking;
	}

	public School getSchool() {
		return school;
	}

	public Student[] getStudents() {
		return students;
	}

	public String getInitials() {
		return students[0].getLastInitial() + students[1].getLastInitial();
	}

	@Override
	public String toString() {
		return students[0] + " & " + students[1];
	}

	public int getRanking() {
		return ranking;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Team))
			return false;
		Team t = (Team) obj;
		return (school.equals(t.school) && students[0].equals(t.students[0]) && students[1]
				.equals(t.students[1]));
	}

}
