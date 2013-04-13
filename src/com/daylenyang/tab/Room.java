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
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Room))
			return false;
		Room r = (Room) obj;
		return roomNumber.equals(r.roomNumber);
	}

}
