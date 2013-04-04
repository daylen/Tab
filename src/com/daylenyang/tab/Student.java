package com.daylenyang.tab;

import java.io.Serializable;

public class Student implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -935167523803975005L;
	private String firstName;
	private String lastName;

	public Student(String singleName) {
		// if the name has a space, split into first and last name
		// if not, assume it's just the last name

		if (singleName.indexOf(' ') == -1) {
			// last name
			this.firstName = "";
			this.lastName = singleName;
		} else {
			this.firstName = singleName.substring(0, singleName.indexOf(' '));
			this.lastName = singleName.substring(singleName.indexOf(' ') + 1);
		}

	}

	public Student(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	@Override
	public String toString() {
		if (firstName.length() > 0)
			return firstName + " " + lastName;
		else
			return lastName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getInitials() {
		if (firstName.length() > 0)
			return firstName.charAt(0) + "" + lastName.charAt(0);
		else
			return getLastInitial();
	}

	public String getLastInitial() {
		return lastName.charAt(0) + "";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Student) {
			Student s = (Student) obj;
			return (firstName.equals(s.firstName) && (lastName
					.equals(s.lastName)));
		} else {
			return false;
		}
	}

}
