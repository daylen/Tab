package com.daylenyang.tab;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Tournament implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8747374459113680784L;

	private String name;

	private int numEliminationRounds;
	
	private TournamentState tournamentState;
	
	private Map<String, School> schools = new HashMap<String, School>();

	private PairingRule firstRoundPairingRule; // power protect
	private PairingRule preliminaryRoundPairingRule; // power match
	private PairingRule eliminationRoundPairingRule; // power protect

	private int preliminaryRoundNumJudges; // in this case, 2

	public void setPreliminaryRoundNumJudges(int preliminaryRoundNumJudges) {
		this.preliminaryRoundNumJudges = preliminaryRoundNumJudges;
	}

	private List<Team> teams;
	private List<Team> advancingTeams;

	private List<Judge> judges;
	private List<Room> rooms;
	private List<Round> preliminaryRounds;
	private List<Round> eliminationRounds;

	private Map<Student, List<Double>> speakerPoints;

	public Map<Student, List<Double>> getSpeakerPoints() {
		return speakerPoints;
	}

	public List<Round> getEliminationRounds() {
		return eliminationRounds;
	}

	public Tournament(String name) {
		this.name = name;
		this.teams = new ArrayList<Team>();
		this.preliminaryRounds = new ArrayList<Round>();
		this.eliminationRounds = new ArrayList<Round>();
		this.advancingTeams = new ArrayList<Team>();
		this.speakerPoints = new HashMap<Student, List<Double>>();
	}

	public Tournament(String name, String teamFileName, String judgeFileName,
			String roomFileName) throws FileNotFoundException {
		this(name);
		DataImport importer = new DataImport();

		importer.parseTeams(teamFileName, teams, speakerPoints, schools);
		judges = importer.parseJudges(judgeFileName, schools);
		rooms = importer.parseRooms(roomFileName);
	}

	public boolean hasPreviouslyJudged(Judge j, Pair pairToCheck,
			List<Round> rounds) {
		for (Round r : rounds) {
			for (Pair currPair : r.getPairs()) {
				if (currPair.getAffTeam() == pairToCheck.getAffTeam()
						|| currPair.getAffTeam() == pairToCheck.getNegTeam()
						|| currPair.getNegTeam() == pairToCheck.getAffTeam()
						|| currPair.getNegTeam() == pairToCheck.getNegTeam()) {
					if (currPair.getJudges().contains(j)) {
						// the candidate judge has previously judged some team
						// in the pair
						return true;
					}
				}
			}
		}
		return false;
	}

	public void addTeam(Team t) {
		teams.add(t);
		t.getSchool().addTeam();
	}

	public void addTeams(ArrayList<Team> teamList) {
		for (Team t : teamList) {
			addTeam(t);
		}
	}

	public void enterSpeakerPointsForStudent(String studentName, double points,
			int roundIndex) {
		for (Student s : speakerPoints.keySet()) {
			if (s.toString().equals(studentName)) {
				// This is the student
				enterSpeakerPointsForStudent(s, points, roundIndex);
				return;
			}
		}
		throw new RuntimeException("Speaker point file contains a student not found in teams file");
	}

	public void enterSpeakerPointsForStudent(Student s, double points,
			int roundIndex) {
		if (speakerPoints.get(s).size() > roundIndex) {
			speakerPoints.get(s).set(roundIndex, points);
		} else {
			speakerPoints.get(s).add(roundIndex, points);
		}

	}

	public List<Team> getAdvancingTeams() {
		return advancingTeams;
	}

	/**
	 * Returns the number of times the specified team was on the aff side.
	 * 
	 * Used for balancing the number of times a team was on a side.
	 * 
	 * @param team
	 * @return
	 */
	public int getAffHistoryForTeam(Team team, List<Round> rounds) {
		int numTimesWasAff = 0;

		for (Round r : rounds) {
			for (Pair p : r.getPairs()) {
				if (p.getAffTeam().equals(team))
					numTimesWasAff++;
			}
		}

		return numTimesWasAff;
	}

	/**
	 * Returns the number of ballots a team has.
	 * 
	 * Used for determining future opponents, and for displaying on the
	 * interface.
	 * 
	 * @param team
	 * @return
	 */
	public int getBallotsForTeam(Team team) {
		int numBallots = 0;

		for (Round r : preliminaryRounds) {
			for (Pair p : r.getPairs()) {
				if (p.getAffTeam().equals(team))
					numBallots += p.getAffBallots();
				else if (p.getNegTeam().equals(team))
					numBallots += p.getNegBallots();
			}
		}

		return numBallots;
	}

	public PairingRule getEliminationRoundPairingRule() {
		return eliminationRoundPairingRule;
	}

	public List<Judge> getJudges() {
		return judges;
	}

	public String getName() {
		return name;
	}

	public int getNumEliminationRounds() {
		return numEliminationRounds;
	}

	/*
	 * public int getNumPreliminaryRounds() { return numPreliminaryRounds; }
	 */

	public int getPreliminaryRoundNumJudges() {
		return preliminaryRoundNumJudges;
	}

	public PairingRule getPreliminaryRoundPairingRule() {
		return preliminaryRoundPairingRule;
	}

	/**
	 * Returns a list of the previous opponents for a specified team.
	 * 
	 * Used for determining future opponents.
	 * 
	 * @param team
	 * @return
	 */
	public ArrayList<Team> getPreviousOpponentsForTeam(Team team,
			List<Round> rounds) {
		ArrayList<Team> previousOpponents = new ArrayList<Team>();

		for (Round r : rounds) {
			for (Pair p : r.getPairs()) {
				if (p.getAffTeam().equals(team))
					previousOpponents.add(p.getNegTeam());
				else if (p.getNegTeam().equals(team))
					previousOpponents.add(p.getAffTeam());
			}
		}

		return previousOpponents;
	}

	public List<Room> getRooms() {
		return rooms;
	}

	public List<Round> getPreliminaryRounds() {
		return preliminaryRounds;
	}

	public List<Team> getTeams() {
		return teams;
	}

	public void setEliminationRoundPairingRule(
			PairingRule eliminationRoundPairingRule) {
		this.eliminationRoundPairingRule = eliminationRoundPairingRule;
	}

	public void setNumEliminationRounds(int numEliminationRounds) {
		this.numEliminationRounds = numEliminationRounds;
	}

	/*
	 * public void setNumPreliminaryRounds(int numPreliminaryRounds) {
	 * this.numPreliminaryRounds = numPreliminaryRounds; }
	 */

	public void setPreliminaryRoundPairingRule(
			PairingRule preliminaryRoundPairingRule) {
		this.preliminaryRoundPairingRule = preliminaryRoundPairingRule;
	}

	/**
	 * Makes sure that the tournament properties are valid, so that you can run
	 * a tournament.
	 * 
	 * @return
	 */
	public boolean validateTournament() {

		if (teams.size() < 2)
			return false;

		// find the total number of debates BEFORE adding the null team
		int totalConcurrentDebates = teams.size() / 2;

		if (teams.size() % 2 == 1) {
			// add a null team
			teams.add(NullTeam.getInstance());
		}

		// YOU MUST HAVE MORE THAN YOU NEED
		// if (HAVE > NEED) GOOD
		// if (NEED > HAVE) BAD

		if (preliminaryRoundNumJudges * totalConcurrentDebates > judges.size())
			return false;
		if (totalConcurrentDebates > rooms.size())
			return false;

		return true;
	}

	public boolean validatePrelimRound(int roundIndex) {
		// Check speaker points
		for (Student s : speakerPoints.keySet()) {
			// Check the size
			if (speakerPoints.get(s).size() < roundIndex)
				return false;
		}
		// Check for ballots in all pairs
		for (Pair p : preliminaryRounds.get(roundIndex).getPairs()) {
			// Check if more than zero ballots
			if (p.getAffBallots() + p.getNegBallots() == 0)
				return false;
		}
		return true;
	}

	public PairingRule getFirstRoundPairingRule() {
		return firstRoundPairingRule;
	}

	public void setFirstRoundPairingRule(PairingRule firstRoundPairingRule) {
		this.firstRoundPairingRule = firstRoundPairingRule;
	}

	public TournamentState getTournamentState() {
		return tournamentState;
	}

	public void setTournamentState(TournamentState tournamentState) {
		this.tournamentState = tournamentState;
	}

	public Map<String, School> getSchools() {
		return schools;
	}

	public void setSchools(Map<String, School> schools) {
		this.schools = schools;
	}

}
