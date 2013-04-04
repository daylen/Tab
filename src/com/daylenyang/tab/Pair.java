package com.daylenyang.tab;

import java.io.Serializable;
import java.util.ArrayList;

public class Pair implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 525726494393123361L;
	private Team affTeam;
	private Team negTeam;
	private int affBallots;
	private int negBallots;
	private ArrayList<Judge> judges;
	private Room room;

	public Pair(Team affTeam, Team negTeam) {
		this.affTeam = affTeam;
		this.negTeam = negTeam;
		judges = new ArrayList<Judge>();
	}

	public void setBallots(int aff, int neg) {
		affBallots = aff;
		negBallots = neg;
	}

	public int getAffBallots() {
		return affBallots;
	}

	public int getNegBallots() {
		return negBallots;
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public Team getAffTeam() {
		return affTeam;
	}

	public Team getNegTeam() {
		return negTeam;
	}

	public ArrayList<Judge> getJudges() {
		return judges;
	}

	public void addJudge(Judge j) {
		this.judges.add(j);
	}

	@Override
	public String toString() {
		return affTeam + " (AFF) vs. " + negTeam + " (NEG)";
	}

}
