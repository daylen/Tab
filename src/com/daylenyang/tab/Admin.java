package com.daylenyang.tab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Admin implements Serializable {

	// TODO implement action to display arbitrary round and speaker points

	private static final long serialVersionUID = 5378416302299271295L;
	static final int internalVersionNumber = 1;
	static String updateURL = "http://daylenyang.com/tab/ver.txt";
	static TournamentState tState;
	static Tournament myTournament;
	static BufferedReader console;

	private static String getFileName() throws IOException {
		String fileName = console.readLine();
		if (fileName.indexOf('.') != -1)
			fileName = fileName.substring(0, fileName.indexOf('.'));
		return fileName + ".tournament";
	}

	private static void getTopSpeaker() {
		if (tState != TournamentState.ELIM)
			return;

		double bestScore = 0;
		Student bestSpeaker = null;
		for (Student s : myTournament.getSpeakerPoints().keySet()) {
			double thisPersonsScore = getTotalForList(myTournament
					.getSpeakerPoints().get(s));
			if (thisPersonsScore > bestScore) {
				bestScore = thisPersonsScore;
				bestSpeaker = s;
			}
		}
		System.out.println("The top speaker is " + bestSpeaker.toString()
				+ " with " + bestScore + " points.");

	}

	private static double getTotalForList(List<Double> list) {
		double total = 0;
		for (Double d : list) {
			total += d;
		}
		return total;
	}

	private static boolean isTransitionAllowed(char cmd) {
		switch (cmd) {
		case 'n':
			return (tState == TournamentState.TOURNAMENT_NOT_LOADED);
		case 'o':
			return (tState == TournamentState.TOURNAMENT_NOT_LOADED);
		case 'i':
			return (tState == TournamentState.PRELIM);
		case 'p':
			return (tState == TournamentState.TOURNAMENT_LOADED || tState == TournamentState.PRELIM);
		case 'e':
			return (tState == TournamentState.PRELIM || tState == TournamentState.ELIM);
		case 't':
			return (tState == TournamentState.ELIM);
		case 'q':
			return true;
		default:
			return false;
		}
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {
		// Initialize tournament state
		tState = TournamentState.TOURNAMENT_NOT_LOADED;

		// Print app name, version, copyright, and expiration date
		printHello();
		checkForUpdate();

		// Some more important stuff
		console = new BufferedReader(new InputStreamReader(System.in));
		char[] commands = { 'n', 'o', 'i', 'p', 'e', 't', 'q' };
		String[] commandExplains = { "ew", "pen", "mport", "relim", "lim",
				"op speaker", "uit" };

		while (true) {
			// Display available actions
			System.out.print("Available actions: ");
			for (int i = 0; i < commands.length; i++) {
				if (isTransitionAllowed(commands[i]))
					System.out.print("[" + commands[i] + "]"
							+ commandExplains[i] + "  ");
			}

			// Get input from user
			System.out.print("\n> ");
			String cmd = console.readLine();

			// Blank line
			if (cmd.length() == 0)
				continue;

			// The letter
			char letter = cmd.toLowerCase().charAt(0);

			// Is allowed?
			if (!isTransitionAllowed(letter)) {
				System.out.println("This command is not available right now.");
				continue;
			}

			switch (letter) {
			case 'n':
				newTournament();
				break;
			case 'o':
				openTournament();
				break;
			case 'i':
				importDataForPrelimRound();
				break;
			case 'p':
				runPrelimRound();
				break;
			case 'e':
				runElimRound();
				break;
			case 't':
				getTopSpeaker();
				break;
			case 'q':
				quit();
				break;
			default:
				System.out.println("Unknown command.");
			}
		}

	}

	private static void newTournament() throws IOException {
		System.out.println("Type a tournament name: ");
		System.out.print("> ");
		String tournamentName = console.readLine();
		String teamFN = "teams.csv";
		String judgeFN = "judges.csv";
		String roomFN = "rooms.csv";
		System.out.printf(
				"Ensure that %s, %s, and %s contain the correct info.\n",
				teamFN, judgeFN, roomFN);
		System.out.println("Then press enter to begin import.");
		System.out.print("> ");
		console.readLine();
		myTournament = new Tournament(tournamentName, teamFN, judgeFN, roomFN);

		System.out.println("Here are the results of the import:\n");
		System.out.println("IMPORT SUMMARY =====");
		System.out
				.printf("Imported %d teams, %d judges, and %d rooms.\n",
						myTournament.getTeams().size(), myTournament
								.getJudges().size(), myTournament.getRooms()
								.size());
		System.out.println("IMPORT DETAILS =====");
		System.out.println("Teams: " + myTournament.getTeams());
		System.out.println("Judges: " + myTournament.getJudges());
		System.out.println("Rooms: " + myTournament.getRooms());
		System.out.println();

		myTournament.setPreliminaryRoundNumJudges(2);
		myTournament.setPreliminaryRoundPairingRule(PairingRule.POWER_MATCH);
		myTournament.setEliminationRoundPairingRule(PairingRule.POWER_PROTECT);

		if (!myTournament.validateTournament()) {
			System.err
					.println("You don't have enough judges or rooms to run a tournament. Edit the files and try again.");
			System.exit(0);
		}

		tState = TournamentState.TOURNAMENT_LOADED;

	}

	private static void openTournament() throws IOException,
			ClassNotFoundException {
		System.out.println("To open a saved tournament, type a file name: ");
		System.out.print("> ");
		myTournament = Persistence.load(getFileName());

		// Print some stuff out
		int numPrelim = myTournament.getPreliminaryRounds().size();
		int numElim = myTournament.getEliminationRounds().size();

		System.out.printf("Tournament '%s' loaded.\n", myTournament.getName());
		System.out.println("Number of prelim rounds so far: " + numPrelim);
		System.out.println("Number of elim rounds so far: " + numElim);

		// Determine and set tState
		if (numPrelim == 0)
			tState = TournamentState.TOURNAMENT_LOADED;
		else if (numPrelim != 0 && numElim == 0)
			tState = TournamentState.PRELIM;
		else
			tState = TournamentState.ELIM;

	}

	private static void prettyPrintARound(Round r, List<Round> rounds,
			boolean printBallotCounts) {
		String[][] table = new String[r.getPairs().size() + 1][4];
		table[0][0] = "AFF";
		table[0][1] = "NEG";
		table[0][2] = "JUDGES";
		table[0][3] = "ROOM";

		for (int i = 1; i < table.length; i++) {
			for (int j = 0; j < table[0].length; j++) {
				Pair p = r.getPairs().get(i - 1);
				if (j == 0)
					if (printBallotCounts)
						table[i][j] = p.getAffTeam().toString()
								+ "="
								+ myTournament.getBallotsForTeam(
										p.getAffTeam(), rounds);
					else
						table[i][j] = p.getAffTeam().toString();
				if (j == 1)
					if (printBallotCounts)
						table[i][j] = p.getNegTeam().toString()
								+ "="
								+ myTournament.getBallotsForTeam(
										p.getNegTeam(), rounds);
					else
						table[i][j] = p.getNegTeam().toString();
				if (j == 2)
					table[i][j] = p.getJudges().toString();
				if (j == 3)
					table[i][j] = p.getRoom().toString();
			}
		}

		printTable(table);
	}

	private static void printHello() {
		System.out.println("Tab");
		System.out.println("(c) 2013 Daylen Yang. All rights reserved.");

		System.out.println();

	}

	private static void checkForUpdate() {
		System.out.println("Checking for updates...");
		try {
			URL url = new URL(updateURL);
			URLConnection urlConnect = url.openConnection();
			String userAgent = "Tab/" + internalVersionNumber + " Java/"
					+ System.getProperty("java.version");
			urlConnect.setRequestProperty("User-Agent", userAgent);
			urlConnect.setConnectTimeout(5000);
			urlConnect.setReadTimeout(5000);
			BufferedReader in = new BufferedReader(new InputStreamReader(
					urlConnect.getInputStream()));

			int latestVer = Integer.parseInt(in.readLine());
			in.close();
			if (internalVersionNumber < latestVer)
				System.out
						.println("WARNING: This version of Tab is out of date!"
								+ " Please download a newer version.");
			else
				System.out.println("This version of Tab is up to date.");

		} catch (Exception e) {
			System.out
					.println("WARNING: There was a problem checking for updates.");
		}
		System.out.println();

	}

	// http://stackoverflow.com/questions/275338/java-print-a-2d-string-array-as-a-right-justified-table
	private static void printTable(String[][] table) {
		// Find out what the maximum number of columns is in any row
		int maxColumns = 0;
		for (int i = 0; i < table.length; i++) {
			maxColumns = Math.max(table[i].length, maxColumns);
		}

		// Find the maximum length of a string in each column
		int[] lengths = new int[maxColumns];
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				lengths[j] = Math.max(table[i][j].length(), lengths[j]);
			}
		}

		// Generate a format string for each column
		String[] formats = new String[lengths.length];
		for (int i = 0; i < lengths.length; i++) {
			formats[i] = "%1$" + lengths[i] + "s"
					+ (i + 1 == lengths.length ? "\n" : " ");
		}

		// Print 'em out
		for (int i = 0; i < table.length; i++) {
			for (int j = 0; j < table[i].length; j++) {
				System.out.printf(formats[j], table[i][j]);
			}
		}
	}

	private static void quit() throws IOException {

		System.exit(0);
	}

	private static void runElimRound() throws NumberFormatException,
			IOException {

		if (!myTournament.validatePrelimRound(myTournament
				.getPreliminaryRounds().size() - 1)) {
			System.out
					.println("You cannot generate an elim round because you need to import data for the previous prelim round first.");
			return;
		}

		tState = TournamentState.ELIM;

		if (myTournament.getNumEliminationRounds() == 0) {
			System.out.println("How many elim rounds will there be?");
			System.out.print("1=Break to finals  2=Break to semis  "
					+ "3=Break to quarters  4=Break to octos\n> ");
			myTournament.setNumEliminationRounds(Integer.parseInt(console
					.readLine()));

		}

		// Generate a round
		RoundGen rg = new ElimRoundGen(myTournament,
				myTournament.getEliminationRounds());
		int roundIndex = myTournament.getEliminationRounds().size();

		System.out
				.print("For this round, how many judges will there be per pair?\n> ");
		rg.setCurrentNumJudges(Integer.parseInt(console.readLine()));

		rg.generateManyRoundsAndPickBestOne(myTournament.getAdvancingTeams());

		System.out.println("ELIM ROUND " + (roundIndex + 1));
		prettyPrintARound(myTournament.getEliminationRounds().get(roundIndex),
				myTournament.getEliminationRounds(), false);

		// Save the round
		saveTournament();

		// Import data
		System.out.println("Result import for elim round " + (roundIndex + 1));
		for (Pair p : myTournament.getEliminationRounds().get(roundIndex)
				.getPairs()) {
			System.out.println("In " + p
					+ ", did the [l]eft team or [r]ight team win?");
			while (true) {
				System.out.print("> ");
				String typed = console.readLine();
				if (typed.equals("l")) {
					p.setBallots(1, 0);
					break;
				} else if (typed.equals("r")) {
					p.setBallots(0, 1);
					break;
				}
			}
		}

		// Save the round
		saveTournament();
	}

	private static void runPrelimRound() throws IOException {

		if (myTournament.getPreliminaryRounds().size() > 0
				&& !myTournament.validatePrelimRound(myTournament
						.getPreliminaryRounds().size() - 1)) {
			System.out
					.println("You cannot generate a new prelim round because you need to import data for the previous prelim round first.");
			return;
		}

		tState = TournamentState.PRELIM;

		// Generate a round

		RoundGen rg = new PrelimRoundGen(myTournament,
				myTournament.getPreliminaryRounds());
		int roundIndex = myTournament.getPreliminaryRounds().size();

		rg.generateManyRoundsAndPickBestOne(myTournament.getTeams());

		System.out.println("ROUND " + (roundIndex + 1));
		prettyPrintARound(myTournament.getPreliminaryRounds().get(roundIndex),
				myTournament.getPreliminaryRounds(), false);

		// Save the round
		saveTournament();
	}

	private static void importDataForPrelimRound() throws IOException {

		System.out
				.print("For what prelim round do you want to import data?\n> ");
		int roundIndex = Integer.parseInt(console.readLine()) - 1;

		System.out.println("Result import for round " + (roundIndex + 1));
		String fileName = "prelim" + (roundIndex + 1) + ".csv";
		String sFileName = "speakers.csv";
		System.out.printf("Ensure that %s and %s contain the correct info.\n",
				fileName, sFileName);
		System.out.println("Then press enter to begin import.");
		System.out.print("> ");
		console.readLine();

		DataImport.parsePrelimRoundResults(fileName, myTournament
				.getPreliminaryRounds().get(roundIndex));
		DataImport.parseSpeakerPoints(sFileName, myTournament, roundIndex);
		saveTournament();
	}

	private static void saveTournament() throws IOException {
		if (tState == TournamentState.TOURNAMENT_NOT_LOADED)
			return;
		String safeTournamentName = myTournament.getName().trim().toLowerCase()
				.replaceAll("[^\\w]", "");
		Date d = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HHmmss");
		String dateString = sdf.format(d);

		String fullName = safeTournamentName + "-autosave-" + dateString
				+ ".tournament";

		Persistence.save(myTournament, fullName);
		System.out.printf("Auto-saved '%s'.\n", fullName);
	}

}
