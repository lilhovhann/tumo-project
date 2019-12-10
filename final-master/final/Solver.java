import java.io.*;
import java.nio.file.Path;
import java.util.*;

public class Solver {

    private int rows, columns; // height and width of the board
    private HashSet<Board> visitedBoards; // keeps track of all previous board configurations
    private Board initialBoard; // initial state of the board
    private Board finalBoard; // final state of the board as in we need to have this at the end
    private HashSet<coordinates> emptySpaces; // to see where the empty spaces are

    // read the file line by line , make all the blocks and throw them in to intial
    // configuration,
    // and take the line where it represents the goal and put that in an end goal
    // configuration

    // figure out where the empty spaces are and throw them into a hashset as well

    /**
     *
     * @param initFile
     * @param goalFile
     * @throws Exception
     */
    public Solver(File initFile, File goalFile) throws Exception {
        visitedBoards = new HashSet<>();
        emptySpaces = new HashSet<>();
        HashSet<Block> blocks = readFileAndInitialize(initFile, "initial");
        emptySpaces = calculateEmptySpaces(blocks);
        initialBoard = new Board(blocks, emptySpaces, new coordinates(rows, columns));

        HashSet<Block> finalBlocks = readFileAndInitialize(goalFile, "goal");
        HashSet<coordinates> finalEmpty = new HashSet<>();
        finalEmpty = calculateEmptySpaces(finalBlocks);
        finalBoard = new Board(finalBlocks, emptySpaces, new coordinates(rows, columns));
    }

    /**
     *
     * @param file
     * @param type
     * @return
     * @throws Exception
     */
    // initialize the height and width of the board(s) , set up all the blocks and
    // throw them in a hashset of blocks
    public HashSet<Block> readFileAndInitialize(File file, String type) throws Exception {
        HashSet<Block> blocks = new HashSet<>();
        BufferedReader br = new BufferedReader(new FileReader(file));
        if (type.equals("initial")) {
            String firstLine = br.readLine();
            String[] splitedArray = firstLine.split(" ");
            if (splitedArray.length != 2) {
                throw new Exception("Invalid input: " + firstLine);
            }
            try {
                this.rows = Integer.parseInt(splitedArray[0]);
                this.columns = Integer.parseInt(splitedArray[1]);
            } catch (Exception e) {
                throw new Exception("Invalid input: " + firstLine);
            }
        }

        String line;
        Block block;
        coordinates size;
        coordinates upperLeft;
        int count = 1;
        while ((line = br.readLine()) != null) {
            String[] splited = line.split(" ");
            if (splited.length != 4) {
                throw new Exception("Invalid input: " + line);
            }
            try {
                size = new coordinates(Integer.parseInt(splited[0]), Integer.parseInt(splited[1]));
                upperLeft = new coordinates(Integer.parseInt(splited[2]), Integer.parseInt(splited[3]));
                block = new Block(size, upperLeft, count, new coordinates(this.rows, this.columns));
            } catch (Exception e) {
                throw new Exception("Invalid input: " + line);
            }
            blocks.add(block.clone());

            count++;
        }
        br.close();

        return blocks;
    }

