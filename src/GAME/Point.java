package GAME;

/* Ilya Lyubomirov -- 16205331 */
/* Mikhail Yankelevich -- 16205326 */
/* Svetoslav Nizhnichenkov -- 17712081 */

import java.util.Arrays;

/**
 * This class implements game (board) element Point. This element is visualised as polygon
 * in GUI. Each point contains an array stack (with standard set of array stack ADT methods)
 * that holds number of checkers on the point.
 */
public class Point {

    /**
     * final int contains maximum (constant) number of points on the board
     */
    private static final int CAPACITY = 24;

    /**
     * number of checkers on a Point
     */
    private int size;

    /**
     * initialises an array of checkers (i.e. a Point)
     */
    private Checker[] arr;


    /**
     * Default constructor creates a new array of points of
     * size 24 (number of points on the board)
     */

    Point() {
        arr = new Checker[CAPACITY];
        this.size = 0;
    }

    /**
     * ADT size method
     *
     * @return - returns number of elements on a point
     */
    public int size() {
        return this.size;
    }

    /**
     * ADT isEmpty method
     *
     * @return - returns TRUE if there are no elements on the point
     */
    public boolean isEmpty() {
        return size == 0;
    }


    /**
     * ADT push method
     *
     * @param input - adds a checker on a point
     */
    void push(Checker input) {
        this.arr[size()] = input;
        size++;
    }

    /**
     * ADT pop method
     *
     * @return returns top object on the point
     */
    Checker pop() {
        if (size == 0)
            return null;
        else {
            Checker toReturn = this.arr[size() - 1];
            this.arr[size() - 1] = null;
            size--;
            return toReturn;
        }
    }


    /**
     * ADT top method
     *
     * @return top checker on a point
     */
    public Checker top() {
        if (size == 0)
            return null;
        return this.arr[size() - 1];
    }


    /**
     * returns a string containing colour and number of checkers on a point
     *
     * @return string representation
     */
    public String toString() {
        if (!isEmpty())
            return top().getColour() + " " + size();
        return null;
    }

    void clear() {
        size = 0;
        Arrays.fill(arr, null);
    }
}