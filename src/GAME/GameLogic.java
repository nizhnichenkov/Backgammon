package GAME;

/* Ilya Lyubomirov -- 16205331 */
/* Mikhail Yankelevich -- 16205326 */
/* Svetoslav Nizhnichenkov -- 17712081 */

import java.util.LinkedList;

/**
 * This class implements methods used for game events and core game logic.
 */
public class GameLogic extends Board {
    public GameLogic() {
        super();
    }

    private MatchScore match = new MatchScore(this);
    /**
     * initialises two player objects
     */
    private Player player1 = new Player("Player1", Colour.WHITE);
    private Player player2 = new Player("Player2", Colour.BLACK);

    /**
     * indicates double moves
     */
    private boolean doubleMoves = false;

    /**
     * indicates number of double moves left
     */
    private int doubleMovesLeft = 4;
    /**
     * initialises Dice object
     */
    private Dice dice = new Dice();

    /**
     * initialises an array to store dice throw results
     */
    int[] diceThrow;

    public void setDiceThrow(int[] diceThrow) {
        this.diceThrow = diceThrow;
    }

    /**
     * @return diceThrow array
     */
    public int[] getDiceThrow() {
        return diceThrow;
    }

    /**
     * This method throws dice on demand and stores values in an array above.
     */
    public void throwDice() {
        diceThrow = dice.rollDice();
        if (diceThrow[0] == diceThrow[1]) {
            doubleMoves = true;
            doubleMovesLeft = 4;
        } else {
            doubleMoves = false;
            doubleMovesLeft = 0;
        }
    }

    /**
     * @return current double moves number  left
     */
    public int getDoubleMovesLeft() {
        return this.doubleMovesLeft;
    }

    /**
     * @return if double move performed this time
     */
    public boolean getDoubleMoves() {
        return this.doubleMoves;
    }


    /**
     * initialises new Colour ( first player starts with the WHITES )
     */
    private Colour colour = Colour.WHITE;

    /**
     * colour accessor method
     *
     * @return current colour
     */
    public Colour getColour() {
        return this.colour;
    }

    public MatchScore getMatchScore() {
        return match;
    }

    /**
     * @param fromId
     * @param toId
     * @return boolean result if move withing dice throw
     */
    public boolean idInDiceThrow(int fromId, int toId) {
        if (this.colour.equals(Colour.BLACK) && getMiddleBlack().isEmpty()) {
            return (fromId + diceThrow[0]) == toId || (fromId + diceThrow[1]) == toId;
        } else if (this.colour.equals(Colour.WHITE) && getMiddleWhite().isEmpty()) {
            return (toId + diceThrow[0]) == fromId || (toId + diceThrow[1]) == fromId;
        } else {
            return false;
        }
    }

    /**
     * this method tells if move is in dice throw when moves from middle
     *
     * @param to
     * @return as described
     */
    private boolean inDiceThrowFromMiddle(int to) {
        return (this.colour.equals(Colour.WHITE) && to == 24 - diceThrow[0]) || (this.colour.equals(Colour.WHITE) && to == 24 - diceThrow[1])
                || (this.colour.equals(Colour.BLACK) && to == -1 + diceThrow[0]) || (this.colour.equals(Colour.BLACK) && to == -1 + diceThrow[1]);
    }

    /**
     * this method removes used dice for normal move
     *
     * @param pastId
     * @param presentId
     */
    private void removeUsedDice(int pastId, int presentId) {
        int max;
        int min;
        if (pastId >= presentId) {
            max = pastId;
            min = presentId;
        } else {
            min = pastId;
            max = presentId;
        }
        int move = max - min;
        if (this.doubleMoves) {
            doubleMovesLeft--;
            if (doubleMovesLeft == 1) {
                diceThrow[0] = 0;
            } else if (doubleMovesLeft == 0) {
                diceThrow[1] = 0;
            }
        } else {
            if (diceThrow[0] == move)
                diceThrow[0] = 0;
            else diceThrow[1] = 0;
        }

    }