    /**
     *
     * @param currentBoard
     * @return
     */
    public HashSet<coordinates> calculateEmptySpaces(HashSet<Block> currentBoard) {
        HashSet<coordinates> empty = new HashSet<>();

        boolean[][] taken = new boolean[rows][columns];
        Iterator iterator = currentBoard.iterator();
        while (iterator.hasNext()) {
            Block block = (Block) iterator.next();
            for (int i = 0; i < block.getSize().getRow(); i++) {
                for (int j = 0; j < block.getSize().getCol(); j++) {
                    taken[block.getUpperLeft().getRow() + i][block.getUpperLeft().getCol() + j] = true;
                }
            }
        }

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (taken[i][j] == false) {
                    empty.add(new coordinates(i, j));
                }
            }
        }
        return empty;
    }

    // use this for depth first search
    public Board solve() throws Exception {
        int i = 0;
        System.out.println("INITIAL BOARD");
        System.out.println("============");
        initialBoard.printBoard();
        System.out.println("============");
        System.out.println("GOAL BOARD");
        System.out.println("============");
        finalBoard.printBoard();
        Board b = DepthFirstSearch(this.initialBoard, i);
        if (b != null) {
            System.out.println("SOLUTION FOUND");
            this.printPath(b, 0);
            return b;
        }
        System.out.println("SOLUTION NOT FOUND");
        return null;
    }

    public Board DepthFirstSearch(Board currentBoard, int recursiveLevel) throws Exception {
        if (currentBoard != null && currentBoard.isSolved(this.finalBoard)) { 
            return currentBoard;
        }
        if (currentBoard == null) {
            return null;
        }
        System.out.println("Printing the initial board");
        System.out.println(currentBoard);
        currentBoard.printBoard();
        System.out.println(currentBoard.getEmptySpaces());
        Iterator it = currentBoard.getBlocks().iterator();
        while (it.hasNext()) {
            Block current = (Block) it.next();
            Board updated = currentBoard.moveOneBlock(current, current.oneUp());

            System.out.println("After attempting to move up, ");
            if (updated != null) {
                updated.printBoard();
            } else {
                System.out.println("updated board shows null");
            }


            if (updated != null && !visitedBoards.contains(updated)) {
                visitedBoards.add(updated);
                updated = DepthFirstSearch(updated, recursiveLevel + 1);
                if (updated != null && updated.isSolved(finalBoard)) {
                    return updated;
                }
            }

            updated = currentBoard.moveOneBlock(current, current.oneRight());
            System.out.println("After attempting to move right, ");
            if (updated != null) {
                updated.printBoard();
            } else {
                System.out.println("updated board shows null");
            }
         
            if (updated != null && !visitedBoards.contains(updated)) {
                visitedBoards.add(updated);
                updated = DepthFirstSearch(updated, recursiveLevel + 1);
                if (updated != null && updated.isSolved(finalBoard)) {
                    return updated;
                }
            }

            updated = currentBoard.moveOneBlock(current, current.oneDown());
            System.out.println("After attempting to move down, ");
            if (updated != null) {
                updated.printBoard();
            } else {
                System.out.println("updated board shows null");
            }
            
            if (updated != null && !visitedBoards.contains(updated)) {
                visitedBoards.add(updated);
                updated = DepthFirstSearch(updated, recursiveLevel + 1);
                if (updated != null && updated.isSolved(finalBoard)) {
                    return updated;
                }
            }

            updated = currentBoard.moveOneBlock(current, current.oneLeft());
            System.out.println("After attempting to move left, ");
            if (updated != null) {
                updated.printBoard();
            } else {
                System.out.println("updated board shows null");
            }
           
            if (updated != null && !visitedBoards.contains(updated)) {
                visitedBoards.add(updated);
                updated = DepthFirstSearch(updated, recursiveLevel + 1);
                if (updated != null && updated.isSolved(finalBoard)) {
                    return updated;
                }
            }

        }
        
        return null;
    }

    // use this for breadth first search
    public Board Solve() throws Exception {

        System.out.println("INITIAL BOARD");
        System.out.println("============");
        initialBoard.printBoard();
        System.out.println("============");
        System.out.println("GOAL BOARD");
        System.out.println("============");
        finalBoard.printBoard();
        long start = System.nanoTime();
        Board b = BreadthFirstSearch(this.initialBoard);
        long finish = System.nanoTime();
        long time = finish - start;
        if (b != null) {
            System.out.println("SOLUTION FOUND");
            this.printPath(b, time / 1000000);
            return b;
        }
        System.out.println("SOLUTION NOT FOUND");
        return null;
    }

    public Board BreadthFirstSearch(Board init) throws Exception {
        if (init != null && init.isSolved(finalBoard)) {
            return init;
        }
        if (init == null) {
            return null;
        }
        PriorityQueue<Board> boardList = new PriorityQueue<>();
        // LinkedList<Board> boardList = new LinkedList<>();
        boardList.add(init);
        visitedBoards.add(init);
        while (!boardList.isEmpty()) {
            Board b = boardList.poll();
            Iterator it = b.getBlocks().iterator();
            while (it.hasNext()) {
                Block bl = (Block) it.next();
                for (coordinates drctn : coordinates.DIRS) {
                    Board newBoard = b.moveOneBlock(bl, bl.getUpperLeft().addCoordinates(drctn));
                    if (newBoard == null) {
                        continue;
                    }
                    boolean cont = visitedBoards.contains(newBoard);
                    if (!cont) {
                        boardList.add(newBoard);
                        visitedBoards.add(newBoard);
                        newBoard.previousInstance(b, bl, drctn);
                    }
                    if (newBoard.isSolved(finalBoard)) {
                        return newBoard;
                    }
                }
            }

        }
        return null;
    }

    public void printPath(Board b, long time) {
        boolean debug = false;
        int count = 0;
        System.out.println("PRINTING PATH OF SOLVED BOARD");
        if (b != null) {
            while (b.getParent() != null) {
                if (debug) {
                    System.out.println("AT STEP: " + count);
                }
                b.printBoard();
                System.out.println("====================");
                b = b.getParent();
                count++;
            }
            if (debug) {
                System.out.println("AT STEP: " + count);
            }
            b.printBoard();
            count++;
            System.out.println("====================");
            System.out.println(count + " STEPS");
            System.out.println("TIME ELAPSED TO FIND SOLUTION:" + time + " milliseconds");
            System.out.println("DONE");
        }
    }

    public static void main(String args[]) throws Exception {
        File big3 = new File("big.block.3");
        File big3goal = new File("big.block.3.goal");
        Solver s = new Solver(big3, big3goal);

        s.Solve();
    }
}