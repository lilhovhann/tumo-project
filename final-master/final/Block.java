import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.io.*;

public class Block implements Cloneable {

    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int DOWN = 3;
    private static final int LEFT = 4;
    private coordinates upperLeft;
    private coordinates size;
    private coordinates bottomRight;
    private int myNumber;
    private coordinates myBoardSize;
    // private TreeMap<Integer, Block> AllBlocks; //a collection of all the blocks
    // that have a unique number

    // DEBUG VARIABLES AND RUNTIME OF FUNCTIONS
    // debug controllers
    private static boolean DEBUG1 = false, DEBUG2 = false, DEBUG3 = false;

    public static boolean getBLOCK1() {
        return DEBUG1;
    }

    public static void setDebug(boolean one, boolean two, boolean three) {
        DEBUG1 = one;
        DEBUG2 = two;
        DEBUG3 = three;
        if (DEBUG1)
            setCount(0);
        if (DEBUG3)
            resetTimes();
    }

    private static int BlockCount;

    public static void setCount(int count) {
        BlockCount = count;
    }

    public static int getCount() {
        return BlockCount;
    }

    public static void printCount() {
        if (DEBUG1)
            System.out.println("Number of Blocks created: " + BlockCount);
    }

    private static long start;
    private static long EmptyTime, canMoveTime, moveTime;

    public static void resetTimes() {
        EmptyTime = 0;
        canMoveTime = 0;
        moveTime = 0;
    }

    public static long getShouldBeEmpty() {
        return EmptyTime;
    }

    public static long getCanMove() {
        return canMoveTime;
    }

    public static long getMove() {
        return moveTime;
    }

    public static void printTimes() {
        if (DEBUG3) {
            System.out.println("\nTotal runtimes of relevant methods in (Block): ");
            System.out.println("shouldBeEmpty(): " + EmptyTime / 1000000 + "\ncanMove(): " 
                    + canMoveTime / 1000000 + "\nmove(): " + moveTime / 1000000);
        }
    }

    /**
     * default constructor of Block. makes all important assignments and allocates
     * necessary space with necessary validations
     * 
     * @param size        represents size of block that this block is assigned to.
     * @param upperLeft   represents upperLeft coordinates of the Block
     * @param myNumber    represents number that belongs to this block in HasMap of
     *                    integers and Blocks
     * @param myBoardSize represents Board size that this Block is in.
     * @throws InvalidTypeException If there is a Type mismatch during assignment
     */
    public Block(coordinates size, coordinates upperLeft, int myNumber, coordinates myBoardSize) 
            throws Exception {
        if (!(size.isValidSize()) || (size.getRow() > myBoardSize.getRow()) 
                || (size.getCol() > myBoardSize.getCol())) {
            throw new Exception("Invalid size");
        }
        this.size = new coordinates(size);

        if (!upperLeft.isValidPosition() || upperLeft.getRow() > myBoardSize.getRow()
                || upperLeft.getCol() > myBoardSize.getCol()) {
            throw new Exception("Invalid position");
        }
        this.upperLeft = new coordinates(upperLeft);
        this.myNumber = myNumber;

        if (!myBoardSize.isValidSize()) {
            throw new Exception("Invalid size");
        }
        this.myBoardSize = new coordinates(myBoardSize);
        updateBottomRight();
    }

    /**
     * @return upperLeft coordinates for this Block
     */
    public coordinates getUpperLeft() {
        return new coordinates(upperLeft);
    }

    /**
     * @return size as coordinates for this Block
     */
    public coordinates getSize() {
        return new coordinates(size);
    }

    /**
     * @return the size of the Board that this Block belongs to
     */
    public coordinates getMyBoardSize() {
        return new coordinates(myBoardSize);
    }

    /**
     * @return bottomRight coordinates of this Block
     */
    public coordinates getBottomRight() {
        return new coordinates(bottomRight);
    }

    /**
     * @return the number that this Block was assigned
     */
    public int getMyNumber() {
        return myNumber;
    }

    /**
     * takes new coordinates and assigns this Blocks row and column to those and
     * updates BottomRight afterwards
     * 
     * @param newCoordinates new coordinates that this Block will use as its
     *                       position
     */
    private void updateCoordinates(coordinates newCoordinates) throws Exception {
        upperLeft.setRow(newCoordinates.getRow());
        upperLeft.setCol(newCoordinates.getCol());
        updateBottomRight();
    }

    /**
     * This updates BottomRight and should only fire when upperLeft coordinates are
     * updated, although no problem occurs when it is called by accident. Assertion
     * is for validation of coordinates
     */
    private void updateBottomRight() throws Exception {
        if (!(upperLeft.addCoordinates(size).isValidPosition())) {
            throw new Exception("Invalid position");
        }
        bottomRight = upperLeft.addCoordinates(size);
        if (bottomRight.getRow() > myBoardSize.getRow() 
                || bottomRight.getCol() > myBoardSize.getCol()) {
            throw new Exception("Invalid position");
        }
    }

