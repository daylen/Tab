package com.daylenyang.tab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ElimRoundGen extends RoundGen {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6544648926199925679L;

	public ElimRoundGen(Tournament tournament, List<Round> rounds) {
		super(tournament, rounds);
		currentPairingRule = tournament.getEliminationRoundPairingRule();
	}

	public void generateManyRoundsAndPickBestOne(List<Team> advancingTeams) {
		determineAdvancingTeams(advancingTeams);
		super.generateManyRoundsAndPickBestOne(advancingTeams);
	}

	private void determineAdvancingTeams(List<Team> advancingTeams) {

		if (advancingTeams.size() == 0) {
			// This is the first elim round, need to determine breaks
			int numTeamsToBreak = (int) Math.pow(2,
					tournament.getNumEliminationRounds());
			advancingTeams.addAll(getTopTeams(numTeamsToBreak));
			System.out.println(advancingTeams);
		} else {
			// This is not the first elim round, just drop teams
			List<Team> winners = new ArrayList<Team>();
			for (Pair p : rounds.get(rounds.size() - 1).getPairs()) {
				if (p.getAffBallots() > p.getNegBallots()) {
					winners.add(p.getAffTeam());
				} else if (p.getAffBallots() < p.getNegBallots()) {
					winners.add(p.getNegTeam());
				} else {
					throw new RuntimeException(
							"Ties are not permitted in elim rounds");
				}
			}
			advancingTeams.retainAll(winners);

		}

	}

	private List<Team> getTopTeams(int numTeams) {

		// In this case, TeamPlusWeight is a misnomer. Really it's
		// TeamPlusBallotCount
		ArrayList<TeamPlusWeight> teamsSortedByBallotCount = new ArrayList<TeamPlusWeight>();

		for (Team team : tournament.getTeams()) {

			teamsSortedByBallotCount
					.add(new TeamPlusWeight(team, tournament.getBallotsForTeam(
							team, tournament.getPreliminaryRounds())));

		}

		Collections.sort(teamsSortedByBallotCount);
		Collections.reverse(teamsSortedByBallotCount);

		ArrayList<Team> teams = new ArrayList<Team>();
		for (TeamPlusWeight tpw : teamsSortedByBallotCount) {
			teams.add(tpw.team);
		}

		// Determine if we need to break a tie

		ArrayList<Team> teamsThatWillBreak = new ArrayList<Team>();

		int tiedScore = tournament.getBallotsForTeam(teams.get(numTeams - 1),
				tournament.getPreliminaryRounds());
		if (tiedScore == tournament.getBallotsForTeam(teams.get(numTeams),
				tournament.getPreliminaryRounds())) {
			// Need to break a tie

			// Find the index of a better score than the tied score
			for (TeamPlusWeight tpw : teamsSortedByBallotCount) {
				if (tpw.weight > tiedScore) {
					teamsThatWillBreak.add(tpw.team);
				} else
					break;
			}

			int remainingTeamsToSelect = numTeams - teamsThatWillBreak.size();

			// Get all the teams with the same ballot count
			ArrayList<Team> tiedTeams = new ArrayList<Team>();

			for (TeamPlusWeight team : teamsSortedByBallotCount) {
				if (tiedScore == team.weight) {
					tiedTeams.add(team.team);
				} else if (tiedScore > team.weight) {
					break;
				}
			}

			// call head to head

			BreakDeterminer breakDet = new BreakDeterminer(tiedTeams,
					remainingTeamsToSelect, tournament.getPreliminaryRounds(),
					tournament.getSpeakerPoints());
			teamsThatWillBreak.addAll(breakDet.getTiedTeamsToBreak());

			return teamsThatWillBreak;

		} else {
			// no need to break tie
			return teams.subList(0, numTeams);
		}

	}

}
