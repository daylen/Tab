package com.daylenyang.tab;

import java.io.Serializable;

public class Room implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3686827967671958262L;
	private String roomNumber;

	public Room(String roomNumber) {
		this.roomNumber = roomNumber;
	}

	@Override
	public String toString() {
		return roomNumber;
	}

}
