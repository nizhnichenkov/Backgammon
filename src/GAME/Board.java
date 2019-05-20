package GAME;

/* Ilya Lyubomirov -- 16205331 */
/* Mikhail Yankelevich -- 16205326 */
/* Svetoslav Nizhnichenkov -- 17712081 */


/**
 * This class implements Board. Consists of constructors,
 * mutator/accessor and logic methods necessary for core
 * logic operations
 */
public class Board {
    protected Point[] arrayOfPoints;
    protected Point middleBlack;
    protected Point middleWhite;
    protected Point blackOut;
    protected Point whiteOut;

    /**
     * default constructor creates a board object with 24 points, 2 separate bar areas (for
     * blacks and whites), and 2 out areas (for blacks and whites). It also fills the points
     * (stacks) with initial checkers layout
     */
    public Board() {
        arrayOfPoints = new Point[24];
        this.middleBlack = new Point();
        this.middleWhite = new Point();
        this.blackOut = new Point();
        this.whiteOut = new Point();

        for (int i = 0; i < 24; i++) {
            arrayOfPoints[i] = new Point();

            switch (i) {
                case 0:
                    for (int j = 0; j < 2; j++) {
                        arrayOfPoints[0].push(new Checker(Colour.BLACK));
                    }
                    break;
                case 5:
                    for (int j = 0; j < 5; j++) {
                        arrayOfPoints[5].push(new Checker(Colour.WHITE));
                    }
                    break;
                case 7:
                    for (int j = 0; j < 3; j++) {
                        arrayOfPoints[7].push(new Checker(Colour.WHITE));
                    }
                    break;
                case 11:
                    for (int j = 0; j < 5; j++) {
                        arrayOfPoints[11].push(new Checker(Colour.BLACK));
                    }
                    break;
                case 12:
                    for (int j = 0; j < 5; j++) {
                        arrayOfPoints[12].push(new Checker(Colour.WHITE));
                    }
                    break;
                case 16:
                    for (int j = 0; j < 3; j++) {
                        arrayOfPoints[16].push(new Checker(Colour.BLACK));
                    }
                    break;
                case 18:
                    for (int j = 0; j < 5; j++) {
                        arrayOfPoints[18].push(new Checker(Colour.BLACK));
                    }
                    break;
                case 23:
                    for (int j = 0; j < 2; j++) {
                        arrayOfPoints[23].push(new Checker(Colour.WHITE));
                    }
                    break;
            }
        }
    }

    /**
     * getter methods of the data above
     */
    public Point[] getArrayOfPoints() {
        return this.arrayOfPoints;
    }

    public Point getMiddleBlack() {
        return this.middleBlack;
    }

    public Point getMiddleWhite() {
        return this.middleWhite;
    }

    public Point getBlackOut() {
        return blackOut;
    }

    public Point getWhiteOut() {
        return whiteOut;
    }

    /**
     * this method returns the top checker form the point
     *
     * @param idPoint # of the point
     * @return checker object
     */
    public Checker getChecker(int idPoint) {
        return this.arrayOfPoints[idPoint].top();
    }

    /**
     * this method returns the point stack from the getPoint array
     *
     * @param idPoint # of the point
     * @return stack with checkers
     */
    public Point getPoint(int idPoint) {
        return this.arrayOfPoints[idPoint];
    }

    /**
     * this method moves the checker from one point to another
     *
     * @param idFrom # of the point to remove a checker
     * @param idTo   # of the point to move the checker to
     * @return always returns TRUE (only run if allowed by outer logic)
     */
    boolean move(int idFrom, int idTo) {
        add(idTo, remove(idFrom));
        return true;
    }

    /**
     * this method puts the checker to the bar
     *
     * @param id # of the point from which to move the checker
     */
    void putToMiddle(int id) {
        Checker tmp = remove(id);
        if (tmp.getColour().equals(Colour.BLACK)) {
            this.middleBlack.push(tmp);
        } else if (tmp.getColour().equals(Colour.WHITE)) {
            this.middleWhite.push(tmp);
        }
    }

    /**
     * this method moves a checker from the bar to specified point
     *
     * @param toId  # of the point to move a checker to
     * @param color colour of the checker
     */
    boolean moveFromMiddle(int toId, Colour color) {

        if (color.equals(Colour.BLACK)) {
            add(toId, this.middleBlack.pop());
        } else {
            add(toId, this.middleWhite.pop());
        }
        return true;
    }

    /**
     * this method completely removes the checker from the board
     * only used in this class
     *
     * @param id from which point to remove
     * @return checker from the top of its stack
     */
    private Checker remove(int id) {
        return this.arrayOfPoints[id].pop();

    }

    /**
     * this method creates checker on the board
     *
     * @param id      where to add a checker
     * @param checker checker
     */
    private void add(int id, Checker checker) {
        this.arrayOfPoints[id].push(checker);
    }

    /**
     * this method bears off a checker
     *
     * @param id    point #
     * @param color colour of the checker
     * @return checker object
     */
    Checker bearOff(int id, Colour color) {
        Checker tmp = remove(id);

        if (tmp.getColour().equals(Colour.BLACK)) {
            this.blackOut.push(tmp);

        } else if (tmp.getColour().equals(Colour.WHITE)) {
            this.whiteOut.push(tmp);

        }
        return tmp;
    }

    /**
     * This is a cheat layout. as specified in the sprint task
     */
    public void cheatLayout() {

        middleBlack.clear();
        middleWhite.clear();

        blackOut.clear();
        for (int i = 0; i < 13; i++)
            blackOut.push(new Checker(Colour.BLACK));

        whiteOut.clear();
        for (int i = 0; i < 13; i++)
            whiteOut.push(new Checker(Colour.WHITE));

        for (int i = 0; i < 24; i++) {
            arrayOfPoints[i].clear();
            switch (i) {
                case 23:
                    for (int j = 0; j < 2; j++) {
                        arrayOfPoints[23].push(new Checker(Colour.BLACK));
                    }
                    break;
                case 0:
                    arrayOfPoints[0].push(new Checker(Colour.WHITE));
                    arrayOfPoints[0].push(new Checker(Colour.WHITE));
                    break;
            }
        }
    }

    /**
     * this enables empty layout which is necessary for starting game 2 onwards
     */
    public void emptyLayout() {

        middleBlack.clear();

        middleWhite.clear();

        blackOut.clear();

        whiteOut.clear();

        for (int i = 0; i < 24; i++)
            arrayOfPoints[i].clear();

    }

}