    /**
     * Most important method of this class. Creates a copy of this Block. removes
     * the copy from the HashSet Blocks. removes the copy from the HashMap.removes
     * the Key from the KeySet as well and removes coordinates from the empty
     * spaces. proceeds to make movement if possible, and updates the changes on the
     * copy.Re-adds the coordinates to the empty spaces and re-adds to the HashSet
     * of Blocks and HashMaps of Blocks
     * 
     * @param direction       direction that the Block should be moved in
     * @param blocks          HashSet of Blocks
     * @param availableSpaces the HashSet of empty spaces
     * @param AllBlocks       the HashMap of Blocks
     * @return true if Block was moved successfully , false otherwise
     * @throws Exception
     */
    public boolean moveBlock(int direction, HashSet<Block> blocks, HashSet<coordinates> availableSpaces,
            HashMap<Integer, Block> AllBlocks) throws Exception {
        if (!checkIfEmpty(direction, availableSpaces)) {
            return false;
        }
        Block copy = clone();
        blocks.remove(copy);
        AllBlocks.remove(copy.hashCode(), copy);
        removeKeyFromHashMap(AllBlocks, copy.hashCode());
        copy.removeCoordinates(availableSpaces);
        if (direction == UP) {
            copy.updateCoordinates(copy.oneUp());
        }
        if (direction == RIGHT) {
            copy.updateCoordinates(copy.oneRight());
        }
        if (direction == DOWN) {
            copy.updateCoordinates(copy.oneDown());
        }
        if (direction == LEFT) {
            copy.updateCoordinates(copy.oneLeft());
        }

        copy.addCoordinates(availableSpaces);
        blocks.add(copy);
        AllBlocks.put(copy.hashCode(), copy);
        return true;
    }

    /**
     * makes sure to not go out of Bounds, if there is a risk , returns same
     * coordinates
     * 
     * @return the one up coordinates from where upperLeft of this Block is
     */
    public coordinates oneUp() {
        if (upperLeft.getRow() == 0) {
            return new coordinates(upperLeft);
        }
        return new coordinates(upperLeft.getRow() - 1, upperLeft.getCol());
    }

    /**
     * makes sure to not go out of Bounds, if there is a risk , returns same
     * coordinates
     * 
     * @return the right coordinates from where upperLeft of this Block is
     */
    public coordinates oneRight() {
        if (bottomRight.getCol() == myBoardSize.getCol()) {
            return new coordinates(upperLeft);
        }
        return new coordinates(upperLeft.getRow(), upperLeft.getCol() + 1);
    }

    /**
     * makes sure to not go out of Bounds, if there is a risk , returns same
     * coordinates
     * 
     * @return the one down coordinates from where upperLeft of this Block is
     */
    public coordinates oneDown() {
        if (bottomRight.getRow() == myBoardSize.getRow()) {
            return new coordinates(upperLeft);
        }
        return new coordinates(upperLeft.getRow() + 1, upperLeft.getCol());
    }

    /**
     * makes sure to not go out of Bounds, if there is a risk , returns same
     * coordinates
     * 
     * @return the left coordinates from where upperLeft of this Block is
     */
    public coordinates oneLeft() {
        if (upperLeft.getCol() == 0) {
            return new coordinates(upperLeft);
        }
        return new coordinates(upperLeft.getRow(), upperLeft.getCol() - 1);
    }

    /**
     * iterates through the KeySet of the HashMap and looks to see if the HashCode
     * is there and if it is, removes it from the KeySet.
     * 
     * @param AllBlocks Maps an integer to a Block because each Block is assigned an
     *                  integer
     * @param hash      represents the HashCode that is to be removed the Map
     * @return true if Key is Successfully removed , false otherwise
     */
    public boolean removeKeyFromHashMap(HashMap<Integer, Block> AllBlocks, int hash) {
        Iterator<Integer> it = AllBlocks.keySet().iterator();
        while (it.hasNext()) {
            if (it.next() == hash) {
                AllBlocks.remove(hash);
                return true;
            }
        }
        return false;
    }

