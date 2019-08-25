## Sudoku Solver
Implemented a traditional approach of pencil-and paper [1] to solve trivial sudoku problems. Solves n*n Grid where n>=3.

## Algorithm
- In phase one, locked all  single values if any by intersecting row and columns possible values Starting with set that has lowest preemptive set.
- If all cells are filled, then print output
- Else try solving it by selecting a value from the subset obtained from  possiblevalueList remaining cells.
- If solution found print it
- Else print no Solution

**Note:** Most of Easy and Medium problems will be solved in first phase itself.

## Data Structures
- A priority queue to store cell position and possible values.
- A List to maintain preemptive values of each row and column.
- A Two dimensional array to store the board.


## Packages & class
* Default
  Main.java: Has main method implementation.
* Sudoku
  handler.java: Has the implementation of sudoku solution

  Methods
  * runSolver #sudoku.handler
    Does all required initialization and invokes the solution methods.

  * trySolveByLocking #sudoku.handler
    Has the implementation of locking single value.

  * tryRandomChoice #sudoku.handler
    Selects and assign a value that doesn't violate the sudoku rule from possible list to reach solution

### Input

9 (grid size)

123456789 (list of symbols)

Board Values

.....9743

.5...8.1.

.1.......

8....5...

...8.4...

...3....6

.......7.

.3.5...8.

9724...5.

## Restrictions

* Only Grid with valid inputs will be allowed
* '.' should not be given in symbols list


## References
1. Crook, J.F.. (2009). A pencil-and-paper algorithm for solving Sudoku puzzles. Notices of the American Mathematical Society. 56.
