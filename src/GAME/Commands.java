package GAME;

/* Ilya Lyubomirov -- 16205331 */
/* Mikhail Yankelevich -- 16205326 */
/* Svetoslav Nizhnichenkov -- 17712081 */


import java.util.LinkedList;

/**
 * This class implements logic methods that are called depending on user inputs.
 * It includes methods such as move, remove, etc.
 */
public class Commands {

    /**
     * create GameLogic and Commands objects
     */
    private GameLogic game;

    public Commands(GameLogic game) {
        this.game = game;
    }

    /**
     * linked list containing all possible legal moves in current turn
     * also includes its setter
     */
    private LinkedList<String> allMoves = new LinkedList<>();

    public void setAllMoves(LinkedList<String> allMoves) {
        this.allMoves = allMoves;
    }

    /**
     * General method that takes user's input. Depending on the input, the method
     * then calls a specific function in class 'Game'.
     *
     * @param command command entered by user
     * @return String containing current action or invalid action to be printed
     */
    public String enterCommand(String command) {
        String[] splitCommand;
        String playerColour;

        command = command.toLowerCase();
        splitCommand = command.split(" ");
        String getCommandName = splitCommand[0];

        switch (getCommandName) {
            case "next":
                game.diceThrow[0] = 0;
                game.diceThrow[1] = 0;
                playerColour = game.changeTurns();
                return ("\n" + playerColour + " Turn\t");
            case "bar":
                boolean checkFBar;
                checkFBar = moveFromMiddle(command);
                if (checkFBar) {
                    return ("Moved checker from BAR to " + splitCommand[1]);
                } else {
                    return ("\nInvalid move!\nPlease enter correct move.");
                }
            case "cheat":
                game.cheatLayout();
                return ("Cheat entered");
            case "double":
                return "Doubling is not allowed at the moment!";
            default:
                String text;
                text = defaultCommand(splitCommand[0]);
                if (!text.equals("")) {
                    return text;
                }
                try {
                    if (splitCommand[1].equals("off")) {
                        boolean checkRemove;
                        checkRemove = remove(command);
                        if (checkRemove) {
                            return ("Removed checker from point " + splitCommand[0]);
                        } else {
                            return ("\nInvalid move!\nPlease enter correct move.");
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException ignored) {
                }
                boolean checkMove;
                checkMove = move(command);
                if (checkMove) {
                    return ("Moving checker from point " + splitCommand[0] + " to " + splitCommand[1]);
                } else {
                    return ("\nInvalid move!\nPlease enter correct move.");
                }
        }
    }

    /**
     * this method compares entered command (letter A, B,...) to the possible moves list.
     * if the command is found in this list -> calls enterCommand with "old" moving parameters
     *
     * @param text entered command
     * @return either result of enterCommand or empty string
     */
    private String defaultCommand(String text) {
        try {
            for (String allMove : allMoves) {
                String[] inputs = allMove.split(" ");
                if (inputs[1].endsWith("*")) {
                    inputs[1] = inputs[1].substring(0, inputs[1].length() - 1);
                }
                String[] actualCommand = inputs[1].split("-");
                if (text.equals(inputs[0].toLowerCase())) {
                    return enterCommand(actualCommand[0] + " " + actualCommand[1]);
                }
            }
        } catch (ArrayIndexOutOfBoundsException ignored) {
        }
        return "";
    }

    /*
     * methods below call game logic functions depending on the command.
     * each method checks that user's input is in acceptable scope.
     * if the latter does not hold or user's input lacks parameters asks
     * for input again
     */

    /**
     * Reverses the numbers of points on the board for when players change turns
     *
     * @param id user input (point #)
     * @return id to use by logic
     */
    private int reverse(int id) {
        return 23 - id;
    }

    /**
     * Removes a checker indexed by a position on the board
     *
     * @param command command entered by user
     */
    private boolean remove(String command) {
        String[] splitCommand;
        int checkerPos;

        splitCommand = command.split(" ");

        try {
            if (Integer.parseInt(splitCommand[0]) <= 24 && Integer.parseInt(splitCommand[0]) >= 1) {
                checkerPos = Integer.parseInt(splitCommand[0]) - 1;
                if (game.getColour().equals(Colour.BLACK)) {
                    checkerPos = reverse(checkerPos);
                }
                /*
                 * calls the 'bearOff' method in class 'Game' to remove the checker from board
                 */
                return game.bearOff(checkerPos);
            }
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }

    /**
     * Move a checker from position 'x' ( fromPos ) to position 'y' ( toPos )
     *
     * @param command command entered by user
     */
    private boolean move(String command) {
        String[] splitCommand;
        int fromPos;
        int toPos;

        splitCommand = command.split(" ");

        try {
            if (Integer.parseInt(splitCommand[0]) <= 24 && Integer.parseInt(splitCommand[0]) >= 1) {
                if (Integer.parseInt(splitCommand[1]) <= 24 && Integer.parseInt(splitCommand[1]) >= 1) {
                    fromPos = Integer.parseInt(splitCommand[0]) - 1;
                    toPos = Integer.parseInt(splitCommand[1]) - 1;
                    if (game.getColour().equals(Colour.BLACK)) {
                        fromPos = reverse(fromPos);
                        toPos = reverse(toPos);
                    }
                    /*
                     * Checks if arguments entered by user are legal. If not, prints that the move was invalid
                     * calls the 'move' method in class 'Game' to move a checker on the board
                     */
                    return game.move(fromPos, toPos);

                }
            }
            return false;
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            return false;
        }
    }

    /**
     * Move checker from the middle of the board to an allowable position
     *
     * @param command the command entered by the user
     */
    private boolean moveFromMiddle(String command) {
        String[] splitCommand;
        int toPos;

        splitCommand = command.split(" ");
        /*
         * checks if there are checkers to move from bar
         * returns false if there are none and thus terminates method
         */
        if (game.getColour().equals(Colour.BLACK) && game.getMiddleBlack().isEmpty()) {
            return false;
        } else if (game.getColour().equals(Colour.WHITE) && game.getMiddleWhite().isEmpty()) {
            return false;
        }
        try {


            if (Integer.parseInt(splitCommand[1]) <= 24 && Integer.parseInt(splitCommand[1]) >= 1) {
                toPos = Integer.parseInt(splitCommand[1]) - 1;
                if (game.getColour().equals(Colour.BLACK)) {
                    toPos = reverse(toPos);
                }
                /*
                 * Checks if arguments entered by user are legal. If not, prints that the move was invalid
                 * calls the 'move' method in class 'Game' to move a checker on the board
                 */

                return game.moveFromMiddle(toPos, game.getColour());
            }
            return false;
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }
    }
}