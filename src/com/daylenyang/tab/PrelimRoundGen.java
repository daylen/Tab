package com.daylenyang.tab;

import java.util.List;

public class PrelimRoundGen extends RoundGen {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8619948939364734621L;

	public PrelimRoundGen(Tournament tournament, List<Round> rounds) {
		super(tournament, rounds);
		currentPairingRule = tournament.getPreliminaryRoundPairingRule();
	}


}
