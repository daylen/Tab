package com.daylenyang.tab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Admin implements Serializable {

	private static final long serialVersionUID = 5378416302299271295L;
	static final int internalVersionNumber = 7;
	static String updateURL = "http://daylenyang.com/tab/ver.txt";

	static Tournament myTournament;
	static BufferedReader console;

	private static String getFileName() throws IOException {
		String fileName = console.readLine();
		if (fileName.indexOf('.') != -1)
			fileName = fileName.substring(0, fileName.indexOf('.'));
		return fileName + ".tournament";
	}

	private static void printTopSpeakers() {
		List<StudentPlusSpeakerPoints> students = new ArrayList<StudentPlusSpeakerPoints>();
		for (Student s : myTournament.getSpeakerPoints().keySet()) {
			students.add(new StudentPlusSpeakerPoints(s,
					getTotalForList(myTournament.getSpeakerPoints().get(s))));
		}
		Collections.sort(students);
		Collections.reverse(students);
		System.out.println(students);
	}

	private static class StudentPlusSpeakerPoints implements
			Comparable<StudentPlusSpeakerPoints> {

		Student s;
		double points;

		public StudentPlusSpeakerPoints(Student s, double points) {
			this.s = s;
			this.points = points;
		}

		@Override
		public int compareTo(StudentPlusSpeakerPoints arg0) {
			if (points > arg0.points)
				return 1;
			else if (points < arg0.points)
				return -1;
			else
				return 0;
		}

		public String toString() {
			return s + " " + points;

		}

	}

	private static double getTotalForList(List<Double> list) {
		double total = 0;
		for (Double d : list) {
			total += d;
		}
		return total;
	}

	private static boolean isTransitionAllowed(char c) {

		if (myTournament == null) {
			switch (c) {
			case 'n':
			case 'o':
			case 'q':
				return true;
			default:
				return false;
			}
		} else {

			switch (c) {
			case 't':
				return (myTournament.getTournamentState() == TournamentState.PRELIM
						|| myTournament.getTournamentState() == TournamentState.ELIM || myTournament
							.getTournamentState() == TournamentState.ALL_DONE);
			case 'i':
				return (myTournament.getTournamentState() == TournamentState.PRELIM || myTournament
						.getTournamentState() == TournamentState.IMPORT_REQUIRED);
			case 'j':
				return (myTournament.getTournamentState() == TournamentState.PRELIM || myTournament
						.getTournamentState() == TournamentState.ELIM);
			case 'p':
				return (myTournament.getTournamentState() == TournamentState.TOURNAMENT_LOADED || myTournament
						.getTournamentState() == TournamentState.PRELIM);
			case 'e':
				return (myTournament.getTournamentState() != TournamentState.ALL_DONE && (myTournament
						.getTournamentState() == TournamentState.PRELIM || myTournament
						.getTournamentState() == TournamentState.ELIM));
			case 'q':
				return true;
			default:
				return false;
			}
		}
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException {

		// Print app information and check for update
		printHello();
		checkForUpdate();

		// Reader
		console = new BufferedReader(new InputStreamReader(System.in));

		// Some more important stuff
		String[] commands = { "new tournament", "open tournament",
				"top speakers", "import data", "judges", "prelim", "elim",
				"quit" };

		while (true) {
			// Display available actions
			System.out.print("Available actions: ");
			for (int i = 0; i < commands.length; i++) {
				if (isTransitionAllowed(commands[i].charAt(0)))
					System.out.print("[" + commands[i].charAt(0) + "]"
							+ commands[i].substring(1) + "  ");
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
			case 't':
				printTopSpeakers();
				break;
			case 'i':
				importDataForPrelimRound();
				break;
			case 'j':
				editJudges();
				break;
			case 'p':
				runPrelimRound();
				break;
			case 'e':
				runElimRound();
				break;
			case 'q':
				quit();
				break;
			default:
				System.out.println("Unknown command.");
			}
		}

	}

	private static void editJudges() throws IOException {
		System.out.println("There are currently "
				+ myTournament.getJudges().size() + " judges.");
		System.out
				.println("Available actions: [a]dd judge  [r]emove judge  [n]ever mind");
		while (true) {
			System.out.print("> ");
			String typed = console.readLine();
			if (typed.equals("a")) {
				addJudge();
				break;
			} else if (typed.equals("r")) {
				removeJudge();
				break;
			} else if (typed.equals("n")) {
				return;
			}
		}
		System.out.println("There are now " + myTournament.getJudges().size()
				+ " judges.");

		saveTournament();

		if (myTournament.getTournamentState() == TournamentState.PRELIM
				&& !myTournament.validateTournament()) {
			System.out
					.println("WARNING: There may not be enough judges. Tab could crash when you try to generate another round.");
		}

	}

	private static void removeJudge() throws IOException {
		System.out.println("Type the number for the judge you want to remove:");
		for (int i = 0; i < myTournament.getJudges().size(); i++) {
			System.out.println(i + ". " + myTournament.getJudges().get(i));
		}
		int index = getIntegerFromUser();
		System.out.println("Removed judge: "
				+ myTournament.getJudges().get(index));
		myTournament.getJudges().remove(index);
	}

	private static void addJudge() throws IOException {
		System.out.print("Judge full name\n> ");
		String name = console.readLine();
		System.out.print("Judge affiliation (press enter if none)\n> ");
		String affiliation = console.readLine();

		Judge j;
		if (affiliation.length() != 0) {
			// judge has school
			School school = null;
			if (myTournament.getSchools().containsKey(affiliation))
				school = myTournament.getSchools().get(affiliation);
			else {
				school = new School(affiliation);
				myTournament.getSchools().put(affiliation, school);
			}

			j = new Judge(school, name);
		} else {
			// just the judge
			j = new Judge(NullSchool.getInstance(), name);
		}
		myTournament.getJudges().add(j);
		System.out.println("Added judge: " + j);

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
		myTournament.setFirstRoundPairingRule(PairingRule.POWER_PROTECT);
		myTournament.setPreliminaryRoundPairingRule(PairingRule.POWER_MATCH);
		myTournament.setEliminationRoundPairingRule(PairingRule.POWER_PROTECT);

		if (!myTournament.validateTournament()) {
			System.err
					.println("You don't have enough judges or rooms to run a tournament. Edit the files and try again.");
			System.exit(0);
		}

		myTournament.setTournamentState(TournamentState.TOURNAMENT_LOADED);

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

	}

	private static void prettyPrintARound(Round r) {
		String[][] table = new String[r.getPairs().size() + 1][4];
		table[0][0] = " ";
		table[0][1] = " ";
		table[0][2] = "JUDGES";
		table[0][3] = "ROOM";

		for (int i = 1; i < table.length; i++) {
			for (int j = 0; j < table[0].length; j++) {
				Pair p = r.getPairs().get(i - 1);

				StringBuilder sb = new StringBuilder();
				switch (j) {
				case 0:
					sb.append(p.getAffTeam());
					sb.append(" (");
					sb.append(myTournament.getBallotsForTeam(p.getAffTeam()));
					sb.append(")");
					table[i][j] = sb.toString();
					break;
				case 1:
					sb.append(p.getNegTeam());
					sb.append(" (");
					sb.append(myTournament.getBallotsForTeam(p.getNegTeam()));
					sb.append(")");
					table[i][j] = sb.toString();
					break;
				case 2:
					table[i][j] = p.getJudges().toString();
					break;
				case 3:
					table[i][j] = p.getRoom().toString();
					break;
				default:
				}

			}
		}

		printTable(table);
	}

	private static void printHello() {
		System.out.println("Tab (Build " + internalVersionNumber + ")");
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

		myTournament.setTournamentState(TournamentState.ELIM);

		/*if (myTournament.getNumEliminationRounds() == 0) {
			while (true) {
				System.out.println("How many elim rounds will there be?");
				System.out.println("1=Break to finals  2=Break to semis  "
						+ "3=Break to quarters  4=Break to octos");

				myTournament.setNumEliminationRounds(getIntegerFromUser());
				break;
			}
		}*/

		// Generate a round
		ElimRoundGen rg = new ElimRoundGen(myTournament,
				myTournament.getEliminationRounds());
		rg.determineAdvancingTeams(myTournament.getAdvancingTeams(), 6);
		//int roundIndex = myTournament.getEliminationRounds().size();

		/*while (true) {
			System.out
					.println("For this round, how many judges will there be per pair?");
			int numJudges = getIntegerFromUser();

			// Check if this number is OK

			// Figure out how many debates are happening
			int numDebates = (int) Math.pow(2,
					myTournament.getNumEliminationRounds() - roundIndex) / 2;

			if (numJudges * numDebates > myTournament.getJudges().size()) {
				System.out.println("You don't have enough judges.");
				myTournament.setTournamentState(TournamentState.PRELIM);
				return;
			}

			rg.setCurrentNumJudges(numJudges);
			break;
		}*/

		// rg.generateManyRoundsAndPickBestOne(myTournament.getAdvancingTeams());
/*
		System.out.println("ELIM ROUND " + (roundIndex + 1));
		prettyPrintARound(myTournament.getEliminationRounds().get(roundIndex));

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

		// Is the tournament over?
		if (roundIndex + 1 == myTournament.getNumEliminationRounds()) {
			System.out.println("The tournament is over.");
			myTournament.setTournamentState(TournamentState.ALL_DONE);
		}

		// Save the round
		saveTournament(); */
		
		myTournament.setTournamentState(TournamentState.ALL_DONE);
	}

	private static void runPrelimRound() throws IOException {
		myTournament.setTournamentState(TournamentState.PRELIM);
		// make a copy of the array from the tournament so that we can modify it
		// when manually pairing teams
		// List<Team> tmpTeams = new ArrayList<Team>(myTournament.getTeams());

		boolean manuallyPairTeams;
		while (true) {
			System.out.println("Do you want to manually pair some teams?");
			System.out.println("[y]es  [n]o");
			System.out.print("> ");
			String input = console.readLine();
			if (input.length() == 0)
				continue;

			char firstChar = input.charAt(0);

			if (firstChar == 'y') {
				manuallyPairTeams = true;
				break;
			} else if (firstChar == 'n') {
				manuallyPairTeams = false;
				break;
			}
		}

		List<Pair> manualPairs = new ArrayList<Pair>();
		if (manuallyPairTeams) {
			// start manually pairing teams
			manualPairs = manuallyPairTeams();
		}

		// Generate a round

		RoundGen rg = new PrelimRoundGen(myTournament,
				myTournament.getPreliminaryRounds());
		int roundIndex = myTournament.getPreliminaryRounds().size();

		rg.generateManyRoundsAndPickBestOne(myTournament.getTeams(), manualPairs);

		System.out.println("ROUND " + (roundIndex + 1));
		prettyPrintARound(myTournament.getPreliminaryRounds().get(roundIndex));

		myTournament.setTournamentState(TournamentState.IMPORT_REQUIRED);

		// Save the round
		saveTournament();
	}

	private static List<Pair> manuallyPairTeams() throws IOException {
		List<Pair> manualPairs = new ArrayList<Pair>();
		while (true) {
			System.out.println("What is the AFF team in the pair?");

			for (int i = 0; i < myTournament.getTeams().size(); i++) {
				System.out.println(i + ". " + myTournament.getTeams().get(i));
			}

			int firstTeamIndex = getIntegerFromUser();
			if (firstTeamIndex < 0
					|| firstTeamIndex >= myTournament.getTeams().size()) {
				System.out.println("Invalid team number.");
				continue;
			}

			System.out.println("What is the NEG team in the pair?");

			for (int i = 0; i < myTournament.getTeams().size(); i++) {
				System.out.println(i + ". " + myTournament.getTeams().get(i));
			}

			int secondTeamIndex = getIntegerFromUser();
			if (secondTeamIndex < 0
					|| secondTeamIndex >= myTournament.getTeams().size()) {
				System.out.println("Invalid team number.");
				continue;
			}

			if (firstTeamIndex == secondTeamIndex) {
				System.out.println("You cannot pick the same team both times.");
				continue;
			}

			manualPairs.add(new Pair(myTournament.getTeams()
					.get(firstTeamIndex), myTournament.getTeams().get(
					secondTeamIndex)));

			while (true) {
				System.out.println("Do you want to manually pair more teams?");
				System.out.println("[y]es  [n]o");
				System.out.print("> ");
				String input = console.readLine();
				if (input.length() == 0)
					continue;

				char firstChar = input.charAt(0);

				if (firstChar == 'y') {
					break;
				} else if (firstChar == 'n') {
					return manualPairs;
				}
			}

		}
	}

	private static int getIntegerFromUser() throws IOException {
		int number;
		while (true) {
			System.out.print("> ");
			String input = console.readLine();
			if (input.length() == 0)
				continue;

			try {
				number = Integer.parseInt(input);
				break;
			} catch (NumberFormatException e) {
				System.out.println("Not a number. Try again.");
				continue;
			}
		}
		return number;
	}

	private static void importDataForPrelimRound() throws IOException {

		// the first round will be zero
		int roundIndex;

		while (true) {
			System.out.println("Import data for which prelim round?");

			roundIndex = getIntegerFromUser() - 1;

			// Verify the round index
			if (roundIndex < 0)
				System.out.println("Too small, try again.");
			else if (roundIndex >= myTournament.getPreliminaryRounds().size())
				System.out.println("Too big, try again.");
			else
				break;
		}

		System.out.println("Import data for round " + (roundIndex + 1));
		String fileName = "prelim" + (roundIndex + 1) + ".csv";
		String sFileName = "speakers.csv";
		System.out.printf("Ensure that %s and %s contain the correct info.\n",
				fileName, sFileName);
		System.out.println("Then press enter to begin import.");
		System.out.print("> ");
		console.readLine();

		// Do the actual importing
		DataImport.parsePrelimRoundResults(fileName, myTournament
				.getPreliminaryRounds().get(roundIndex));
		DataImport.parseSpeakerPoints(sFileName, myTournament, roundIndex);

		boolean proceed = true;
		for (int i = 0; i < myTournament.getPreliminaryRounds().size(); i++) {
			if (!myTournament.validatePrelimRound(i)) {
				System.out.println("WARNING: Round " + (i + 1)
						+ " is not valid");
				proceed = false;
			}
		}

		if (proceed)
			myTournament.setTournamentState(TournamentState.PRELIM);
		else
			System.out
					.println("There are still rounds that do not have correct data.");

		saveTournament();
	}

	private static void saveTournament() throws IOException {
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