    /**
     * this method removes used dice for moves from middle
     *
     * @param toId
     */
    private void removeUsedDiceFromMiddle(int toId) {
        if (this.doubleMoves) {
            doubleMovesLeft--;
            if (doubleMovesLeft == 1) {
                diceThrow[0] = 0;
            } else if (doubleMovesLeft == 0) {
                diceThrow[1] = 0;
            }
        } else {
            if (this.colour.equals(Colour.WHITE)) {
                if (toId == 24 - diceThrow[0])
                    diceThrow[0] = 0;
                else if (toId == 24 - diceThrow[1])
                    diceThrow[1] = 0;

            } else {
                if (toId == diceThrow[0] - 1)
                    diceThrow[0] = 0;
                else if (toId == diceThrow[1] - 1)
                    diceThrow[1] = 0;
            }
        }

    }

    /**
     * this method checks if move is allowed
     *
     * @param fromId point # where a checker needs to be moved
     * @param toId   point # from which a checker needs to be moved
     * @return true if such a move is allowed
     */
    private boolean isAllowed(int fromId, int toId) {
        if (fromId == toId)
            return false;
        if (!super.getPoint(fromId).isEmpty())
            if (this.getColour().equals(super.getChecker(fromId).getColour()) && idInDiceThrow(fromId, toId)) {
                if (!super.getPoint(toId).isEmpty())
                    return super.getChecker(fromId).getColour().equals(super.getChecker(toId).getColour())
                            || (hitAllowed(toId) && !this.colour.equals(super.getChecker(toId).getColour()));
                else
                    return true;
            }
        return false;
    }

    /**
     * This method checks if hit is allowed on this point
     *
     * @param toId
     * @return boolean value of allowance
     */
    public boolean hitAllowed(int toId) {
        if (super.getPoint(toId).size() == 1) {
            return !this.colour.equals(super.getPoint(toId).top().getColour());
        }
        return false;
    }


    /**
     * This method is hitting checkers if such move is possible.
     * only from point to point excluding middle
     *
     * @param fromId
     * @param toId
     */
    public void hit(int fromId, int toId) {
        putToMiddle(toId);
        super.move(fromId, toId);
    }

    /**
     * This method moves checker to the side by calling bearOff method from
     * the board. The actual method to be used by client classes.
     * <p>
     * It also checks whether the move is allowed ( e.g. if you try to move from a
     * point where there are no checkers ).
     *
     * @param id point #
     * @return
     */
    public Checker remove(int id) {
        if (getArrayOfPoints()[id].isEmpty())
            return null;
        return bearOff(id, this.getColour());
    }

    /**
     * This method overrides the move method from the board implementing logic
     * inside the actual method to be used by client classes
     *
     * @param idFrom point # (from where to move)
     * @param idTo   point # (where to move to)
     * @return if allowed
     */
    public boolean move(int idFrom, int idTo) {
        if (getArrayOfPoints()[idFrom].isEmpty())
            return false;
        if (isAllowed(idFrom, idTo)) {
            removeUsedDice(idFrom, idTo);

            if (hitAllowed(idTo)) {
                hit(idFrom, idTo);
                return true;
            } else return super.move(idFrom, idTo);
        }
        return false;
    }

    /**
     * This method overrides the moveFromMiddle method from the board implementing
     * logic inside the actual method to be used by client classes
     *
     * @param toId  point #
     * @param color colour of the checker
     * @return returns if move was made
     */
    public boolean moveFromMiddle(int toId, Colour color) {
        if (getMiddleWhite().isEmpty() && color.equals(Colour.WHITE) || getMiddleBlack().isEmpty() && color.equals(Colour.BLACK))
            return false;
        if (inDiceThrowFromMiddle(toId) && hitAllowed(toId) && !this.colour.equals(super.getPoint(toId).top().getColour())) {
            putToMiddle(toId);
            removeUsedDiceFromMiddle(toId);
            return super.moveFromMiddle(toId, color);
        } else if (inDiceThrowFromMiddle(toId) && (super.getPoint(toId).isEmpty())) {
            removeUsedDiceFromMiddle(toId);
            return super.moveFromMiddle(toId, color);
        } else if (inDiceThrowFromMiddle(toId) && super.getPoint(toId).top().getColour().equals(color)) {
            removeUsedDiceFromMiddle(toId);
            return super.moveFromMiddle(toId, color);
        }
        return false;
    }

