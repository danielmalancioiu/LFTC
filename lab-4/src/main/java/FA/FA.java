package FA;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class FA {
	private List<String> states;
	private List<String> alphabet;
	private final List<Transition> transitions;
	private String initialState;
	private List<String> outputStates;
	private final String filename;

	public FA(String filename) {
		this.filename = filename;
		this.states = new ArrayList<>();
		this.alphabet = new ArrayList<>();
		this.transitions = new ArrayList<>();
		this.initialState = "";
		this.outputStates = new ArrayList<>();
		try {
			init();
		} catch (Exception e) {
			System.out.println("Error when initializing FA");
		}
	}

	private void init() throws Exception {
		var regex = Pattern.compile("^([a-z_]*)=");
		for (String line: Files.readAllLines(Paths.get(filename))) {
			var matcher = regex.matcher(line);
			var match = matcher.find();
			if (matcher.group(0) == null) {
				throw new Exception("Invalid line: " + line);
			}
			switch (matcher.group(0)) {
			case "states=" -> {
				var statesWithCurlyBrackets = line.substring(line.indexOf("=") + 1);
				var states = statesWithCurlyBrackets.substring(1, statesWithCurlyBrackets.length() - 1).trim();
				this.states = List.of(states.split(", *"));
			}
			case "alphabet=" -> {
				var alphabetWithCurlyBrackets = line.substring(line.indexOf("=") + 1);
				var alphabet = alphabetWithCurlyBrackets.substring(1, alphabetWithCurlyBrackets.length() - 1).trim();
				this.alphabet = List.of(alphabet.split(", *"));
			}
			case "out_states=" -> {
				var outputStatesWithCurlyBrackets = line.substring(line.indexOf("=") + 1);
				var outputStates = outputStatesWithCurlyBrackets.substring(1, outputStatesWithCurlyBrackets.length() - 1).trim();
				this.outputStates = List.of(outputStates.split(", *"));
			}
			case "initial_state=" -> this.initialState = line.substring(line.indexOf("=") + 1).trim();
			case "transitions=" -> {
				var transitionsWithCurlyBrackets = line.substring(line.indexOf("=") + 1);
				var transitions = transitionsWithCurlyBrackets.substring(1, transitionsWithCurlyBrackets.length() - 1).trim();
				var transitionsList = List.of(transitions.split("; *"));
				for (String transition : transitionsList) {
					var transitionWithoutParantheses = transition.substring(1, transition.length() - 1).trim();
					var individualValues = List.of(transitionWithoutParantheses.split(", *"));
					this.transitions.add(new Transition(individualValues.get(0), individualValues.get(1), individualValues.get(2)));
				}
			}
			default -> throw new Exception("Invalid line in file");
			}
		}
	}


	private void printListOfString(String listName, List<String> list) {
		System.out.print(listName + " = {");
		for (int i = 0; i < list.size(); i++) {
			if (i != list.size() - 1) {
				System.out.print(list.get(i) + ", ");
			} else {
				System.out.print(list.get(i));
			}
		}
		System.out.println("}");
	}

	public void printStates() {
		printListOfString("states", states);
	}

	public void printAlphabet() {
		printListOfString("alphabet", alphabet);
	}

	public void printOutputStates() {
		printListOfString("out_states", outputStates);
	}

	public void printInitialState() {
		System.out.println("initial_state = " + initialState);
	}

	public void printTransitions() {
		System.out.print("transitions = {");
		for (int i = 0; i < transitions.size(); i++) {
			if (i != transitions.size() - 1) {
				System.out.print("(" + transitions.get(i).getFrom() + ", " + transitions.get(i).getTo() + ", " + transitions.get(i).getLabel() + "); ");
			} else {
				System.out.print("(" + transitions.get(i).getFrom() + ", " + transitions.get(i).getTo() + ", " + transitions.get(i).getLabel() + ")");
			}
		}
		System.out.println("}");
	}

	public boolean checkAccepted(String word) {
		List<String> wordAsList = List.of(word.split(""));
		var currentState = initialState;
		for (String c: wordAsList) {
			var found = false;
			for (Transition transition: transitions) {
				if (transition.getFrom().equals(currentState) && transition.getLabel().equals(c)) {
					currentState = transition.getTo();
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return outputStates.contains(currentState);
	}

	public String getNextAccepted(List<String> word) {
		String state = initialState;
		StringBuilder acceptedWord = new StringBuilder();

		for (String letter : word) {
			String newState = null;
			for (Transition transition : transitions) {
				if (transition.getFrom().equals(state) && transition.getLabel().equals(letter)) {
					newState = transition.getTo();
					break;
				}
			}
			if (newState == null) {
				break;
			}
			state = newState;
			acceptedWord.append(letter);
		}

		if (!outputStates.contains(state)) {
			return null;
		}

		return acceptedWord.toString();
	}

	public String getNextAccepted(String word) {
		return getNextAccepted(stringToListOfChars(word));
	}

	private List<String> stringToListOfChars(String word) {
		List<String> charList = new ArrayList<>();
		for (char c : word.toCharArray()) {
			charList.add(String.valueOf(c));
		}
		return charList;
	}

	public boolean isDFA() {
		if (states.isEmpty() || alphabet.isEmpty() || outputStates.isEmpty()) {
			return false;
		}

		if (!states.contains(initialState)) {
			return false;
		}

		if (!states.containsAll(outputStates)) {
			return false;
		}

		Set<String> seenTransitions = new HashSet<>();
		for (Transition transition : transitions) {
			String key = transition.getFrom() + "," + transition.getLabel();
			if (seenTransitions.contains(key)) {
				return false;
			}
			seenTransitions.add(key);
		}

		for (String state : states) {
			Set<String> seenLabels = new HashSet<>();
			for (Transition transition : transitions) {
				if (transition.getFrom().equals(state)) {
					String label = transition.getLabel();
					if (seenLabels.contains(label)) {
						return false;
					}
					seenLabels.add(label);
				}
			}
			if (!seenLabels.containsAll(alphabet)) {
				return false;
			}
		}

		return true;
	}




}