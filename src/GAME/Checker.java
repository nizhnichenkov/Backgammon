package GAME;

/* Ilya Lyubomirov -- 16205331 */
/* Mikhail Yankelevich -- 16205326 */
/* Svetoslav Nizhnichenkov -- 17712081 */

/**
 * This method implements game element Checker, along with necessary
 * constructors and methods
 */
public class Checker {

    private Colour colour;

    public Checker(Colour colour) {
        this.colour = colour;
    }

    public Colour getColour() {
        return this.colour;
    }

    public String toString() {
        return this.colour.toString();
    }
}


