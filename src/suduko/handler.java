package suduko;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;


public class handler {
	String[][] multi;
	int size;
	ArrayList<String> rangeOfValues;
	Map<Integer, Set<String>> possibleRowValues = new HashMap<Integer, Set<String>>();
	Map<Integer, Set<String>> possibleColumnValues = new HashMap<Integer, Set<String>>();
	
	private PriorityQueue<Entry<Integer, Set<String>>> rowSet;
	private PriorityQueue<Entry<Integer, Set<String>>> columnSet;
	
	public handler(int size, ArrayList<String> rangeOfValues, String[][] multi) {
		this.size = size;
		this.rangeOfValues = rangeOfValues;
		this.multi = multi;
	}
	
	public void runSolver() {
		for(int index=1; index<= multi.length; index++) {
			possibleRowValues.put(index, new HashSet<>(rangeOfValues));
			possibleColumnValues.put(index, new HashSet<>(rangeOfValues));
		}

		for(int row=0; row < multi.length; row++) {
			for(int column=0; column < multi[row].length; column++) {
				if(multi[row][column] != "0") {
					possibleRowValues.get(row+1).remove(multi[row][column]);
					possibleColumnValues.get(column+1).remove(multi[row][column]);
				}
			}
		}
		trySolveByLocking();
		buildQueue();
		if(columnSet.size() > 0 || rowSet.size() > 0) {
			solveByDFS(0);
			System.out.println("no solution");

		} else {
			display();
		}

	}
	
	void solveByDFS(int cellIndex) {
		if(cellIndex == size*size) {
			display();
			System.exit(0);
		} else {
			int row = cellIndex / size;
			int col = cellIndex % size;

			if(multi[row][col] != "0")  {
				solveByDFS(cellIndex+1);

			} else {
				Set<String> subValues = getSubSquareValues(row, col);
				for(String value: rangeOfValues) {
					if(possibleRowValues.get(row+1).contains(value) && possibleColumnValues.get(col+1).contains(value) && !subValues.contains(value)) {
						multi[row][col] = value;
						possibleRowValues.get(row+1).remove(value);
						possibleColumnValues.get(col+1).remove(value);
						solveByDFS(cellIndex+1);
						possibleRowValues.get(row+1).add(value);
						possibleColumnValues.get(col+1).add(value);
						multi[row][col] = "0";
					}

				}
			}
		}
	}


	void trySolveByLocking() {
		buildQueue();
		while(rowSet.size() > 0 || columnSet.size() > 0) {
			Entry<Integer, Set<String>> row = rowSet.peek();  
			Entry<Integer, Set<String>> column = columnSet.peek();  

			if (row == null && column != null) {
				computeColumnWise(column);
				columnSet.poll();
			} else if (column == null && row != null) {
				computeRowWise(row);
				rowSet.poll();
			} else if(row.getValue().size() < column.getValue().size()) {
				computeRowWise(row);
				rowSet.poll();
			} else {
				computeColumnWise(column);
				columnSet.poll();
			}

		}
	}

	void computeRowWise(Entry<Integer, Set<String>> row) {
		int rowIndex = row.getKey() - 1; 
		Set<String> values = row.getValue();

		for(int columnIndex = 0; columnIndex < multi.length; columnIndex++) {
			if(fillIn(rowIndex, columnIndex, values)) break;
		}
	}


	void computeColumnWise(Entry<Integer, Set<String>> column) {
		int columnIndex = column.getKey() - 1;
		Set<String> values = column.getValue();
		for(int rowIndex = 0; rowIndex < multi.length; rowIndex++) {
			if(fillIn(rowIndex, columnIndex, values)) break;
		}
	}

	boolean fillIn(int rowIndex, int columnIndex, Set<String> values) {
		if(multi[rowIndex][columnIndex] == "0") {
			if(values.size() == 1) {
				String value = values.iterator().next();
				multi[rowIndex][columnIndex] = value;
				possibleColumnValues.get(columnIndex+1).remove(value);
				possibleRowValues.get(rowIndex+1).remove(value);
				buildQueue();
				return true;
			} else {
				Set<String> intersection = new HashSet<String>(possibleColumnValues.get(columnIndex+1));
				intersection.retainAll(possibleRowValues.get(rowIndex+1));
				Set<String> subValues = getSubSquareValues(rowIndex, columnIndex);
				intersection.removeAll(subValues);
				if(intersection.size() == 1) {
					String value = intersection.iterator().next();
					multi[rowIndex][columnIndex] = value;
					values.remove(value);
					possibleColumnValues.get(columnIndex+1).remove(value);
					possibleRowValues.get(rowIndex+1).remove(value);
					buildQueue();
				}
			}
		}
		return false;
	}

	void buildQueue() {
		rowSet = new PriorityQueue<Entry<Integer, Set<String>>>(multi.length, new MyComparator()); 
		columnSet = new PriorityQueue<Entry<Integer, Set<String>>>(multi.length, new MyComparator()); 

		Iterator<Entry<Integer, Set<String>>> iter1 = possibleRowValues.entrySet().iterator();
		Iterator<Entry<Integer, Set<String>>> iter2 = possibleColumnValues.entrySet().iterator();
		while(iter1.hasNext() || iter2.hasNext()) {
			Entry<Integer, Set<String>> row = iter1.next();
			Entry<Integer, Set<String>> column = iter2.next();

			if(row.getValue().size() > 0) rowSet.add(row);
			if(column.getValue().size() > 0) columnSet.add(column);		  
		}

	}

	Set<String> getSubSquareValues(int row, int column) {
		Set<String> subGridValues = new HashSet<String>();
		int subGridSize = (int) Math.sqrt(size);
		int rowStart = row - row % subGridSize; 
		int colStart = column - column % subGridSize;

		for(int colOffset = 0; colOffset < subGridSize; colOffset++){
			for(int rowOffset = 0; rowOffset < subGridSize; rowOffset++){
				String currValue = multi[rowStart + rowOffset][colStart + colOffset];
				if(currValue != "0") subGridValues.add(currValue);
			}
		}
		return subGridValues;
	}

	void display() {
		for(int row=0; row < multi.length; row++) {
			for(int column=0; column < multi[row].length; column++) {
				System.out.print(multi[row][column]);
			}
			System.out.println("\n");
		}
	}
}

class MyComparator implements Comparator<Entry<Integer, Set<String>>> { 
	public int compare(Entry<Integer, Set<String>> x, Entry<Integer, Set<String>> y) 
	{ 
		if(x.getValue().size() == y.getValue().size())
			return x.getKey() - y.getKey();

		return x.getValue().size() - y.getValue().size();
	} 
}
