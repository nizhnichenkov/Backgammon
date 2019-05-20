package GAME;

/* Ilya Lyubomirov -- 16205331 */
/* Mikhail Yankelevich -- 16205326 */
/* Svetoslav Nizhnichenkov -- 17712081 */

/**
 * This class implements Dice (logic element) that produces
 * an array with two random values from 1 to 6
 */
class Dice {

    Dice() {
    }

    /**
     * creates an integer array of 2 elements
     * and assigns each a random number in
     * the range: 1 to 6; then returns the array
     */
    int[] rollDice() {
        int[] dice = new int[2];

        dice[0] = (int) (Math.random() * 6) + 1;
        dice[1] = (int) (Math.random() * 6) + 1;

        return dice;
    }
}
