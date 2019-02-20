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

	/* constructor to initialize the required values */
	public handler(int size, ArrayList<String> rangeOfValues, String[][] multi) {
		this.size = size;
		this.rangeOfValues = rangeOfValues;
		this.multi = multi;
	}

	/* solver function call*/
	public void runSolver() {
		for(int index=1; index<= multi.length; index++) {
			possibleRowValues.put(index, new HashSet<>(rangeOfValues));
			possibleColumnValues.put(index, new HashSet<>(rangeOfValues));
		}

		// Computes the possible fits for the row and column.
		for(int row=0; row < multi.length; row++) {
			for(int column=0; column < multi[row].length; column++) {
				if(!multi[row][column].equals(".")) {
					possibleRowValues.get(row+1).remove(multi[row][column]);
					possibleColumnValues.get(column+1).remove(multi[row][column]);
				}
			}
		}

		/* Trying to find full/partial solution by locking the answers for particular cell
		 *  using possible row, column and sub grid values */
		trySolveByLocking();

		// Re building the queue to find whether the full solution is arrived.
		buildQueue();
		if(columnSet.size() > 0 || rowSet.size() > 0) {
			//If not continue to solve it by picking random choice.
			tryRandomChoice(0);

			//No Solution found
			System.out.println("no solution");

		} else {
			// Displays the answer arrived by locking method.
			display();
		}

	}

	void tryRandomChoice(int cellIndex) {
		if(cellIndex == size*size) {
			//Solution found
			display();
			System.exit(0);

		} else {
			int row = cellIndex / size;
			int col = cellIndex % size;

			if(!multi[row][col].equals("."))  {
				//move to next empty cell
				tryRandomChoice(cellIndex+1);

			} else {
				Set<String> subValues = getSubSquareValues(row, col);
				for(String value: rangeOfValues) {
					if(possibleRowValues.get(row+1).contains(value) && possibleColumnValues.get(col+1).contains(value) && !subValues.contains(value)) {
						
						/* try filling the cell with the choices. 
						 * choices follows the insertion order of the list values.
						 */
						multi[row][col] = value;
						possibleRowValues.get(row+1).remove(value);
						possibleColumnValues.get(col+1).remove(value);
						tryRandomChoice(cellIndex+1);
						
						// abort the selection made if any sudoku rule is violated.
						possibleRowValues.get(row+1).add(value);
						possibleColumnValues.get(col+1).add(value);
						multi[row][col] = ".";
					}

				}
			}
		}
	}


	void trySolveByLocking() {
		
		buildQueue();
		
		// Starting with smallest set to track down the value for particular cell.
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
	
	// Compute the value using the particular row's preemptive set.
	void computeRowWise(Entry<Integer, Set<String>> row) {
		int rowIndex = row.getKey() - 1; 
		Set<String> values = row.getValue();

		for(int columnIndex = 0; columnIndex < multi.length; columnIndex++) {
			if(fillIn(rowIndex, columnIndex, values)) break;
		}
	}
	
	// Compute the value using the particular column's preemptive set.
	void computeColumnWise(Entry<Integer, Set<String>> column) {
		int columnIndex = column.getKey() - 1;
		Set<String> values = column.getValue();
		for(int rowIndex = 0; rowIndex < multi.length; rowIndex++) {
			if(fillIn(rowIndex, columnIndex, values)) break;
		}
	}
	
	// Tries to fill the cell by checking for hidden singles in the given preemptive set.
	boolean fillIn(int rowIndex, int columnIndex, Set<String> values) {
		if(multi[rowIndex][columnIndex].equals(".")) {
			if(values.size() == 1) {
				// Found the right fit for the cell.
				String value = values.iterator().next();
				multi[rowIndex][columnIndex] = value;
				possibleColumnValues.get(columnIndex+1).remove(value);
				possibleRowValues.get(rowIndex+1).remove(value);
				buildQueue();

				// Filled all the values in the given set to a cell.
				return true;
				
			} else {
				
				// Finds the singles in the set by intersecting the column, row and grid values.
				Set<String> subValues = getSubSquareValues(rowIndex, columnIndex);
				Set<String> intersection = new HashSet<String>(possibleColumnValues.get(columnIndex+1));
				
				intersection.retainAll(possibleRowValues.get(rowIndex+1));
				intersection.removeAll(subValues);
				
				if(intersection.size() == 1) {
					// Found the right fit for the cell.
					String value = intersection.iterator().next();
					multi[rowIndex][columnIndex] = value;
					values.remove(value);
					possibleColumnValues.get(columnIndex+1).remove(value);
					possibleRowValues.get(rowIndex+1).remove(value);
					
					// Rebuild the queue after modifying the possible values list.
					buildQueue();
				}
				
			}
		}
		
		// Haven't completed filling the entire set.
		return false;
	}

	void buildQueue() {
		// Initializes the set with custom comparator.
		rowSet = new PriorityQueue<Entry<Integer, Set<String>>>(multi.length, new MyComparator()); 
		columnSet = new PriorityQueue<Entry<Integer, Set<String>>>(multi.length, new MyComparator()); 

		Iterator<Entry<Integer, Set<String>>> iter1 = possibleRowValues.entrySet().iterator();
		Iterator<Entry<Integer, Set<String>>> iter2 = possibleColumnValues.entrySet().iterator();
		
		while(iter1.hasNext() || iter2.hasNext()) {
			Entry<Integer, Set<String>> row = iter1.next();
			Entry<Integer, Set<String>> column = iter2.next();
			
			// Add all valid entries in the preemptive set
			if(row.getValue().size() > 0) rowSet.add(row);
			if(column.getValue().size() > 0) columnSet.add(column);		  
		}

	}

	// Gives the  already filled values in a sub grid for the particular cell.
	Set<String> getSubSquareValues(int row, int column) {
		Set<String> subGridValues = new HashSet<String>();
		int subGridSize = (int) Math.sqrt(size);
		int rowStart = row - row % subGridSize; 
		int colStart = column - column % subGridSize;

		for(int colOffset = 0; colOffset < subGridSize; colOffset++){
			for(int rowOffset = 0; rowOffset < subGridSize; rowOffset++){
				String currValue = multi[rowStart + rowOffset][colStart + colOffset];
				if(!currValue.equals(".")) subGridValues.add(currValue);
			}
		}
		
		return subGridValues;
	}

	// Displays the given board values.
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
	// Priority is given for the set with less possible values.
	public int compare(Entry<Integer, Set<String>> x, Entry<Integer, Set<String>> y) 
	{ 
		/* If sets has equal number of values, then priority is given based on key.
		 * It is not a standard way. Choice can be made in different way too.
		 */
		if(x.getValue().size() == y.getValue().size())
			return x.getKey() - y.getKey(); 

		return x.getValue().size() - y.getValue().size();
	} 
}
