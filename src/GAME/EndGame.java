package GAME;

/* Ilya Lyubomirov -- 16205331 */
/* Mikhail Yankelevich -- 16205326 */
/* Svetoslav Nizhnichenkov -- 17712081 */

/**
 * This class has method that when called
 * would check whether a player has won
 * by counting the number of checkers for
 * each player that are off board.
 * <p>
 * As soon as one of the players has 15 checkers off board,
 * game ends.
 */
public class EndGame {

    /**
     * This method takes an instance of 'GameLogic' as argument
     * and checks if some of the players have all their checkers
     * off the board. As soon as that becomes true,
     * it announces the name of the winner and stops the game.
     *
     * @param game game logic object
     * @return name of winner
     */
    public String announceWinner(GameLogic game) {

        if (game.getColour() == Colour.WHITE)
            return game.getPlayer1().getName() + " (WHITE)";
        else return game.getPlayer2().getName() + "(BLACK)";
    }

    /**
     * method to check whether a player has won the game
     * it checks for beared off checkers for a passed checker's colour
     *
     * @param game logic object
     * @return true/false depending on # of checkers off board
     */
    public boolean isWinner(GameLogic game) {


        if (forceWin)
            return true;
        /*
         * checks the player who plays with the white checkers
         * has all of his checkers off board
         * returns true if condition is met, false otherwise
         */
        if (game.getWhiteOut().size() == 15) {
            return true;
        }

        if (!game.getMatchScore().isAgreementToDouble() && game.getColour().equals(game.getMatchScore().ownerColour))
            return true;
        else if (!game.getMatchScore().isAgreementToDouble() && !game.getColour().equals(game.getMatchScore().ownerColour))
            return false;

        /*
         * else same condition checked this time for the player
         * that plays with the black checkers
         */
        return game.getBlackOut().size() == 15;
    }

    public void setForceWin(boolean forceWin) {
        this.forceWin = forceWin;
    }

    private boolean forceWin = false;
}