    /**
     * This method assigns nicknames, and throws dice automatically to determine
     * which player starts first. Re-rolls if values are equal. If second player
     * rolled larger value, then changeturns method is called.
     *
     * @param name1 nickname of the 1st player
     * @param name2 nickname of the 2nd player
     * @return a string containing roll results and info regarding start of the game
     */
    public String startGame(String name1, String name2) {
        player1.setName(name1);
        player2.setName(name2);
        throwDice();

        while (diceThrow[0] == diceThrow[1]) {
            throwDice();
        }

        if (diceThrow[0] < diceThrow[1]) {
            this.colour = Colour.BLACK;
            return ("\n" + name1 + " rolled " + diceThrow[0] + "\n" + name2 + " rolled " + diceThrow[1] + "\n" + name2 + " moves first!");
        }
        return ("\n" + name1 + " rolled " + diceThrow[0] + "\n" + name2 + " rolled " + diceThrow[1] + "\n" + name1 + " moves first!");
    }

    /**
     * this method determines current player (turn)
     *
     * @return current player
     */
    public Player getCurrentPlayer() {
        if (this.colour == Colour.WHITE)
            return player1;
        return player2;
    }

    /**
     * @return player1 / player2 depending on # of checkers removed from board
     */
    public Player getPlayer1() {
        return player1;
    }

    public Player getPlayer2() {
        return player2;
    }


    /**
     * This method allows to change current player (turn)
     *
     * @return returns new current player colour
     */
    String changeTurns() {
        /*
        used to have throwDice here
         */
        if (this.colour == (Colour.WHITE)) {
            this.colour = Colour.BLACK;
            return "Black";
        } else {
            this.colour = Colour.WHITE;
            return "White";
        }
    }

    /**
     * This method returns array of possible moves for all board. array of linked lists
     *
     * @return arr
     */
    public LinkedList<Integer>[] getPossibleMoves() {

        LinkedList<Integer>[] arr = new LinkedList[25];

        for (int i = 0; i < 24; i++) {
            arr[i] = movesFromPoint(i);
        }
        arr[24] = movesFromBar();

        return arr;
    }

    /**
     * this method performs a force move if there is no other move possible.
     *
     * @return boolean value id success
     */
    public boolean forceMove() {
        if (getPossibleMovesNumber() == 1 && movesBearOff().isEmpty()) {
            LinkedList<Integer>[] arr = getPossibleMoves();
            for (int i = 0; i < 24; i++) {
                if (!arr[i].isEmpty()) {
                    move(i, arr[i].get(0));
                    return true;
                }
            }
            if (!arr[24].isEmpty()) {
                moveFromMiddle(arr[24].get(0), this.colour);
                return true;
            }
        }
        return false;
    }

    /**
     * this method is the same as forceMoev^ but only returns true and false
     * without actually moving
     *
     * @return
     */
    public boolean forceMoveAllowed() {
        if (getPossibleMovesNumber() == 1 && movesBearOff().isEmpty()) {
            LinkedList<Integer>[] arr = getPossibleMoves();
            for (int i = 0; i < 24; i++) {
                if (!arr[i].isEmpty()) {
                    return true;
                }
            }
            return !arr[24].isEmpty();
        }
        return false;
    }

    /**
     * a number of total moves from bar + normal
     *
     * @return a number of total moves from bar + normal
     */
    public int getPossibleMovesNumber() {

        int count = 0;
        for (int i = 0; i < 24; i++) {
            count += movesFromPoint(i).size();
        }
        count += movesFromBar().size();
        return count;
    }


