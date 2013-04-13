package com.daylenyang.tab;

import java.io.Serializable;

public class Judge implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9192563356696202006L;
	private School school;
	private String name;

	public Judge(School school, String name) {
		this.school = school;
		this.name = name;
	}

	public School getSchool() {
		return school;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Judge))
			return false;
		Judge j = (Judge) obj;
		return name.equals(j.name);
	}

}
