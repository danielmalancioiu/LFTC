import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import FA.FA;

public class Scanner {
	private String program;
	private final List<String> tokens;
	private final List<String> reservedWords;
	private SymbolTable symbolTable;
	private List<Pair<String, Pair<Integer, Integer>>> PIF;
	private int index = 0;
	private int currentLine = 1;

	public Scanner() {
		this.symbolTable = new SymbolTable(100);
		this.PIF = new ArrayList<>();
		this.reservedWords = new ArrayList<>();
		this.tokens = new ArrayList<>();
		try {
			readTokens();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setProgram(String program) {
		this.program = program;
	}

	private void readTokens() throws IOException {
		File file = new File("src/main/resources/token.in");
		BufferedReader br = Files.newBufferedReader(file.toPath());
		String line;
		while ((line = br.readLine()) != null) {
			String[] parts = line.split(" ");
			switch (parts[0]) {
			case "program", "var", "if", "else", "print", "for", "while", "arr", "input" -> reservedWords.add(parts[0]);
			default -> tokens.add(parts[0]);
			}
		}
	}

	private void skipSpaces() {
		while (index < program.length() && Character.isWhitespace(program.charAt(index))) {
			if (program.charAt(index) == '\n') {
				currentLine++;
			}
			index++;
		}
	}

	private boolean treatStringConstant() {
		var regexForStringConstant = Pattern.compile("^\\|[a-zA-z0-9_ ?:*^+=.!]*\\|");
		var matcher = regexForStringConstant.matcher(program.substring(index));

		if (!matcher.find()) {
			if (Pattern.compile("^\\|[^|]*\\|").matcher(program.substring(index)).find()) {
				throw new ScannerException("Invalid string constant at line " + currentLine);
			}
			if (Pattern.compile("^\\|[^|]*").matcher(program.substring(index)).find()) {
				throw new ScannerException("Missing | at line " + currentLine);
			}
			return false;
		}
		var stringConstant = matcher.group(0);
		index += stringConstant.length();
		Pair<Integer, Integer> position;
		try {
			position = symbolTable.addStringConstant(stringConstant);
		} catch (Exception e) {
			position = symbolTable.getPositionStringConstant(stringConstant);
		}
		PIF.add(new ImmutablePair<>("str const", position));
		return true;
	}

	private boolean treatIntConstant(){
		if (Pattern.compile("^([+-]?[1-9][0-9]*|0)[a-zA-z_]").matcher(program.substring(index)).find()) {
			return false;
		}
		var fa = new FA("src/main/resources/int.in");
		var intConstant = fa.getNextAccepted(program.substring(index));
		if (Objects.equals(intConstant, null)) {
			return false;
		}
		if ((intConstant.charAt(0) == '+' || intConstant.charAt(0) == '-')
				&& PIF.size() > 0
				&& (PIF.get(PIF.size() - 1).getLeft().equals("int const") || PIF.get(PIF.size() - 1).getLeft().equals("string const") || PIF.get(PIF.size() - 1).getLeft().equals("identifier"))) {
			return false;
		}

		index += intConstant.length();
		Pair<Integer, Integer> position;
		try {
			position = symbolTable.addIntConstant(Integer.parseInt(intConstant));
		} catch (Exception e) {
			position = symbolTable.getPositionIntConstant(Integer.parseInt(intConstant));
		}
		PIF.add(new ImmutablePair<>("int const", position));
		return true;
	}

	private boolean checkIfValid(String possibleIdentifier, String programSubstring) {
		if (reservedWords.contains(possibleIdentifier)) {
			return false;
		}
		if (Pattern.compile("^[A-Za-z][A-Za-z0-9]*").matcher(programSubstring).find()) {
			return true;
		}

		return symbolTable.hasIdentifier(possibleIdentifier);
	}

	private boolean treatIdentifier() {
		var fa = new FA("src/main/resources/identifier.in");
		var identifier = fa.getNextAccepted(program.substring(index));
		if (identifier == null) {
			return false;
		}
		if (!checkIfValid(identifier, program.substring(index))) {
			return false;
		}
		index += identifier.length();
		Pair<Integer, Integer> position;
		try {
			position = symbolTable.addIdentifier(identifier);
		} catch (Exception e) {
			position = symbolTable.getPositionIdentifier(identifier);
		}
		PIF.add(new ImmutablePair<>("identifier", position));
		return true;
	}

	private boolean treatFromTokenList() {
		String possibleToken = program.substring(index).split(" ")[0];
		for (var reservedToken: reservedWords) {
			if (possibleToken.startsWith(reservedToken)) {
				var regex = "^" + "[a-zA-Z0-9_]*" + reservedToken + "[a-zA-Z0-9_]+";
				if (Pattern.compile(regex).matcher(possibleToken).find()) {
					return false;
				}
				index += reservedToken.length();
				PIF.add(new ImmutablePair<>(reservedToken, new ImmutablePair<>(-1, -1)));
				return true;
			}
		}
		for (var token : tokens) {
			if (Objects.equals(token, possibleToken)) {
				index += token.length();
				PIF.add(new ImmutablePair<>(token, new ImmutablePair<>(-1, -1)));
				return true;
			}
			else if (possibleToken.startsWith(token)) {
				index += token.length();
				PIF.add(new ImmutablePair<>(token, new ImmutablePair<>(-1, -1)));
				return true;
			}
		}
		return false;
	}

	private void nextToken(String programFileName) throws ScannerException{
		skipSpaces();
		if (index == program.length()) {
			return;
		}
		if (treatIdentifier()) {
			return;
		}
		if (treatStringConstant()) {
			return;
		}
		if (treatIntConstant()) {
			return;
		}
		if (treatFromTokenList()) {
			return;
		}
		throw new ScannerException(programFileName + " has a lexical error: invalid token at line " + currentLine + ", index " + index);
	}

	public void scan(String programFileName){
		try {
			Path file = Path.of("src/main/resources/" + programFileName);
			setProgram(Files.readString(file));
			index = 0;
			PIF = new ArrayList<>();
			symbolTable = new SymbolTable(47);
			currentLine = 1;
			while (index < program.length()) {
				nextToken(programFileName);
			}
			FileWriter fileWriter = new FileWriter("PIF" + programFileName.replace(".txt", ".out"));
			for (var pair : PIF) {
				fileWriter.write(pair.getKey() + " -> (" + pair.getValue().getKey() + ", " + pair.getValue().getValue() + ")\n");
			}
			fileWriter.close();
			fileWriter = new FileWriter("ST" + programFileName.replace(".txt", ".out"));
			fileWriter.write(symbolTable.toString());
			fileWriter.close();
			System.out.println(programFileName + " is lexically correct");
		} catch (IOException | ScannerException e) {
			System.out.println(e.getMessage());
		}
	}
}