    /**
     * This methods checks and returns an array of the point where a checker can
     * move.
     *
     * @param fromId point #
     * @return list  of possible points
     */
    public LinkedList<Integer> movesFromPoint(int fromId) {
        LinkedList<Integer> list = new LinkedList<Integer>();
        if (!super.getPoint(fromId).isEmpty()) {
            if (this.colour.equals(super.getPoint(fromId).top().getColour())) {
                for (int i = 0; i < 24; i++) {
                    if (isAllowed(fromId, i))
                        list.add(i);
                }
            }
        }
        return list;
    }

    /**
     * returns a list of moves from bar
     *
     * @return list of moves
     */
    public LinkedList<Integer> movesFromBar() {
        LinkedList<Integer> list = new LinkedList<Integer>();
        if (getMiddleWhite().isEmpty() && this.colour.equals(Colour.WHITE) || getMiddleBlack().isEmpty() && this.colour.equals(Colour.BLACK))
            return list;
        for (int i = 0; i < 24; i++)
            if (inDiceThrowFromMiddle(i) && hitAllowed(i) && !this.colour.equals(super.getPoint(i).top().getColour())) {
                list.add(i);
            } else if (inDiceThrowFromMiddle(i) && (super.getPoint(i).isEmpty())) {
                list.add(i);
            } else if (inDiceThrowFromMiddle(i) && super.getPoint(i).top().getColour().equals(this.colour)) {
                list.add(i);
            }

        return list;
    }

    /**
     * return a list of the moves for bearoff. Used in GUI
     *
     * @return list of moves
     */
    public LinkedList<Integer> movesBearOff() {
        LinkedList<Integer> list = new LinkedList<Integer>();
        if (this.colour.equals(Colour.WHITE) && !getMiddleWhite().isEmpty() || (this.colour.equals(Colour.BLACK) && !getMiddleBlack().isEmpty()))
            return list;
        for (int i = 0; i < 24; i++) {
            if (inRightCorner()) {
                if (inDiceThrowBearOff(i)) {
                    list.add(i);
                } else if (idMaxBearOff() == i && !findInDiceInCorner() && (diceThrow[0] != 0 || diceThrow[1] != 0) && largerDiceForRemove()) {
                    list.add(i);
                }
            }
        }
        return list;
    }

    /**
     * checks if for certain id bearOff is possible
     * same as bearOff itself, just doesnt do the action.
     *
     * @param id
     * @return boolean value as described
     */
    public boolean movesBearOffID(int id) {
        if (inRightCorner()) {
            if (inDiceThrowBearOff(id)) {
                return true;
            } else
                return idMaxBearOff() == id && !findInDiceInCorner() && (diceThrow[0] != 0 || diceThrow[1] != 0) && largerDiceForRemove();
        }
        return false;
    }

    /**
     * this method checks if the dice which is smaller than dice throw should be bared off
     *
     * @return boolean value as described
     */
    private boolean largerDiceForRemove() {
        if (this.colour.equals(Colour.WHITE))
            return getPossibleMovesNumber() == 0 && (diceThrow[0] > idMaxBearOff() || diceThrow[1] >= idMaxBearOff());

        return getPossibleMovesNumber() == 0 && (diceThrow[0] > 24 - idMaxBearOff() || diceThrow[1] > 24 - idMaxBearOff());
    }

    /**
     * this method checks if all of the checkers are in the wight corner of the bearing off
     *
     * @return boolean value as described
     */
    private boolean inRightCorner() {
        if (this.colour.equals(Colour.WHITE)) {
            for (int i = 5; i < 24; i++) {
                if (!getPoint(i).isEmpty())
                    if (getPoint(i).top().getColour().equals(this.colour))
                        return false;
            }
            return true;
        }
        if (this.colour.equals(Colour.BLACK)) {
            for (int i = 0; i < 18; i++) {
                if (!getPoint(i).isEmpty())
                    if (getPoint(i).top().getColour().equals(this.colour))
                        return false;
            }
            return true;
        }
        return false;
    }

    /**
     * this method checks id certain dice on the point id is in dice throw for bearoff
     *
     * @param id
     * @return boolean value as described
     */
    public boolean inDiceThrowBearOff(int id) {
        if (getPoint(id).isEmpty())
            return false;
        if (this.colour.equals(Colour.WHITE)) {
            return diceThrow[0] == id + 1 || diceThrow[1] == id + 1;
        } else
            return diceThrow[0] == 24 - id || diceThrow[1] == 24 - id;
    }

