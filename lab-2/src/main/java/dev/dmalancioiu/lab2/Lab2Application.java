package dev.dmalancioiu.lab2;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Lab2Application {

	public static void main(String[] args) {
		SymbolTable symbolTable = new SymbolTable(51);

		try {
			System.out.println("abc -> " + symbolTable.addIdentifier("abc") );
			System.out.println("a -> " + symbolTable.addIdentifier("a"));
			System.out.println("b -> " + symbolTable.addIdentifier("b"));
			System.out.println("ab -> " + symbolTable.addIdentifier("ab"));
			System.out.println("bc -> " + symbolTable.addIdentifier("bc"));

			System.out.println("1 -> " + symbolTable.addIntConstant(1));
			System.out.println("5 -> " + symbolTable.addIntConstant(5));
			System.out.println("100 -> " + symbolTable.addIntConstant(100));
			System.out.println("20 -> " + symbolTable.addIntConstant(20));
			System.out.println("111 -> " + symbolTable.addIntConstant(111));
			System.out.println("49 -> " + symbolTable.addIntConstant(49));
			System.out.println("76 -> " + symbolTable.addIntConstant(76));

			System.out.println("string -> " + symbolTable.addStringConstant("string"));
			System.out.println("test -> " + symbolTable.addStringConstant("test"));
			System.out.println("lab -> " + symbolTable.addStringConstant("lab"));
			System.out.println("symbol -> " + symbolTable.addStringConstant("symbol"));
			System.out.println("table -> " + symbolTable.addStringConstant("table"));

			System.out.println("abc -> " + symbolTable.addIdentifier("abc"));

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		System.out.println(symbolTable);

	}
}
