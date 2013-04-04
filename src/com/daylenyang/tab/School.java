package com.daylenyang.tab;

import java.io.Serializable;

public class School implements Comparable<School>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7303169296936661113L;
	private String schoolName;
	private int numTeams;

	public School(String schoolName) {
		this.schoolName = schoolName;
		numTeams = 0;
	}

	@Override
	public String toString() {
		return schoolName;
	}

	public int getNumTeams() {
		return numTeams;
	}

	public void addTeam() {
		numTeams++;
	}

	@Override
	public int compareTo(School s) {
		return this.getNumTeams() - s.getNumTeams();
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof School)
				&& obj.toString().equals(this.schoolName);

	}

}