    /**
     * the method returns max point in the corner for remove. meaning no checkers on the higher point value.
     *
     * @return max point in the corner for remove
     */
    private int idMaxBearOff() {
        int toReturn;
        if (this.colour.equals(Colour.WHITE)) {
            toReturn = 5;
            for (int i = 0; i < 6; i++) {
                if (!getPoint(i).isEmpty()) {
                    toReturn = i;
                }
            }
            return toReturn;
        } else if (this.colour.equals(Colour.BLACK)) {
            for (int i = 18; i < 24; i++) {
                if (!getPoint(i).isEmpty()) {
                    return i;
                }
            }
        }

        return 0;
    }

    /**
     * this method returns if there is a dice in the right corner of the game which can be removed
     * (checks if there are no legal moves for bearoff)
     *
     * @return boolean value as described
     */
    private boolean findInDiceInCorner() {

        for (int i = 0; i < 24; i++) {
            if (!getPoint(i).isEmpty())
                if (inDiceThrowBearOff(i))
                    return true;
        }
        return false;
    }

    /**
     * Removes used dice in case of the BearOff, as situations like larger dice than used should be removed are possible.
     *
     * @param idFrom
     */
    private void removeUsedDiceBearOff(int idFrom) {

        boolean weirdRemove = false;
        if (diceThrow[0] == 0) {
            diceThrow[1] = 0;
            return;
        } else if (diceThrow[1] == 0) {
            diceThrow[0] = 0;
            return;
        }
        if (this.colour.equals(Colour.WHITE)) {
            if (this.doubleMoves) {

                this.doubleMovesLeft--;
                if (this.doubleMovesLeft == 1)
                    diceThrow[0] = 0;
                if (this.doubleMovesLeft == 0)
                    diceThrow[1] = 0;

            } else {

                if (diceThrow[0] == idFrom + 1)
                    diceThrow[0] = 0;
                else if (diceThrow[1] == idFrom + 1)
                    diceThrow[1] = 0;
                else weirdRemove = true;
            }
        } else {
            if (this.doubleMoves) {
                this.doubleMovesLeft--;
                if (this.doubleMovesLeft == 1)
                    diceThrow[0] = 0;
                if (this.doubleMovesLeft == 0)
                    diceThrow[1] = 0;

            } else {
                if (diceThrow[0] == 24 - idFrom)
                    diceThrow[0] = 0;
                else if (diceThrow[1] == 24 - idFrom)
                    diceThrow[1] = 0;
                else weirdRemove = true;
            }

        }
        if (weirdRemove) {
            diceThrow[0] = 0;
            weirdRemove = false;
        }
    }

    /**
     * This is a bearOff method. called if bearOff should be made. It will perform ONLY a legal move
     *
     * @param idFrom
     * @return boolean value as described
     */
    public boolean bearOff(int idFrom) {

        if (this.colour.equals(Colour.WHITE) && !getMiddleWhite().isEmpty() || (this.colour.equals(Colour.BLACK) && !getMiddleBlack().isEmpty()))
            return false;
        if (inRightCorner()) {
            if (inDiceThrowBearOff(idFrom)) {
                remove(idFrom);
                removeUsedDiceBearOff(idFrom);
                return true;
            } else if (idMaxBearOff() == idFrom && !findInDiceInCorner() && (diceThrow[0] != 0 || diceThrow[1] != 0) && largerDiceForRemove()) {
                remove(idFrom);
                removeUsedDiceBearOff(idFrom);
                return true;
            }
        }

        return false;

    }


    /**
     * This method resets the game
     */
    public void resetGame() {
        this.getMatchScore().ownerColour = null;
        this.getMatchScore().doublingCubeValue = 1;
        this.getMatchScore().setAgreementToDouble(true);
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

    public Player getOtherPlayer() {
        if (getCurrentPlayer().equals(player1))
            return player2;
        return player1;
    }

}