    /**
     * if the hashSet doesn't contain all the coordinates to move to then this means
     * its occupied.This method checks the direction and checks if its possible for
     * the current block to be moved there by using a for loop and checking if the
     * HasSet 'empty' has empty coordinates in the location where the block would be
     * if it was moved
     * 
     * @param direction identifies left right up down
     * @param empty     hashSet of available spaces to go to
     * @return false if you cant move this ( block ) to the desired direction. true
     *         if u can
     */
    private boolean checkIfEmpty(int direction, HashSet<coordinates> empty) throws Exception {
        if (direction > 4 || direction < 1)
            throw new Exception("Invalid Direction! given direction: " + direction);
        if (direction == UP) {
            for (int i = upperLeft.getCol(); i < bottomRight.getCol(); i++) {
                if (!empty.contains(new coordinates(upperLeft.getRow() - 1, i))) {
                    return false;
                }
            }
        }
        if (direction == RIGHT) {
            for (int i = upperLeft.getRow(); i < bottomRight.getRow(); i++) {
                if (!empty.contains(new coordinates(i, upperLeft.getCol() + size.getCol()))) {
                    return false;
                }
            }
        }
        if (direction == DOWN) {
            for (int i = upperLeft.getCol(); i < bottomRight.getCol(); i++) {
                if (!empty.contains(new coordinates(upperLeft.getRow() + size.getRow(), i))) {
                    return false;
                }
            }
        }
        if (direction == LEFT) {
            for (int i = upperLeft.getRow(); i < bottomRight.getRow(); i++) {
                if (!empty.contains(new coordinates(i, upperLeft.getCol() - 1))) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * use he size and position of this Block to basically find them in the HasSet
     * and add coordinates into the HashSet this would mean that we are removing the
     * coordinates where the Block takes up space so by adding new coordinates we
     * are freeing space in the HasSet technically. This is the opposite of the
     * addCoordinates method.
     * 
     * @param empty represents the HashSet of all the empty spaces in a Board that
     *              represents same Board as the one that contains all the Blocks
     *              that this Block is found in
     */
    void removeCoordinates(HashSet<coordinates> empty) {
        for (int i = upperLeft.getRow(); i < bottomRight.getRow(); i++) {
            for (int j = upperLeft.getCol(); j < bottomRight.getCol(); j++) {
                empty.add(new coordinates(i, j));
            }
        }
    }

    /**
     * Use the size and position of this given Block and at each available position
     * (as a coordinate) add coordinates of the Block by removing the empty
     * coordinates from the HasSet. This is the opposite of removeCoordinates
     * 
     * @param empty represents the HashSet of all the empty spaces in a Board that
     *              represents same Board as the one that contains all the Blocks
     *              that this Block is found in
     */
    void addCoordinates(HashSet<coordinates> empty) {
        for (int i = upperLeft.getRow(); i < bottomRight.getRow(); i++) {
            for (int j = upperLeft.getCol(); j < bottomRight.getCol(); j++) {
                empty.remove(new coordinates(i, j));
            }
        }
    }

    /**
     * a Block is uniquely identified by its size and upperLeft coordinates , since
     * all pieces are rectangular, and we make sense of a block by returning those
     * as a string
     * 
     * @return all the important position information in a string
     */
    @Override
    public String toString() {
        return "Size: " + size + "\tUpper left coordinates: " + upperLeft 
                + "\tBottom right coordinates: " + bottomRight;
    }

    /**
     *
     * This function makes a deepClone of a Block
     * 
     * @return a new Block with same characteristics as this Block
     * @throws Exception left unused
     */
    // Block deepClone() throws Exception {
    //     coordinates upperLeftCopy = new coordinates(upperLeft);
    //     coordinates sizeCopy = new coordinates(size);
    //     coordinates bottomRightCopy = new coordinates(bottomRight);
    //     coordinates myBoardSizeCopy = new coordinates(myBoardSize);
    //     return new Block(sizeCopy, upperLeftCopy, myNumber, myBoardSizeCopy);
    // }


    /** 
     * compares position and size, as well as HashCode. Notice how two identical
     * Blocks that are not in the same position are two separate entities and
     * therefore have different HashCodes, unlike coordinates where two having same
     * row and col are equal.Print
     * 
     * @param other block that will be compared with this block
     * @return true if they are equal , false otherwise
    */
    @Override
    public boolean equals(Object other) {
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }
        Block otherBlock = (Block) other;
        return upperLeft.equals(otherBlock.upperLeft) && size.equals(otherBlock.size)
                && hashCode() == otherBlock.hashCode();
    }

    /**
     * determines unique hashCode based on position of block and size of block
     * 
     * @return hashCode that was created
     */
    @Override
    public int hashCode() {
        return (int) (Math.pow((3), size.getCol()) + size.getRow() * 5 
                + Math.pow(upperLeft.getRow(), 3) + Math.pow(upperLeft.getCol(), 5));
    }

    @Override
    public Block clone() throws CloneNotSupportedException {
        try {
            Block copy = (Block) super.clone();
            copy.upperLeft = upperLeft.clone();
            copy.size = size.clone();
            copy.bottomRight = bottomRight.clone();
            copy.myBoardSize = myBoardSize.clone();
            return copy;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }
}