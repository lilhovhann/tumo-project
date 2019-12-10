import java.util.*;

import static java.lang.System.exit;
import static java.lang.System.setSecurityManager;

public class Board implements Comparable<Board>, Cloneable {
    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int DOWN = 3;
    private static final int LEFT = 4;
    static Board goal = null;

    private Block blockMoved; // reference to block that was last moved
    private coordinates size; // size of the overall board
    private Board parent; // previous Board in configuration
    private coordinates blockMovedDir; // indicates which direction the last block was moved in
    private HashMap<Integer, Block> allBlocks; // a collection of all the blocks that have a unique number
    private HashSet<Block> blocks; // Hash set of blocks coming in from solver
    private HashSet<coordinates> emptySpaces;
    private int priority;

    public int getPrior() {
        return priority;
    }

    public Board getParent() {
        return parent;
    }

    public coordinates getBlockMovedDir() {
        return blockMovedDir;
    }

    public Block getBlockMoved() {
        return blockMoved;
    }

    public HashSet<coordinates> getEmptySpaces() {
        return emptySpaces;
    }

    public HashMap<Integer, Block> getAllBlocks() {
        return allBlocks;
    }

    public HashSet<Block> getBlocks() {
        return blocks;
    }

    public coordinates getSize() {
        return size;
    }

    /**
     * general constructor of board. Allocates all needed information in memory and
     * copies over from hashSets to private variable hashSets.
     * 
     * @param configuration the given hashSet of blocks that represent the blocks in
     *                      the board
     * @param empty         the given hashSet of coordinates that represent all the
     *                      empty spaces in the board
     * @param size          represents the size of the board
     * @throws Exception
     */
    public Board(HashSet<Block> configuration, HashSet<coordinates> empty, coordinates size) throws Exception {
        blocks = new HashSet<>();
        allBlocks = new HashMap<>();
        emptySpaces = new HashSet<>();
        Iterator<Block> it1 = configuration.iterator();
        this.size = new coordinates(size);
        while (it1.hasNext()) {
            Block next = it1.next();
            blocks.add(next);
            allBlocks.put(next.hashCode(), next);
        }
        
        for (int i = 0; i < this.size.getRow(); i++) {
            for (int j = 0; j < this.size.getCol(); j++) {
                emptySpaces.add(new coordinates(i, j));
            }
        }

        Iterator<Block> it2 = blocks.iterator();
        while (it2.hasNext()) {
            Block b = it2.next();
            b.addCoordinates(emptySpaces);
        }
    }

    /**
     * iterates through the blocks of the goal board and checks to see if the
     * current board contains every single block of the goal board
     * 
     * @param goalBoard it is the final configuration board , the goal board
     * @return true if the current board equals goal board , false otherwise
     */
    public boolean isSolved(Board goalBoard) {
        Iterator<Block> goal = goalBoard.blocks.iterator();

        while (goal.hasNext()) {
            if (!blocks.contains(goal.next())) {
                return false;
            }
        }

        return true;
    }

    /**
     *
     * @param prev      reference to the previous board
     * @param moved     reference to the newly moved board
     * @param direction reference to the direction that the last block was moved
     */
    // sets info to this if was not there , only fires if it wasnt previously set
    public void previousInstance(Board prev, Block moved, coordinates direction) {
        if (blockMovedDir == null && parent == null && blockMoved == null) {
            parent = prev;
            blockMoved = moved;
            blockMovedDir = new coordinates(direction);
        }
    }

