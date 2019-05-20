package GAME;

/* Ilya Lyubomirov -- 16205331 */
/* Mikhail Yankelevich -- 16205326 */
/* Svetoslav Nizhnichenkov -- 17712081 */


/**
 * this class implements doubling cube object. contains necessary methods
 */
public class DoublingCube {
    Colour ownerColour;
    int doublingCubeValue;
    int maxScore;


    /**
     * initial values for colour and value
     */
    public DoublingCube() {
        this.ownerColour = null;
        this.doublingCubeValue = 1;
        this.maxScore = 1;
    }

    /**
     * changes current colour
     */
    public void changeOwnerColour() {
        if (ownerColour.equals(Colour.WHITE)) {
            ownerColour = Colour.BLACK;
        } else {
            ownerColour = Colour.WHITE;
        }
    }

    /**
     * Rolls doubling cube, increases its value by multiplying it by 2.
     *
     * @return true if value of doublingCubeValue is <= 64, false otherwise
     */
    public boolean doubleCubeValue() {
        doublingCubeValue *= 2;
        if (doublingCubeValue > 64) {
            doublingCubeValue /= 2;
            return false;
        }
        return true;
    }

    public void setOwnerColour(Colour colour) {
        this.ownerColour = colour;
    }

    public Colour getOwnerColour() {
        return this.ownerColour;
    }

    public void setMaxScore(int maxScore) {
        this.maxScore = maxScore;
    }
}
