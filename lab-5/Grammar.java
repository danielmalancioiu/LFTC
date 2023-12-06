import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

public class Grammar {
	private List<String> setOfTerminals;

	private List<String> setOfNonTerminals;

	private List<Production> listOfProductions;

	private String startingSymbol;

	private String filename;

	public Grammar(String filename) {
		this.filename = filename;
		this.setOfNonTerminals = new ArrayList<>();
		this.setOfTerminals = new ArrayList<>();
		this.listOfProductions = new ArrayList<>();
		this.startingSymbol = "";
	}

	public List<String> getSetOfTerminals() {
		return setOfTerminals;
	}

	public List<String> getSetOfNonTerminals() {
		return setOfNonTerminals;
	}

	public List<Production> getListOfProductions() {
		return listOfProductions;
	}

	public String getStartingSymbol() {
		return startingSymbol;
	}

	public List<String> getProductionsForNonTerminal(String nonTerminal) throws Exception {
		if (!setOfNonTerminals.contains(nonTerminal)) {
			throw new Exception("Given nonTerminal does not exist");
		}
		List<String> prodValues = new ArrayList<>();
		for (Production production : listOfProductions) {
			if (Objects.equals(production.getKey(), nonTerminal)) {
				prodValues.addAll(production.getValues());
			}
		}
		return prodValues;
	}

	public List<String> getSymbolsOfRHS(String productionRHS) {
		String[] tokens = productionRHS.split(",");
		//System.out.println(tokens);
		return Arrays.asList(tokens);
	}

	public void readSet(String line, List<String> set) {
		String[] tokens = line.split(" ");
		set.addAll(Arrays.asList(tokens));
	}

	public void readProductions(String line) {
		String[] prodSplit = line.split(" -> ");
		Production production = new Production();
		production.setKey(prodSplit[0]);
		String[] statesSplit = prodSplit[1].split(" \\| ");
		//System.out.println(Arrays.toString(statesSplit));
		production.setValues(new ArrayList<>(Arrays.asList(statesSplit)));
		//System.out.println(production.getValues());
		listOfProductions.add(production);
	}

	public void readFromFile() {
		File file = new File(filename);
		try {
			FileReader fr = new FileReader(file);
			BufferedReader br = new BufferedReader(fr);
			String line;
			int countLine = 0;
			while ((line = br.readLine()) != null) {
				if (countLine == 0) {
					readSet(line, setOfNonTerminals);
					countLine++;
				}
				else if (countLine == 1) {
					readSet(line, setOfTerminals);
					countLine++;
				}
				else if (countLine == 2) {
					startingSymbol = line;
					countLine++;
				}
				else if (countLine == 3) {
					readProductions(line);
				}
			}
			fr.close();
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public boolean isCFG() {
		try {
			Map<String, String> symbolToNonTerminal = new HashMap<>();
			for (Production production : listOfProductions) {
				String key = production.getKey();

				if (key.split(" ").length > 1) {
					System.out.println("Error: Production key contains more than one symbol.");
					return false;
				}

				if (!setOfNonTerminals.contains(key)) {
					System.out.println("Error: Production key is not in the set of non-terminals.");
					return false;
				}

				for (String value : production.getValues()) {
					List<String> symbols = getSymbolsOfRHS(value);
					for (String symbol : symbols) {

						String[] tokens = symbol.split(" ");
						for(String token : tokens) {
							if (!setOfTerminals.contains(token) && !setOfNonTerminals.contains(token)) {
								System.out.println("Error: Symbol '" + token + "' is not in the set of terminals or non-terminals.");
								return false;
							}
						}
						if (symbolToNonTerminal.containsKey(symbol)) {
							System.out.println("The terminal '" + symbol + "' is already assigned to another non-terminal '" + key + "'.");
							return false;
						}
						else {
							symbolToNonTerminal.put(symbol, key);
						}
					}
				}
			}

		if (!setOfNonTerminals.contains(startingSymbol)) {
			System.out.println("Error: Starting symbol is not in the set of non-terminals.");
			return false;
		}

		for (Production production : listOfProductions) {
			String key = production.getKey();
			for (String value : production.getValues()) {
				List<String> symbols = getSymbolsOfRHS(value);
				if (symbols.get(0).equals(key)) {
					System.out.println("Error: Left recursion detected in production " + production);
					return false;
				}
			}
		}
		return true;

	} catch(

	Exception e)

	{
		System.out.println("Error: " + e.getMessage());
		return false;
	}

}

	@Override
	public String toString() {
		return "Grammar{" +
				"setOfTerminals=" + setOfTerminals +
				", setOfNonTerminals=" + setOfNonTerminals +
				", listOfProductions=" + listOfProductions +
				", startingSymbol='" + startingSymbol + '\'' +
				", filename='" + filename + '\'' +
				'}';
	}
}