    /**
     * find the block and take the desired destination and move the block in that
     * direction.update empty spaces and blocks if movement is successful. validate
     * all positions and destination position before moving
     * 
     * @param findMe      this is the block that will be used in the moving process
     * @param destination this is the destination coordinate where we want to move
     *                    our block
     * @return new board if movement is successful , null otherwise
     * @throws Exception
     */
    public Board moveOneBlock(Block b, coordinates destination) throws Exception {
        boolean success = false;
        Board clone = clone();
        Block findMe = clone.getBlock(b.getSize().getRow(), b.getSize().getCol(),
            b.getUpperLeft(), b.hashCode());
        if (findMe != null) {
            success = true;
        }
        
        if (!success) {
            return null;
        }

        if (!destination.isValidPosition() || destination.getRow() > size.getRow()
                || destination.getCol() > size.getCol()) {
            return null;
        }

        coordinates oneUp = new coordinates(findMe.oneUp());
        if (oneUp.equals(destination)) {
            success = findMe.moveBlock(UP, clone.blocks, clone.emptySpaces, clone.allBlocks);
        }

        coordinates oneRight = new coordinates(findMe.oneRight());
        if (oneRight.equals(destination)) {
            success = findMe.moveBlock(RIGHT, clone.blocks, clone.emptySpaces, clone.allBlocks);
        }

        coordinates oneDown = new coordinates(findMe.oneDown());
        if (oneDown.equals(destination)) {
            success = findMe.moveBlock(DOWN, clone.blocks, clone.emptySpaces, clone.allBlocks);
        }

        coordinates oneLeft = new coordinates(findMe.oneLeft());
        if (oneLeft.equals(destination)) {
            success = findMe.moveBlock(LEFT, clone.blocks, clone.emptySpaces, clone.allBlocks);
        }

        if (success) {
            clone.parent = this;
            clone.blockMoved = findMe;
            clone.blockMovedDir = destination;
            return clone;
        }
        return null;
    }

    /**
     * uses the info of the block and looks for the Block in the Board. MyNumber
     * only used , the rest are not used
     * 
     * @param height   however many the block has
     * @param width    however many columns the block has
     * @param row      represents the upperLeft coordinate's row of the block.
     * @param col      represents the upperLeft coordinate's column of the block
     * @param myNumber the number that the block was assigned in the HashMap
     * @return the block if its is found , null otherwise
     * @throws Exception
     */
    public Block getBlock(int height, int width, int row, int col, int myNumber) throws Exception {
        Block b = this.allBlocks.get(myNumber);
        return b == null ? null : b;
    }

    /**
     * uses the info of the block and looks for the Block in the Board
     * 
     * @param height   however many the block has
     * @param width    however many columns the block has
     * @param pos      represents the upperLeft coordinates of the block.
     * @param myNumber the number that the block was assigned in the HashMap
     * @return the block if its is found , null otherwise
     * @throws Exception
     */
    public Block getBlock(int height, int width, coordinates pos, int myNumber) throws Exception {
        return getBlock(height, width, pos.getRow(), pos.getCol(), myNumber);
    }


    public String toString() {
        StringBuilder sb = new StringBuilder("Printing the Hash set of blocks:\n");
        Iterator it = blocks.iterator();
        while (it.hasNext()) {
            sb.append(it.next() + "\n");
        }
        sb.append("\nPrinting the empty coordinates\n");
        Iterator it1 = emptySpaces.iterator();
        while (it1.hasNext()) {
            sb.append(it1.next() + "\n");
        }
        Iterator it2 = allBlocks.keySet().iterator();
        while (it2.hasNext()) {
            int n = (int) it2.next();
            sb.append("number: " + n + ":\n" + allBlocks.get(n));
            sb.append("\n\n");
        }
        sb.append("\n");
        return sb.toString();
    }

    // used for debugging , print board
    /**
     * print the board in a double for loop and a 2 dimensional array. Each block
     * coordinate will print a number if there is an empty space , print null. Use
     * Block's number that was assigned to print in each box of the array
     */
    public void printBoard() {
        String[][] board = new String[size.getRow()][size.getCol()];
        Iterator iterator = this.blocks.iterator();

        while (iterator.hasNext()) {
            Block currentBlock = (Block) iterator.next();
            int currentNumber = currentBlock.getMyNumber();
            int upperLeftRow = currentBlock.getUpperLeft().getRow();
            int upperLeftCol = currentBlock.getUpperLeft().getCol();
            int bottomRightRow = currentBlock.getBottomRight().getRow();
            int bottomRightCol = currentBlock.getBottomRight().getCol();

            for (int i = upperLeftRow; i < bottomRightRow; i++) {
                for (int j = upperLeftCol; j < bottomRightCol; j++) {
                    board[i][j] = currentNumber + "";
                    if (currentNumber % 10 == 1 && currentNumber % 100 != 11) {
                        board[i][j] += "st";
                    } else if (currentNumber % 10 == 2) {
                        board[i][j] += "nd";
                    } else if (currentNumber % 10 == 3) {
                        board[i][j] += "rd";
                    } else {
                        board[i][j] += "th";
                    }
                }
            }
        }

        for (int i = 0; i < this.size.getRow(); i++) {
            for (int j = 0; j < this.size.getCol(); j++) {
                System.out.printf("%8S", "[" + board[i][j] + "] ");
            }
            System.out.println();
        }
    }

