package GAME;

/* Ilya Lyubomirov -- 16205331 */
/* Mikhail Yankelevich -- 16205326 */
/* Svetoslav Nizhnichenkov -- 17712081 */

/**
 * Class implements player (logic) object. It is used to change turns,
 * remember nickname and colour, and change logic approach depending on current player
 */
public class Player {
    private String name;  //player name
    private Colour colour;//player colour

    /**
     * Player constructor method
     *
     * @param name   player nickname
     * @param colour player colour
     */
    Player(String name, Colour colour) {
        this.name = name;
        this.colour = colour;
    }

    /**
     * accessor method
     *
     * @return current player name
     */
    public String getName() {
        return this.name;
    }

    /**
     * setter method
     *
     * @param name player nickname
     */
    void setName(String name) {
        this.name = name;
    }


    public Colour getColour() {
        return colour;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }
}