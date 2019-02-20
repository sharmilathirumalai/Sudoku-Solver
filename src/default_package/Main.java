package default_package;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import suduko.handler;

public class Main {

	public static void main(String[] args) throws Exception{
		Scanner scan = new Scanner(System.in);

		int size = Integer.parseInt(scan.nextLine());
		String[][] multi = new String[size][size];

		String rangeOfValues = scan.nextLine();

		for(int row = 0; row < size; row++) {
			String rowValues = scan.nextLine();

			for(int col = 0; col < size; col++) {
				multi[row][col] = Character.toString(rowValues.charAt(col));
			}
		}
		scan.close();

		//sudoku handler initialization and runner call.
		handler sudoku = new handler(size,  new ArrayList<String>(Arrays.asList(rangeOfValues.split(""))), multi);
		sudoku.runSolver();
	}
}


