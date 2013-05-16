package com.daylenyang.tab;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

public abstract class RoundGen implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3932831828293618278L;

	protected static class JudgePlusWeight implements
			Comparable<JudgePlusWeight> {
		public Judge judge;
		public int weight;

		public JudgePlusWeight(Judge judge, int weight) {
			this.judge = judge;
			this.weight = weight;
		}

		@Override
		public int compareTo(JudgePlusWeight arg0) {
			return this.weight - arg0.weight;
		}
	}

	protected static class TeamPlusWeight implements Comparable<TeamPlusWeight> {
		public Team team;
		public int weight;

		public TeamPlusWeight(Team team, int weight) {
			this.team = team;
			this.weight = weight;
		}

		@Override
		public int compareTo(TeamPlusWeight arg0) {
			return this.weight - arg0.weight;
		}

		public String toString() {
			return team + " (" + weight + ")";
		}
	}

	protected static final int ITERATION_COUNT = 1000;
	protected Tournament tournament;
	protected List<Round> rounds;

	protected PairingRule currentPairingRule;
	protected int currentNumJudges;

	protected int teamsHavePreviouslyDebatedPenalty;
	protected int teamsAreFromSameSchoolPenalty;

	protected int judgeHasPreviouslyJudgedPenalty;

	protected int judgeIsFromSameSchoolPenalty;

	protected Random random = new Random();

	public RoundGen(Tournament tournament, List<Round> rounds) {
		this.tournament = tournament;
		this.currentNumJudges = tournament.getPreliminaryRoundNumJudges();
		this.rounds = rounds;

		// pairing rule penalty is 10x
		teamsAreFromSameSchoolPenalty = 100 * tournament.getTeams().size();
		teamsHavePreviouslyDebatedPenalty = 1000 * tournament.getTeams().size();

		// randomness is 1x
		judgeHasPreviouslyJudgedPenalty = 100 * tournament.getTeams().size();
		judgeIsFromSameSchoolPenalty = 1000 * tournament.getTeams().size();
	}

	/**
	 * Given a round filled with pairs, this method assigns judges and rooms to
	 * the pairs.
	 * 
	 * @param r
	 *            A round.
	 */
	/*
	 * protected void assignJudgesAndRooms(Round r) { ArrayList<Judge>
	 * pickedJudges = new ArrayList<Judge>(); ArrayList<Judge> notPickedJudges =
	 * new ArrayList<Judge>( tournament.getJudges()); Set<Judge> set1 = new
	 * HashSet<Judge>(tournament.getJudges()); Set<Judge> set2 = new
	 * HashSet<Judge>(); Set<Judge> picked = new HashSet<Judge>(); Set<Judge>
	 * nPicked = new HashSet<Judge>();
	 * 
	 * ArrayList<Room> notPickedRooms = new ArrayList<Room>(
	 * tournament.getRooms());
	 * 
	 * for (Pair pair : r.getPairs()) { set2.clear();
	 * 
	 * if (pair.getAffTeam() instanceof NullTeam || pair.getNegTeam() instanceof
	 * NullTeam) { pair.addJudge(NullJudge.getInstance());
	 * pair.setRoom(NullRoom.getInstance()); continue; }
	 * 
	 * List<Judge> judgeWeights = sortJudgesByQualityIndex(pair,
	 * notPickedJudges);
	 * 
	 * int notPickedJudgesNum = notPickedJudges.size(); int pickedJudgesNum =
	 * pickedJudges.size(); // Add judges to the pair for (int i = 0; i <
	 * currentNumJudges; i++) { Judge judgeToUse = judgeWeights.get(i); if
	 * (notPickedJudges.remove(judgeToUse)) {
	 * 
	 * } else { System.out.println("BIG PROBLEM: JUDGE NOT REMOVED"); }
	 * pair.addJudge(judgeToUse); if (pickedJudges.contains(judgeToUse))
	 * System.out.println("BIG PROBLEM: " + judgeToUse);
	 * pickedJudges.add(judgeToUse); } if (pickedJudges.size() - pickedJudgesNum
	 * != 2) System.out.println("yet another problem"); if (notPickedJudgesNum -
	 * notPickedJudges.size() != 2)
	 * System.out.println("this probably will never show up");
	 * 
	 * // Add room to the pair pair.setRoom(notPickedRooms.get(0));
	 * notPickedRooms.remove(0);
	 * 
	 * picked.clear(); nPicked.clear();
	 * 
	 * picked.addAll(pickedJudges); nPicked.addAll(notPickedJudges);
	 * 
	 * picked.retainAll(nPicked); if (picked.size() != 0)
	 * System.out.println("HUGEST PROB OF ALL");
	 * 
	 * // testing set2.addAll(pickedJudges); set2.addAll(notPickedJudges);
	 * 
	 * if (!set1.equals(set2)) System.out.println("ugh"); }
	 * 
	 * }
	 */

	/**
	 * Given a pair and a list of judges, returns the list of judges sorted by
	 * quality index.
	 * 
	 * @param pair
	 *            A pair.
	 * @param notPickedJudges
	 *            A list of judges.
	 * @return List of judges sorted by quality index.
	 */
	protected List<Judge> sortJudgesByQualityIndex(Pair pair,
			Set<Judge> notPickedJudges) {
		ArrayList<JudgePlusWeight> candidateWeights = new ArrayList<JudgePlusWeight>();

		for (Judge j : notPickedJudges) {

			int weight = computeQualityIndexForJudge(j, pair, true);
			candidateWeights.add(new JudgePlusWeight(j, weight));

		}

		Collections.sort(candidateWeights);
		ArrayList<Judge> candidates = new ArrayList<Judge>();
		for (JudgePlusWeight tpw : candidateWeights) {
			candidates.add(tpw.judge);
		}
		return candidates;
	}

	/**
	 * Given a team and a list of teams, returns the list of teams sorted by
	 * quality index.
	 * 
	 * @param myTeam
	 *            A team.
	 * @param candidateTeams
	 *            A list of teams.
	 * @return List of teams sorted by quality index.
	 */
	protected ArrayList<Team> sortTeamsByQualityIndex(Team myTeam,
			List<Team> candidateTeams) {
		ArrayList<TeamPlusWeight> candidateWeights = new ArrayList<TeamPlusWeight>();

		for (Team candidateTeam : candidateTeams) {
			int weight = computeQualityIndexForPair(myTeam, candidateTeam);

			candidateWeights.add(new TeamPlusWeight(candidateTeam, weight));

		}

		Collections.sort(candidateWeights);
		ArrayList<Team> candidates = new ArrayList<Team>();
		for (TeamPlusWeight tpw : candidateWeights) {
			candidates.add(tpw.team);
		}
		return candidates;
	}

	/**
	 * Given a judge and a pair, compute the quality index for the judge.
	 * 
	 * @param j
	 *            Judge
	 * @param p
	 *            Pair
	 * @param useRandom
	 *            Whether to introduce some randomness.
	 * @return The quality index number.
	 */
	protected int computeQualityIndexForJudge(Judge j, Pair p, boolean useRandom) {

		int weight = (useRandom ? random.nextInt(tournament.getTeams().size())
				: 0);

		// check if previously judged
		if (tournament.hasPreviouslyJudged(j, p, rounds))
			weight += judgeHasPreviouslyJudgedPenalty;
		// check if from same school
		if (j.getSchool().equals(p.getAffTeam().getSchool())
				|| j.getSchool().equals(p.getNegTeam().getSchool()))
			weight += judgeIsFromSameSchoolPenalty;

		return weight;
	}

	/**
	 * Given a team and another team, compute the quality index of the team.
	 * 
	 * @param myTeam
	 *            Team
	 * @param candidateTeam
	 *            Team
	 * @param useRandom
	 *            Whether to introduce some randomness.
	 * @return The quality index number.
	 */
	protected int computeQualityIndexForPair(Team myTeam, Team candidateTeam) {
		List<Team> previousOpponents = tournament.getPreviousOpponentsForTeam(
				myTeam, rounds);

		int weight = 0;

		// Check whether this is a previous opponent
		if (previousOpponents.contains(candidateTeam))
			weight += teamsHavePreviouslyDebatedPenalty;
		// Check whether this is a same school team
		if (tournament.getPreliminaryRounds().size() != 0
				&& candidateTeam.getSchool().equals(myTeam.getSchool()))
			weight += teamsAreFromSameSchoolPenalty;

		// Apply pairing rule

		int myBallots = tournament.getBallotsForTeam(myTeam);
		int theirBallots = tournament.getBallotsForTeam(candidateTeam);

		int pairingRulePenalty = Math.abs(myBallots - theirBallots);

		switch (currentPairingRule) {
		case POWER_MATCH:
			weight += pairingRulePenalty;
			break;
		case POWER_PROTECT:
			weight -= pairingRulePenalty;
			break;
		default:
		}

		return weight;
	}

	/**
	 * Computes the quality index for an entire round.
	 * 
	 * @param r
	 *            The round.
	 * @return The quality index number.
	 */
	protected int computeQualityIndexForRound(Round r) {
		int qualityIndex = 0;

		for (Pair p : r.getPairs()) {
			qualityIndex += computeQualityIndexForPair(p.getAffTeam(),
					p.getNegTeam());
			for (Judge j : p.getJudges()) {
				qualityIndex += computeQualityIndexForJudge(j, p, false);
			}
		}

		return qualityIndex;
	}

	/**
	 * Repeatedly generates a round and calculates its quality index to find the
	 * best round.
	 * 
	 * @param teams
	 *            In preliminary rounds this is all the teams, but in
	 *            elimination rounds it is just the teams that advance.
	 */
	public void generateManyRoundsAndPickBestOne(List<Team> teams,
			List<Pair> manualPairs) {

		// Lower quality index is better.
		int bestQualityIndex = Integer.MAX_VALUE;
		Round bestRound = null;

		for (int i = 0; i < ITERATION_COUNT; i++) {
			// Create a round
			Round r = new Round();

			// Add the manual pairs to the round
			for (Pair p : manualPairs) {
				p.clearJudges();
				r.addPair(p);
			}

			// Create a list of teams to process
			Set<Team> teamsToProcess = new HashSet<Team>(teams);

			// Remove the manual pairs from the teams to process
			for (Pair p : manualPairs) {
				teamsToProcess.remove(p.getAffTeam());
				teamsToProcess.remove(p.getNegTeam());
			}
			// Make that a list
			List<Team> teamsToProcessList = new ArrayList<Team>(teamsToProcess);
			Collections.shuffle(teamsToProcessList);

			// Add the other pairs to the round
			pairTeam(teamsToProcessList, r);
			// Assign judges and rooms
			assignJudgesAndRooms(r);

			// Quality check
			int currQualityIndex = computeQualityIndexForRound(r);

			// Is the round better?
			if (currQualityIndex < bestQualityIndex) {
				bestRound = r;
				bestQualityIndex = currQualityIndex;
			}

		}
		// Finally, add the round
		rounds.add(bestRound);

		for (Pair p : bestRound.getPairs()) {
			System.out.println(p.getJudges());
		}

	}

	private void assignJudgesAndRooms(Round r) {

		Set<Judge> notPickedJudges = new HashSet<Judge>(tournament.getJudges());
		List<Room> notPickedRooms = new ArrayList<Room>(tournament.getRooms());

		for (Pair pair : r.getPairs()) {
			if (pair.getAffTeam() instanceof NullTeam
					|| pair.getNegTeam() instanceof NullTeam) {
				pair.addJudge(NullJudge.getInstance());
				pair.setRoom(NullRoom.getInstance());
				continue;
			}

			// Assign judges
			List<Judge> bestJudgesForThisPair = sortJudgesByQualityIndex(pair,
					notPickedJudges);

			for (int i = 0; i < currentNumJudges; i++) {
				pair.addJudge(bestJudgesForThisPair.get(i));
				notPickedJudges.remove(bestJudgesForThisPair.get(i));
			}

			// Assign rooms
			pair.setRoom(notPickedRooms.get(0));
			notPickedRooms.remove(0);

		}

	}

	/**
	 * Pairs some teams. For each team, it selects another team to pair with
	 * based on quality index and assigns sides.
	 * 
	 * @param teams
	 * @param r
	 */
	protected void pairTeam(List<Team> teams, Round r) {
		while (!teams.isEmpty()) {

			Iterator<Team> it = teams.iterator();
			Team me = it.next();
			it.remove();

			List<Team> candidateTeams = new ArrayList<Team>(teams);
			ArrayList<Team> candidateWeights = sortTeamsByQualityIndex(me,
					candidateTeams);

			Team opponent = candidateWeights.get(0);
			teams.remove(opponent);

			int numTimesWasAff = tournament.getAffHistoryForTeam(me, rounds);
			int numTimesTheyWereAff = tournament.getAffHistoryForTeam(opponent,
					rounds);

			if (numTimesWasAff == numTimesTheyWereAff) {
				// coin flip
				if (random.nextDouble() > 0.5) {
					// you are aff
					r.addPair(new Pair(me, opponent));
				} else {
					// you are neg
					r.addPair(new Pair(opponent, me));
				}
			} else if (numTimesWasAff > numTimesTheyWereAff) {
				// you should be neg now
				r.addPair(new Pair(opponent, me));
			} else {
				// you should be aff now
				r.addPair(new Pair(me, opponent));
			}

		}

	}

	public void setCurrentNumJudges(int currentNumJudges) {
		this.currentNumJudges = currentNumJudges;
	}

}