    // we have to check if boards are the same. we check the size and we check if
    // all the blocks in there are in the same spots

    /**
     * equals compares every single Hashable and potentially dynamic part of 2
     * Boards to ensure that the two are identical if the method returns true.
     * 
     * @param other is a Board that will be compared with this Board
     * @return true if this Board is equal to other Board , false otherwise
     */
    public boolean equals(Object other) {
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        Board b = (Board) other;
        if (size.equals(b.size) && priority == b.priority) {
            if (blocks.size() != b.blocks.size() || emptySpaces.size() != b.emptySpaces.size()
                    || allBlocks.size() != b.allBlocks.size()) {
                return false;
            }
            return blocks.containsAll(b.blocks) && emptySpaces.containsAll(b.emptySpaces)
                    && allBlocks.equals(b.allBlocks) && hashCode() == b.hashCode();
        }
        return false;
    }

    

    /**
     * HashCode is the multiplication of all the HashCodes of the Blocks in this
     * Board
     * 
     * @return HashCode of this Block
     */
    @Override
    public int hashCode() {
        Iterator<Block> it0 = blocks.iterator();
        int hashCode = 1;
        while (it0.hasNext()) {
            hashCode *= it0.next().hashCode();
        }
        return hashCode;
    }

    /**
     * if priority isn't 0 to begin with , update it then return it
     * 
     * @return the cost/priority from current Board to goal Board
     */
    public int getPriority() {
        if (this.priority != 0) {
            this.getCost(goal);
        }
        return this.priority;
    }

    /**
     * this method is necessary in order to be able to sort Boards in the priority
     * queue based on their priority
     * 
     * @param other Board that is being compared with this
     * @return the difference between the priorities
     */
    public int compareTo(Board other) {
        return this.getPriority() - other.getPriority();
    }

    // the lower priority the better

    /**
     *
     * @param goal
     */
    public void getCost(Board goal) {
        if (this.priority != 0) {
            System.out.println("Something is wrong, priority was not 0");
        }
        int temp = 0;
        LinkedList<Block> checkMe = new LinkedList<>();
        for (Block current : this.blocks) {
            checkMe.add(current);
        }

        for (Block current : goal.getBlocks()) {
            if (this.blocks.contains(current)) {
                checkMe.remove(current);
            } else {
                temp += this.Cost(checkMe, current);
            }
        }

        priority = temp;
    }

    /**
     *
     * @param checkMe
     * @param other
     * @return
     */
    private int Cost(LinkedList<Block> checkMe, Block other) {
        int returnMe = Integer.MAX_VALUE;
        int currentMin;

        Block closest = null;
        for (Block b : checkMe) {
            if (b.getSize().getRow() != other.getSize().getRow() 
                    || b.getSize().getCol() != other.getSize().getCol()) {
                        continue;
                    }

            currentMin = other.getUpperLeft().manhattanDist(b.getUpperLeft(), other.getUpperLeft());

            if (currentMin < returnMe) {
                returnMe = currentMin;
                closest = b;
            }
        }
        if (returnMe == Integer.MAX_VALUE) {
            System.out.println("returning max value , goal board doesnt exist");
        }
        checkMe.remove(closest);
        return returnMe;
    }

    public Board clone() throws CloneNotSupportedException {
        try {
            Board copy = (Board) super.clone();
            copy.size = (coordinates) size.clone();
            HashSet<Block> b = new HashSet<>();
            Iterator it0 = blocks.iterator();
            while (it0.hasNext()) {
                b.add(((Block) it0.next()).clone());
            }
            copy.blocks = b;

            HashSet<coordinates> e = new HashSet<>();
            Iterator it1 = emptySpaces.iterator();
            while (it1.hasNext()) {
                e.add(((coordinates) it1.next()).clone());
            }
            copy.emptySpaces = e;

            HashMap<Integer, Block> h = new HashMap<>();
            Iterator<Integer> it2 = allBlocks.keySet().iterator();
            while (it2.hasNext()) {
                Integer in = it2.next();
                Block next = allBlocks.get(in);
                h.put(in, next.clone());
            }
            copy.allBlocks = h;

            return copy;

        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}

