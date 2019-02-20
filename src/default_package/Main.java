package default_package;

import java.util.ArrayList;
import java.util.Arrays;
import suduko.handler;

public class Main {
	static String[][] multi = new String[][]{
		{"0", "0", "0", "0", "0", "0", "0", "0", "0"},
		{"0", "0", "0", "0", "0", "0", "0", "0", "0"},
		{"0", "0", "0", "0", "0", "0", "0", "0", "0"},
		{"0", "0", "0", "0", "0", "0", "0", "0", "0"},
		{"0", "0", "0", "0", "0", "0", "0", "0", "0"},
		{"0", "0", "0", "0", "0", "0", "0", "0", "0"},
		{"0", "0", "0", "0", "0", "0", "0", "0", "0"},
		{"0", "0", "0", "0", "0", "0", "0", "0", "0"},
		{"0", "0", "0", "0", "0", "0", "0", "0", "0"}
	};

	static ArrayList<String> rangeOfValues = new ArrayList<String>(Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8", "9"));


	public static void main(String[] args) {
		handler sudoku = new handler(9, rangeOfValues, multi);
		sudoku.runSolver();
	}
}


