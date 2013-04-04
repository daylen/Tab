package com.daylenyang.tab;

import java.io.Serializable;

public class Judge implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9192563356696202006L;
	private School school;
	private String firstName;
	private String lastName;

	public Judge(School school, String singleName) {
		// if the name has a space, split into first and last name
		// if not, assume it's just the last name
		this.school = school;
		if (singleName.indexOf(' ') == -1) {
			// last name
			this.firstName = "";
			this.lastName = singleName;
		} else {
			this.firstName = singleName.substring(0, singleName.indexOf(' '));
			this.lastName = singleName.substring(singleName.indexOf(' ') + 1);
		}
	}

	public Judge(School school, String firstName, String lastName) {
		this.school = school;
		this.firstName = firstName;
		this.lastName = lastName;
	}

	public School getSchool() {
		return school;
	}

	@Override
	public String toString() {
		if (firstName.length() > 0)
			return firstName + " " + lastName;
		else
			return lastName;
	}

}
