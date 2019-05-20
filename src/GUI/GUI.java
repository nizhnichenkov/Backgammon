package GUI;

/* Ilya Lyubomirov -- 16205331 */
/* Mikhail Yankelevich -- 16205326 */
/* Svetoslav Nizhnichenkov -- 17712081 */

import GAME.Colour;
import GAME.Commands;
import GAME.EndGame;
import GAME.GameLogic;
import javafx.animation.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

/**
 * This class is a GUI Controller. It is used for communication between the GUI (user) and
 * game logic components. The class implements all methods that update outputs and graphics,
 * and receive inputs.
 */
public class GUI extends Application {

    /**
     * FXML elements
     */
    @FXML
    private AnchorPane loginPane;

    @FXML
    private AnchorPane mainPane;

    @FXML
    private AnchorPane loginBackground;

    @FXML
    private TextField name1;

    @FXML
    private TextField name2;

    @FXML
    private TextField pointsField;

    @FXML
    private ArrayList points;

    @FXML
    private Rectangle BAR0;

    @FXML
    private Rectangle BAR1;

    @FXML
    private ArrayList bars;

    @FXML
    private Rectangle OUT0;

    @FXML
    private Rectangle OUT1;

    @FXML
    private ArrayList outs;

    @FXML
    private TextField inputField;

    @FXML
    private ImageView boardIMG;

    @FXML
    private ImageView die1;

    @FXML
    private ImageView die2;

    @FXML
    private ImageView doublingCube;

    @FXML
    private ImageView ledBlack;

    @FXML
    private ImageView ledWhite;

    @FXML
    private TextArea tArea;

    @FXML
    private TextArea moveArea;

    @FXML
    private Button submitButton;

    @FXML
    private Label die1double;

    @FXML
    private Label die2double;

    @FXML
    private Label pointsBlack;

    @FXML
    private Label pointsWhite;

    @FXML
    private Button rollButton;

    @FXML
    private Menu menuHelp;

    @FXML
    private MenuItem menuStart;


    /**
     * this method gets users' nicknames, max points and calls initialiser method
     * to start the game. it disappears on submit button click
     */
    @FXML
    void getNicknames() {
        if (acceptableNicknames(name1.getText().trim(), name2.getText().trim())) {
            if (acceptablePoints(pointsField.getText().trim())) {
                setMaxPoints(Integer.parseInt(pointsField.getText().trim()));
                loginSlideOut(loginBackground);
                loginSlideOut(loginPane);
                loginBackground.setDisable(true);
                loginPane.setDisable(true);
            } else {
                loginShake(pointsField);
            }
        } else {
            loginShake(name1);
            loginShake(name2);
        }

    }

    /**
     * this method shakes a given node using timeline. used for "shaking off"
     * incorrect login screen inputs
     *
     * @param node any fx node
     */
    private void loginShake(Node node) {
        Timeline tl = new Timeline(
                new KeyFrame(Duration.millis(0), new KeyValue(node.translateXProperty(), 0, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.millis(100), new KeyValue(node.translateXProperty(), -10, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.millis(200), new KeyValue(node.translateXProperty(), 10, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.millis(300), new KeyValue(node.translateXProperty(), -10, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.millis(400), new KeyValue(node.translateXProperty(), 10, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.millis(500), new KeyValue(node.translateXProperty(), -10, Interpolator.EASE_OUT)),
                new KeyFrame(Duration.millis(600), new KeyValue(node.translateXProperty(), 0, Interpolator.EASE_OUT))
        );
        tl.play();
        if (node == pointsField) {
            tl.setOnFinished(event -> {
                pointsField.setText("0");
            });
        } else if (node == name2) {
            tl.setOnFinished(event -> {
                name1.setText("Player1");
                name2.setText("Player2");
            });
        }
    }

    /**
     * this method removes a given pane from the screen using timeline
     *
     * @param node login screen panes
     */
    private void loginSlideOut(Node node) {
        node.translateYProperty().set(0);
        Timeline tl = new Timeline(
                new KeyFrame(Duration.millis(1000), new KeyValue(node.translateYProperty(), mainPane.getHeight(), Interpolator.EASE_OUT))
        );
        tl.play();
        if (node == loginPane) {
            tl.setOnFinished(event -> {
                player1Nickname = name1.getText().trim();
                player2Nickname = name2.getText().trim();
                nameAndFirstThrow();
            });
        }
    }

    /**
     * necessary for dead cube animation
     */
    private boolean cubeActivated = false;

    /**
     * this method adds a transition animation for a doubling cube
     *
     * @param node doubling cube
     */
    private void moveCube(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(700), node);
        if (!cubeActivated) {
            tt.setFromY(0);
            tt.setFromX(0);
            if (logic.getColour() == Colour.WHITE) {
                tt.setToY(-188);
            } else {
                tt.setToY(176);
            }
            tt.setToX(-412);
            cubeActivated = true;
        } else {
            if (logic.getColour() == Colour.WHITE) {
                tt.setFromY(176);
                tt.setToY(-188);
            } else {
                tt.setFromY(-188);
                tt.setToY(176);
            }
        }
        tt.play();
    }

    /**
     * nicknames
     */
    private String player1Nickname, player2Nickname;

    /**
     * checker radius final value and init. group of checkers for output
     */
    private static final int checker_radius = 25;

    /**
     * this is a  group of checkers to print them out
     */
    private Group checkers = new Group();

    /**
     * sets checkers images to fill circles
     */
    private Image checkerWhite = new Image("img/white.png");
    private Image checkerBlack = new Image("img/black.png");

    /**
     * sets dice images to be printed by the GUI
     */
    private Image d1 = new Image("img/d1.png");
    private Image d2 = new Image("img/d2.png");
    private Image d3 = new Image("img/d3.png");
    private Image d4 = new Image("img/d4.png");
    private Image d5 = new Image("img/d5.png");
    private Image d6 = new Image("img/d6.png");
    private Image[] diceImg = new Image[]{d1, d2, d3, d4, d5, d6};

    /**
     * sets doubling cube images to be printed by the GUI
     * also initialises current doubling cube value
     */
    private Image doubling2 = new Image("img/mult2.png");
    private Image doubling4 = new Image("img/mult4.png");
    private Image doubling8 = new Image("img/mult8.png");
    private Image doubling16 = new Image("img/mult16.png");
    private Image doubling32 = new Image("img/mult32.png");
    private Image doubling64 = new Image("img/mult64.png");
    private Image[] doublingImg = new Image[]{doubling2, doubling4, doubling8, doubling16, doubling32, doubling64};
    private int doublingCubeValue = 0;

    /**
     * sets images of the board/led to be changed
     * according to player turns
     */
    private Image boardBack = new Image("img/boardBack.png");
    private Image board = new Image("img/board.png");
    private Image ledOn = new Image("img/ledOn.png");
    private Image ledOff = new Image("img/ledOff.png");

    /**
     * game logic object initialisation
     */
    private GameLogic logic = new GameLogic();

    /**
     * commands object initialisation
     */
    private Commands command = new Commands(logic);

    /**
     * gameEnd object
     */
    private EndGame gameEnd = new EndGame();

    /**
     * boolean is used to allow/disallow highlighting
     */
    private boolean restrictHighlight = false;

    /**
     * boolean for game start inputs
     */
    private boolean gameStarted = false;

    /**
     * values are used for drag & drop events
     * and scoring
     */
    private double initX, initY, newX, newY;
    private int checkerFrom, checkerTo;
    private int fromBar, toBearOff;
    private int maxPoints = 0;
    private boolean doublingOffered = false;
    private boolean diceRolled = false;

    /**
     * GUI constructor method
     */
    public GUI() {
    }

    /**
     * fx start creates fx Stage, elements of which are loaded from FXML file
     * a scene is created from the elements. name is set, window resizing is disallowed
     * the stage is made visible
     */
    @Override
    public void start(Stage stage) throws Exception {
        mainPane = FXMLLoader.load(getClass().getResource("GUI.fxml"));
        Scene scene = new Scene(mainPane);
        stage.setResizable(false);
        stage.setTitle("edarmoc: Backgammon");
        stage.getIcons().add(new Image("img/icon.png"));
        stage.setScene(scene);
        stage.show();
    }

    /**
     * this method checks if inputted @param is an acceptable point value
     *
     * @param text any text
     * @return true if it IS an integer
     */
    private static boolean acceptablePoints(String text) {
        try {
            int i = Integer.parseInt(text);
            if (i > 0) {
                return true;
            }
        } catch (NumberFormatException | NullPointerException e) {
            return false;
        }
        return false;
    }

    /**
     * this method checks if inputted @params are acceptable player nicknames
     *
     * @param name1 nickname 1
     * @param name2 nickname 2
     * @return true if both are acceptable
     */
    private static boolean acceptableNicknames(String name1, String name2) {
        return !name1.equals("") && !name2.equals("") && !name1.equals(name2);
    }

    /**
     * maxPoint setter method
     *
     * @param value value to set
     */
    private void setMaxPoints(int value) {
        maxPoints = value;
        logic.getMatchScore().setMaxScore(maxPoints);
    }

    /**
     * this event handler calls start sequence if any key is typed
     * before beginning of the game
     */
    private EventHandler anyKeyEvent = new EventHandler() {
        @Override
        public void handle(Event event) {
            inputField.removeEventHandler(KeyEvent.KEY_TYPED, anyKeyEvent);
            startSeq();
        }
    };

    /**
     * this method initialises nicknames, starts game inside the logic
     * upon receiving inputs from the FXML actionlistener (login pane)
     */
    private void nameAndFirstThrow() {
        setCurrentPoints(0, 0);
        outputText("Welcome " + player1Nickname + " !\n\tYou play Whites\n" +
                "Welcome " + player2Nickname + " !\n\tYou play Blacks");
        outputText(logic.startGame(player1Nickname, player2Nickname));
        outputText("\nPress any key to begin!\n");
        placeDice(logic.getDiceThrow());
        inputField.addEventHandler(KeyEvent.KEY_TYPED, anyKeyEvent);
    }

    /**
     * this method acts in a similar way to the method above, but for
     * second game onwards
     */
    private void nameAndFirstThrowRestart() {
        inputField.addEventHandler(KeyEvent.KEY_TYPED, anyKeyEvent);
        gameStarted = false;
        doublingOffered = false;
        cubeActivated = false;
        checkers.getChildren().clear();
        mainPane.getChildren().remove(checkers);
        outputText("Welcome " + player1Nickname + " !\n\tYou play Whites\n" +
                "Welcome " + player2Nickname + " !\n\tYou play Blacks");
        outputText(logic.startGame(player1Nickname, player2Nickname));
        outputText("\nPress any key to begin!\n");
        placeDice(logic.getDiceThrow());
        diceRolled = false;
    }

    /**
     * this method calls initialiser method and disables start button
     * when it is pressed
     */
    public void startSeq() {
        inputField.clear();
        inputField.removeEventHandler(KeyEvent.KEY_TYPED, anyKeyEvent);
        logic.setDiceThrow(new int[]{0, 0});
        disableUsedDice();
        initialiseComponents();
        if (!logic.getMatchScore().deadCube()) {
            outputText("\n=========================\nGame Started ! \n\nRoll or Double");
            rollButton.setDisable(false);
        } else {
            outputText("\n=========================\nGame Started ! \n\nAuto-Rolling Dice");
            rollDice();
            printAllMoves();
        }
        forceDoublesOff();
        menuStart.setDisable(true);
        gameStarted = true;
    }

    /**
     * this method enables board components as a part of start
     * sequence. it also auto-rolls dice for the first player
     */
    private void initialiseComponents() {
        for (int i = 0; i < 24; i++) {
            Polygon plg = (Polygon) points.get(i);
            plg.setOnMouseEntered(polygonHighlightOn);
        }
        BAR0.setOnMouseEntered(barHighlightOn);
        BAR1.setOnMouseEntered(barHighlightOn);
        OUT0.setOnMouseEntered(bearOffHighlightOn);
        OUT1.setOnMouseEntered(bearOffHighlightOn);
        removeHighlight();
        restrictHighlight = true;

        for (int pointNo = 0; pointNo < 24; pointNo++) {
            placeChecker(pointNo);
        }

        specialBoardSetup();
        checkers.setDisable(true);
        boardUpdate();
    }

    /**
     * this method rolls dice upon user request
     */
    public void rollDice() {
        removeHighlight();
        restrictHighlight = false;
        checkers.setDisable(false);
        diceRolled = true;
        rollButton.setDisable(true);
        forceDoublesOff();
        logic.throwDice();
        outputText("\n====> Rolled " + logic.getDiceThrow()[0] + "  " + logic.getDiceThrow()[1] + " <====");
        placeDice(logic.getDiceThrow());
        disableUsedDice();
        if (gameStarted)
            printAllMoves();
    }

    /**
     * gets text from input line (text field) and checks for possible commands
     */
    public void scanText() {
        String tmp = inputField.getText().trim();
        if (!tmp.equals("")) {
            tArea.appendText("\n > " + tmp + "\n");
            inputCheck(tmp);
        }
        inputField.clear();
    }

    /**
     * adds a key event to text field so that the scanText method is
     * called when ENTER key is pressed (allows to submit text field
     * by pressing ENTER on keyboard)
     */
    public void submitText() {
        inputField.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                scanText();
            }
        });
    }

    /**
     * creates a commands object to find relevant inputs received from the parameter
     *
     * @param tmp string from input field
     */
    private void inputCheck(String tmp) {
        boolean alive = !logic.getMatchScore().deadCube();
        tmp = tmp.toLowerCase();
        if (gameStarted) {
            if (!diceRolled && alive) {
                switch (tmp) {
                    case "help":
                        help();
                        break;
                    case "quit":
                        closeGame();
                        break;
                    case "roll":
                        rollDice();
                        break;
                    case "double":
                        if (alive) {
                            if (!doublingOffered) {
                                outputText("Offering to double the stakes");
                                logic.getMatchScore().setAgreementToDouble(offerDouble("Do you accept the double?"));
                                if (!logic.getMatchScore().isAgreementToDouble()) {
                                    doublingDenied();
                                } else {
                                    doublingAccepted();
                                }
                            }
                        }
                        break;
                    default:
                        break;
                }
            } else {
                switch (tmp) {
                    case "help":
                        help();
                        break;
                    case "quit":
                        closeGame();
                        break;
                    case "clear":
                        clearText();
                        break;
                    case "next":
                        if (gameEnd.isWinner(logic)) {
                            this.endGame(gameEnd.announceWinner(logic));
                        } else
                            nextTurn();
                        break;
                    default:
                        if (gameEnd.isWinner(logic)) {
                            this.endGame(gameEnd.announceWinner(logic));
                        } else {
                            outputText(command.enterCommand(tmp));
                            printAllMoves();
                            removeHighlight();
                        }
                        disableUsedDice();
                        break;
                }
            }
            boardUpdate();
        } else if (!gameStarted) {
            switch (tmp) {
                case "help":
                    help();
                    break;
                case "quit":
                    closeGame();
                    break;
                case "start":
                    startSeq();
                    break;
                default:
                    break;
            }
        } else {
            if (tmp.equals("quit")) {
                closeGame();
            }
        }
    }

    /**
     * this method updates doubling cube as it is accepted
     * it also auto-rolls dice as no other actions are available
     */
    private void doublingAccepted() {
        outputText("Doubling Accepted!");
        if (logic.getMatchScore().getOwnerColour() == null) {
            logic.getMatchScore().setOwnerColour(logic.getColour());
        }
        if (logic.getMatchScore().roll()) {
            increaseDoublingCube();
        }
        doublingOffered = true;
        outputText("Auto-Rolling Dice");
        rollDice();
    }

    /**
     * this method forces game end when doubling is denied
     */
    private void doublingDenied() {
        doublingOffered = false;
        outputText("Doubling Denied!");
        gameEnd.setForceWin(true);
        endGame(gameEnd.announceWinner(logic));
    }

    /**
     * adds param text to text area (output) and
     * notifies who (which player colour) currently makes a move
     *
     * @param text text to be printed to text area
     */
    private void outputText(String text) {
        if (!text.equals("")) {
            tArea.appendText("\n" + text);
        }
        moveArea.clear();
        moveArea.appendText(logic.getColour() + " Turn" + "\n" + logic.getCurrentPlayer().getName());

        if (logic.getColour() == Colour.BLACK) {
            boardIMG.setImage(boardBack);
            ledWhite.setImage(ledOff);
            ledBlack.setImage(ledOn);
        }
        if (logic.getColour() == Colour.WHITE) {
            boardIMG.setImage(board);
            ledWhite.setImage(ledOn);
            ledBlack.setImage(ledOff);
        }
    }

    /**
     * method changes turn of the player
     */
    private void nextTurn() {
        doublingOffered = false;
        disableUsedDice();
        if (diceRolled) {
            checkers.setDisable(true);
            diceRolled = false;
            rollButton.setDisable(false);
            outputText(command.enterCommand("next"));
            removeHighlight();
            disableUsedDice();
            restrictHighlight = true;
            Colour clr = logic.getMatchScore().getOwnerColour();
            if (clr != logic.getColour() && (clr != null ) &&!logic.getMatchScore().deadCube()) {
                outputText("Auto-Rolling Dice");
                inputCheck("roll");
            } else if (logic.getMatchScore().deadCube()) {
                outputText("Auto-Rolling Dice");
                rollDice();
            }else {
                outputText("Roll or Double");
            }
        }
    }

    /**
     * this method displays an alert asking to double
     *
     * @param text string to display in alert
     */
    private boolean offerDouble(String text) {
        ButtonType agree = new ButtonType("Accept", ButtonBar.ButtonData.YES);
        ButtonType disagree = new ButtonType("Deny", ButtonBar.ButtonData.NO);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, text, agree, disagree);
        alert.setTitle("edarmoc: Doubling Stakes");
        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent()) {
            offerDouble(text);
        } else if (result.get() == agree) {
            doublingOffered = false;
            return true;
        } else {
            doublingOffered = false;
            return false;
        }
        return false;
    }

    /**
     * deletes all text from text area
     */
    public void clearText() {
        tArea.clear();
    }

    /**
     * Close Game - quits app
     */
    public void closeGame() {
        Platform.exit();
    }

    /**
     * outputs a list of command controls for manual input
     * commands are distinct from other user inputs by an exclamation point
     */
    public void help() {
        tArea.appendText("\n=========================\n" +
                "next -> changes current player\n\n" +
                "double -> offers to double the doubling cube\n\n" +
                "roll -> rolls the dice\n\n" +
                "X Y -> moves your checker from point X to point Y\n\n" +
                "X -> makes a move corresponding to letter X\n\n" +
                "bar X -> moves checker from BAR to point X\n\n" +
                "X off -> bears off a checker from point X\n\n" +
                "clear -> clears information panel\n\n" +
                "cheat -> enables cheat layout\n\n" +
                "quit -> exits the game\n=========================\n");
    }

    /**
     * this method creates and displays an alert with given parameter
     *
     * @param text text to be displayed in alert
     */
    private void setAlert(String text) {
        boardUpdate();
        removeHighlight();
        ButtonType agree = new ButtonType("Continue", ButtonBar.ButtonData.YES);
        Alert alert = new Alert(Alert.AlertType.INFORMATION, text, agree);
        alert.setTitle("edarmoc: Alert");
        alert.showAndWait();
    }

    /**
     * this method makes dice used for move(s) disappear
     * it also controls double moves labels
     */
    private void disableUsedDice() {
        int[] arr = logic.getDiceThrow();

        if (arr[0] == 0) {
            die1.setVisible(false);
        }
        if (arr[1] == 0) {
            die2.setVisible(false);
        }

        if (logic.getDoubleMoves() && arr[0] > 0) {
            switch (logic.getDoubleMovesLeft()) {
                case 4:
                    die1double.setVisible(true);
                    die2double.setVisible(true);
                    break;
                case 3:
                    die1double.setVisible(true);
                    die2double.setVisible(false);
                    break;
                default:
                    die1double.setVisible(false);
                    die2double.setVisible(false);
                    break;
            }
        }
    }

    /**
     * this method forces double labels off
     */
    private void forceDoublesOff() {
        die1double.setVisible(false);
        die2double.setVisible(false);
    }

    /**
     * This method shuffles and updates dice images according
     * to arr values. It uses a Timer to set 3 scheduled tasks
     * being: shuffling (random changing of images), placeFinalDice
     * (displaying arr values), and a killswitch to terminate itself.
     *
     * @param arr array values received from Dice throw
     */
    private void placeDice(int[] arr) {
        if (arr[0] > 0 && arr[1] > 0) {
            die1.setVisible(true);
            die2.setVisible(true);
            inputField.setDisable(true);
            submitButton.setDisable(true);

            Timer time = new Timer();
            TimerTask killSwitch = new TimerTask() {
                @Override
                public void run() {
                    inputField.setDisable(false);
                    submitButton.setDisable(false);
                    disableUsedDice();
                    time.cancel();
                }
            };
            TimerTask shuffle = new TimerTask() {
                @Override
                public void run() {
                    die1.setImage(diceImg[(int) (Math.random() * 6)]);
                    die2.setImage(diceImg[(int) (Math.random() * 6)]);
                }
            };

            TimerTask placeFinalDice = new TimerTask() {
                @Override
                public void run() {
                    if (arr[0] > 0)
                        die1.setImage(diceImg[arr[0] - 1]);
                    if (arr[1] > 0)
                        die2.setImage(diceImg[arr[1] - 1]);
                }
            };
            time.schedule(killSwitch, 980);
            time.schedule(placeFinalDice, 965);
            time.scheduleAtFixedRate(shuffle, 80, 80);
        } else {
            disableUsedDice();
            forceDoublesOff();
        }
    }

    /**
     * This method updates doubling cube images/values
     */
    private void increaseDoublingCube() {
        if (doublingCubeValue <= 5) {
            doublingCube.setImage(doublingImg[doublingCubeValue++]);
            int value = (int) Math.pow(2, doublingCubeValue);
            outputText("Doubling cube value: " + value);
            moveCube(doublingCube);
        }
    }

    /**
     * brings doubling cube back to default settings for game restart
     */
    private void resetDoublingCube() {
        doublingOffered = false;
        doublingCubeValue = 0;
        doublingCube.setImage(doubling64);
        TranslateTransition tt = new TranslateTransition(Duration.millis(700));
        tt.setFromX(-412);
        tt.setNode(doublingCube);
        if (logic.getMatchScore().getOwnerColour() == Colour.BLACK) {
            tt.setFromY(-188);
        } else {
            tt.setFromY(176);
        }
        tt.setToY(0);
        tt.setToX(0);
        tt.play();
    }

    /**
     * this method prints all possible moves in current turn to the info panel
     */
    private void printAllMoves() {
        LinkedList<String> legalMoves = new LinkedList<>();
        LinkedList<Integer> barList = logic.movesFromBar();
        LinkedList<Integer> offList = logic.movesBearOff();
        switch (logic.getColour()) {
            case WHITE:
                if (!barList.isEmpty()) {
                    for (Integer values : barList) {
                        if (logic.hitAllowed(values)) {
                            legalMoves.add("Bar-" + (values + 1) + "*");
                        } else {
                            legalMoves.add("Bar-" + (values + 1));
                        }
                    }
                }
                for (int i = 0; i < 24; i++) {
                    LinkedList<Integer> list = logic.movesFromPoint(i);
                    if (!list.isEmpty()) {
                        for (Integer integer : list) {
                            if (logic.hitAllowed(integer)) {
                                legalMoves.add((i + 1) + "-" + (integer + 1) + "*");
                            } else {
                                legalMoves.add((i + 1) + "-" + (integer + 1));
                            }
                        }
                    }
                }
                if (!offList.isEmpty()) {
                    for (Integer integer : offList) {
                        legalMoves.add((integer + 1) + "-Off");
                    }
                }
                break;
            case BLACK:
                if (!barList.isEmpty()) {
                    for (Integer values : barList) {
                        if (logic.hitAllowed(values)) {
                            legalMoves.add("Bar-" + (24 - values) + "*");
                        } else {
                            legalMoves.add("Bar-" + (24 - values));
                        }
                    }
                }
                for (int i = 0; i < 24; i++) {
                    LinkedList<Integer> list = logic.movesFromPoint(i);
                    if (!list.isEmpty()) {
                        for (Integer integer : list) {
                            if (logic.hitAllowed(integer)) {
                                legalMoves.add((24 - i) + "-" + (24 - integer) + "*");
                            } else {
                                legalMoves.add((24 - i) + "-" + (24 - integer));
                            }
                        }
                    }
                }
                if (!offList.isEmpty()) {
                    for (Integer integer : offList) {
                        legalMoves.add((24 - integer) + "-Off");
                    }
                }
                break;
        }
        outputText("\n=========================\nPossible moves: " + legalMoves.size() + "\n");
        for (int i = 0; i < legalMoves.size(); i++) {
            StringBuilder str = new StringBuilder(legalMoves.get(i));
            legalMoves.set(i, str.insert(0, alphabetGenerator(i)).toString());
        }

        if (!legalMoves.isEmpty())
            command.setAllMoves(legalMoves);

        for (String legalMove : legalMoves) {
            outputText(legalMove);
        }

        if (logic.forceMove()) {
            boardUpdate();
            disableUsedDice();
            forceDoublesOff();
            removeHighlight();
            try {
                String[] onlyMove = legalMoves.get(0).split(" ");
                setAlert("Forcing move: " + onlyMove[1]);
            } catch (ArrayIndexOutOfBoundsException ignored) {
            }
            printAllMoves();
        }
        if (gameEnd.isWinner(logic)) {
            this.endGame(gameEnd.announceWinner(logic));
        } else {
            if (legalMoves.isEmpty()) {
                outputText("Passing turn to " + logic.getOtherPlayer().getName() + " (" + logic.getOtherPlayer().getColour() + ")");
                nextTurn();
            }
        }
    }

    /**
     * this recursive method returns necessary alphabetic value
     *
     * @param i position in the English alphabet
     * @return letter/s on @param position
     */
    private static String alphabetGenerator(int i) {
        if (i < 0) {
            return "";
        } else {
            return alphabetGenerator((i / 26) - 1) + (char) (65 + i % 26) + " ";
        }
    }

    /**
     * this method creates and returns effects that are used by other
     * gui methods
     *
     * @param id - id of the effect
     * @return effect
     */
    private Effect getEffects(int id) {
        DropShadow green = new DropShadow();
        green.setColor(Color.valueOf("#2bff00"));
        green.setHeight(25.0);
        green.setWidth(20.0);
        green.setRadius(9.5);
        green.setSpread(0.55);

        DropShadow white = new DropShadow();
        white.setColor(Color.valueOf("#ffffff"));
        white.setHeight(25.0);
        white.setWidth(20.0);
        white.setRadius(9.5);
        white.setSpread(0.55);

        if (id == 0)
            return green;
        if (id == 1)
            return white;
        return null;
    }

    /**
     * this handler calls highlightBearOff method when
     * a mouse enters bear off zone
     */
    private EventHandler<MouseEvent> bearOffHighlightOn =
            e -> {
                if (e.getSource() instanceof Rectangle) {
                    int id = Integer.valueOf(((Rectangle) e.getSource()).getId().substring(3));

                    if (logic.getColour() == Colour.WHITE && id == 0) {
                        highlightBearOff(id);
                    } else if (logic.getColour() == Colour.BLACK && id == 1) {
                        highlightBearOff(id);
                    }
                }
            };

    /**
     * this handler calls highlightBar method when
     * a mouse enters Bar
     */
    private EventHandler<MouseEvent> barHighlightOn =
            e -> {
                if (e.getSource() instanceof Rectangle) {
                    int id = Integer.valueOf(((Rectangle) e.getSource()).getId().substring(3));

                    if (logic.getColour() == Colour.WHITE && id == 0) {
                        highlightBar(id);
                    } else if (logic.getColour() == Colour.BLACK && id == 1) {
                        highlightBar(id);
                    }
                }
            };


    /**
     * this method highlights White Bar by default. if current turn is Black,
     * highlights Black's Bar. it also highlights all points where a move from bar
     * can be made
     */
    private void highlightBar(int barID) {
        if (!restrictHighlight) {
            LinkedList<Integer> barList = logic.movesFromBar();
            try {
                for (int i : barList) {
                    Polygon plg = (Polygon) points.get(i);
                    plg.setEffect(getEffects(0));
                }
                Rectangle Bar = (Rectangle) bars.get(barID);
                Bar.setEffect(getEffects(1));
            } catch (NullPointerException ignored) {
            }
        }
    }

    /**
     * this event extracts the id of the calling element (Polygon) to
     * call highlightPoint
     */
    private EventHandler<MouseEvent> polygonHighlightOn =
            e -> {
                if (e.getSource() instanceof Polygon) {
                    int id = Integer.valueOf(((Polygon) e.getSource()).getId().substring(1)) - 1;
                    highlightPoint(logic.movesFromPoint(id), id);
                }
            };

    /**
     * this method highlights necessary points according to possible moves
     *
     * @param list list of points #s
     */
    private void highlightPoint(LinkedList<Integer> list, int currentID) {
        if (!restrictHighlight) {
            for (int i : list) {
                Polygon plg = (Polygon) points.get(i);
                plg.setEffect(getEffects(0));
            }
            Polygon currentPoint = (Polygon) points.get(currentID);
            currentPoint.setEffect(getEffects(1));
            if (logic.movesBearOffID(currentID)) {
                if (Colour.WHITE == logic.getPoint(currentID).top().getColour()) {
                    highlightBearOff(0);
                } else if (Colour.BLACK == logic.getPoint(currentID).top().getColour()) {
                    highlightBearOff(1);
                }
            }
        }
    }

    /**
     * this method highlights bear off on hover
     */
    private void highlightBearOff(int id) {
        if (!restrictHighlight) {
            if (id == 0) {
                OUT0.setEffect(getEffects(0));
            } else if (id == 1) {
                OUT1.setEffect(getEffects(0));
            }
        }
    }

    /**
     * this method removes highlights from all points/bars/outs
     */
    public void removeHighlight() {
        if (!restrictHighlight) {
            for (int i = 0; i < 24; i++) {
                Polygon plg = (Polygon) points.get(i);
                plg.setEffect(null);
            }
            for (int i = 0; i < 2; i++) {
                Rectangle rct = (Rectangle) bars.get(i);
                Rectangle rctOut = (Rectangle) outs.get(i);
                rct.setEffect(null);
                rctOut.setEffect(null);
            }
        }
    }

    /**
     * this method updates the board based on Point #. Switch is used to divide the
     * board into 4 parts (home, outer board, etc). This division is used for
     * placing checkers on the board using checkerGenerator method
     *
     * @param id point #
     */
    private void placeChecker(int id) {
        switch (id / 6) {
            case 0:
                checkerGenerator(804, 686, 0, id);
                break;
            case 1:
                checkerGenerator(385, 686, 6, id);
                break;
            case 2:
                checkerGenerator(130, 81, 12, id);
                break;
            default:
                checkerGenerator(548, 81, 18, id);
                break;
        }
    }

    /**
     * this method creates a checker on the board based on parameters received
     * from placeChecker method
     *
     * @param posX    X position on the pane
     * @param posY    Y position on the pane
     * @param prevIDs min number of previous points
     * @param id      current point #
     */
    private void checkerGenerator(int posX, int posY, int prevIDs, int id) {

        if (logic.getPoint(id).isEmpty())
            return;

        int howManyToPrint = logic.getPoint(id).size();

        for (int i = 0; i < howManyToPrint; i++) {

            Circle checker = new Circle();
            checker.setCursor(Cursor.OPEN_HAND);
            checker.setOnMousePressed(checkerOnMousePressed);
            checker.setOnMouseDragged(checkerOnMouseDragged);
            checker.setOnMouseReleased(checkerOnMouseReleased);
            checker.setOnMouseEntered(highlightOnMouseOver);
            checker.setOnMouseExited(highlightOnMouseExited);

            if (prevIDs < 12) {
                checker.setLayoutY(posY - ((checker_radius * 2 - 2) * (i)));
                checker.setLayoutX(posX - (id - prevIDs) * 51);
            } else {
                checker.setLayoutY(posY + ((checker_radius * 2 - 2) * (i)));
                checker.setLayoutX(posX + (id - prevIDs) * 51);
            }
            checker.setRadius(checker_radius);
            if (logic.getPoint(id).top().getColour() == Colour.BLACK) {
                checker.setFill(new ImagePattern(checkerBlack));
            } else if (logic.getPoint(id).top().getColour() == Colour.WHITE) {
                checker.setFill(new ImagePattern(checkerWhite));
            }
            checkers.getChildren().add(checker);
        }
    }

    /**
     * method sets up BAR and OUT elements of the board
     */
    private void specialBoardSetup() {
        for (int i = 0; i < logic.getMiddleBlack().size(); i++) {
            specialCheckerGenerator(463, 333, (checker_radius - 8) * i * (-1), Colour.BLACK);
        }
        for (int i = 0; i < logic.getMiddleWhite().size(); i++) {
            specialCheckerGenerator(463, 438, (checker_radius - 8) * i, Colour.WHITE);
        }
        for (int i = 0; i < logic.getBlackOut().size(); i++) {
            specialCheckerGenerator(879, 338, (checker_radius - 7) * i * (-1), Colour.BLACK);
        }
        for (int i = 0; i < logic.getWhiteOut().size(); i++) {
            specialCheckerGenerator(879, 426, (checker_radius - 7) * i, Colour.WHITE);
        }
    }

    /**
     * this method creates special (on Bar and Bear-off) checkers on the board
     * based on parameters received from specialBoardSetup method
     *
     * @param posX   X position on the pane
     * @param posY   Y position on the pane
     * @param margin positioning adjustment
     */
    private void specialCheckerGenerator(int posX, int posY, int margin, Colour colour) {
        Circle checker = new Circle();
        checker.setCursor(Cursor.OPEN_HAND);
        checker.setOnMousePressed(checkerOnMousePressed);
        checker.setOnMouseDragged(checkerOnMouseDragged);
        checker.setOnMouseReleased(checkerOnMouseReleased);
        checker.setOnMouseEntered(highlightOnMouseOver);
        checker.setOnMouseExited(highlightOnMouseExited);
        checker.setLayoutY(posY + margin);
        checker.setLayoutX(posX);
        checker.setRadius(checker_radius);

        if (colour == Colour.WHITE) {
            checker.setFill(new ImagePattern(checkerWhite));
        } else if (colour == Colour.BLACK) {
            checker.setFill(new ImagePattern(checkerBlack));
        }

        checkers.getChildren().add(checker);
    }

    /**
     * this method removes all checker elements from the group and pane
     * and re-fills the pane with updated checker layout. Then
     * adds checkers back to the group and pane to be displayed
     */
    private void boardUpdate() {
        mainPane.getChildren().remove(checkers);
        checkers.getChildren().clear();
        for (int pointNo = 0; pointNo < 24; pointNo++) {
            placeChecker(pointNo);
        }
        specialBoardSetup();
        mainPane.getChildren().add(checkers);
    }

    /**
     * gets initial positions (X,Y) of the checker and passes them to
     * posPointCheck, that returns point # where this checker is located
     */
    private EventHandler<MouseEvent> checkerOnMousePressed =
            new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent e) {
                    restrictHighlight = true;
                    initX = e.getSceneX();
                    initY = e.getSceneY();
                    newX = ((Circle) (e.getSource())).getTranslateX();
                    newY = ((Circle) (e.getSource())).getTranslateY();
                    ((Circle) (e.getSource())).setCursor(Cursor.CLOSED_HAND);
                    double centerX = (((Circle) (e.getSource())).getBoundsInParent().getMinX() + ((Circle) (e.getSource())).getBoundsInParent().getMaxX()) / 2;
                    double centerY = (((Circle) (e.getSource())).getBoundsInParent().getMinY() + ((Circle) (e.getSource())).getBoundsInParent().getMaxY()) / 2;
                    checkerFrom = posPointCheck(centerX, centerY);
                    fromBar = specialPosCheck(centerX, centerY, 0);
                }
            };

    /**
     * dynamically changes layout of the checker that is currently moved
     */
    private EventHandler<MouseEvent> checkerOnMouseDragged =
            new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent e) {
                    ((Circle) (e.getSource())).setTranslateX(newX + (e.getSceneX() - initX));
                    ((Circle) (e.getSource())).setTranslateY(newY + (e.getSceneY() - initY));
                }
            };

    /**
     * gets final positions (X,Y) of the checker and passes them to
     * posPointCheck, that returns point # where this checker is moved.
     * finally, calls positionChange to check is moving is allowed
     */
    private EventHandler<MouseEvent> checkerOnMouseReleased =
            new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent e) {
                    double centerX = (((Circle) (e.getSource())).getBoundsInParent().getMinX() + ((Circle) (e.getSource())).getBoundsInParent().getMaxX()) / 2;
                    double centerY = (((Circle) (e.getSource())).getBoundsInParent().getMinY() + ((Circle) (e.getSource())).getBoundsInParent().getMaxY()) / 2;
                    checkerTo = posPointCheck(centerX, centerY);
                    toBearOff = specialPosCheck(centerX, centerY, 1);
                    restrictHighlight = false;
                    positionChange();
                }
            };


    /**
     * this method returns id of the point which satisfies given checker's (X, Y)
     *
     * @param checkerX X layout (in parent) of the checker
     * @param checkerY Y layout (in parent) of the checker
     * @return point # (which satisfies X,Y above)
     */
    private int posPointCheck(double checkerX, double checkerY) {

        double minBoundX, minBoundY, maxBoundX, maxBoundY;
        int pointNo = 0;

        while (pointNo < 24) {

            Polygon plg = (Polygon) points.get(pointNo);

            minBoundX = plg.getBoundsInParent().getMinX();
            minBoundY = plg.getBoundsInParent().getMinY();
            maxBoundX = plg.getBoundsInParent().getMaxX();
            maxBoundY = plg.getBoundsInParent().getMaxY();

            if (checkerX >= minBoundX && checkerX <= maxBoundX && checkerY >= minBoundY && checkerY <= maxBoundY) {
                return pointNo;
            } else {
                pointNo++;
            }
        }
        return -1;
    }

    /**
     * this method checks if a given checker is on bar/bear off zone
     *
     * @param checkerX X layout (in parent) of the checker
     * @param checkerY Y layout (in parent) of the checker
     * @return returns index of the special bar/off if a checker is there
     */
    private int specialPosCheck(double checkerX, double checkerY, int kind) {
        double minBoundX, minBoundY, maxBoundX, maxBoundY;
        int specialNo = 0;
        Rectangle rct;

        while (specialNo < 2) {

            if (kind == 0) {
                rct = (Rectangle) bars.get(specialNo);
            } else {
                rct = (Rectangle) outs.get(specialNo);
            }

            minBoundX = rct.getBoundsInParent().getMinX();
            minBoundY = rct.getBoundsInParent().getMinY();
            maxBoundX = rct.getBoundsInParent().getMaxX();
            maxBoundY = rct.getBoundsInParent().getMaxY();

            if (checkerX >= minBoundX && checkerX <= maxBoundX && checkerY >= minBoundY && checkerY <= maxBoundY) {
                return specialNo;
            } else {
                specialNo++;
            }
        }
        return -1;
    }

    /**
     * this method checks if moving is possible using new
     * data registered by event handlers above. updates the
     * layout if move is possible
     */
    private void positionChange() {
        Colour clrFrom = logic.getColour();
        int from = -1, to = -1;

        if (fromBar == -1) {
            try {
                clrFrom = logic.getPoint(checkerFrom).top().getColour();
            } catch (ArrayIndexOutOfBoundsException ignored) {

            }
        }

        if (logic.getColour() != clrFrom) {
            boardUpdate();
            setAlert("Wrong checker colour!");
            return;
        }

        if (clrFrom == Colour.WHITE) {
            from = checkerFrom + 1;
            to = checkerTo + 1;
        } else if (clrFrom == Colour.BLACK) {
            from = 24 - checkerFrom;
            to = 24 - checkerTo;
        }

        if (fromBar == 0 && clrFrom == Colour.WHITE) {
            outputText(command.enterCommand("Bar " + to));
        } else if (fromBar == 1 && clrFrom == Colour.BLACK) {
            outputText(command.enterCommand("Bar " + to));
        } else if (toBearOff == 0 && clrFrom == Colour.WHITE) {
            outputText(command.enterCommand(from + " Off"));
        } else if (toBearOff == 1 && clrFrom == Colour.BLACK) {
            outputText(command.enterCommand(from + " Off"));
        } else {
            outputText(command.enterCommand(from + " " + to));
        }
        printAllMoves();
        disableUsedDice();
        boardUpdate();
    }

    /**
     * highlights points the similarly to highlightPointOn, but using
     * parameters from event handlers above. it is applied to checkers
     */
    private EventHandler<MouseEvent> highlightOnMouseOver =
            e -> {
                double centerX = (((Circle) (e.getSource())).getBoundsInParent().getMinX() + ((Circle) (e.getSource())).getBoundsInParent().getMaxX()) / 2;
                double centerY = (((Circle) (e.getSource())).getBoundsInParent().getMinY() + ((Circle) (e.getSource())).getBoundsInParent().getMaxY()) / 2;

                int bar = specialPosCheck(centerX, centerY, 0);
                int bearOff = specialPosCheck(centerX, centerY, 1);
                int id = posPointCheck(centerX, centerY);

                if (id != -1) {
                    highlightPoint(logic.movesFromPoint(id), id);
                    if (bearOff != 1) {
                        highlightBearOff(bearOff);
                    }
                }
                if (bar == 0 && logic.getColour() == Colour.WHITE) {
                    highlightBar(bar);
                } else if (bar == 1 && logic.getColour() == Colour.BLACK) {
                    highlightBar(bar);
                }
            };

    /**
     * removes highlights when mouse exits layout of the checker
     */
    private EventHandler<MouseEvent> highlightOnMouseExited =
            e -> removeHighlight();

    /**
     * disables gui/logic components and shows winner if the game is ended
     *
     * @param text game results
     */
    private void endGame(String text) {
        logic.getMatchScore().setScore(logic.getMatchScore().calculateScoreFullWin());
        outputText("Game Results\n" + player1Nickname + " : " + logic.getMatchScore().getScore()[0] + " / " + maxPoints +
                "\n" + player2Nickname + " : " + logic.getMatchScore().getScore()[1] + " / " + maxPoints);
        outputText("\n=========================\nGame Ended!\n\nPress Next Game button to continue\n=========================");
        int[] arr = logic.getMatchScore().getScore();
        setCurrentPoints(arr[0], arr[1]);
        if (!endMatch()) {
            nextGameAlert(text + "\nThis alert will auto-close in a bit");
            restrictHighlight = true;
            checkers.setDisable(true);
            gameStarted = false;
        }
    }

    /**
     * this method re-starts the board elements for a new game in a match
     */
    private void restartGame() {
        rollButton.setDisable(true);
        menuHelp.setDisable(false);
        menuStart.setDisable(false);
        mainPane.getChildren().removeAll(checkers);
        resetDoublingCube();
        logic.resetGame();
        gameEnd.setForceWin(false);
        logic.getMatchScore().checkCrawford();
        removeHighlight();
        nameAndFirstThrowRestart();
    }

    /**
     * disables gui/logic components and shows winner if the match is ended
     */
    private boolean endMatch() {
        if (logic.getMatchScore().isWon()) {
            menuHelp.setDisable(true);
            restrictHighlight = true;
            checkers.setDisable(true);
            int[] arr = logic.getMatchScore().getScore();
            setCurrentPoints(arr[0], arr[1]);
            outputText("\n=========================\nGame Results\n" + player1Nickname + " : " + logic.getMatchScore().getScore()[0] + " / " + maxPoints +
                    "\n" + player2Nickname + " : " + logic.getMatchScore().getScore()[1] + " / " + maxPoints);
            outputText("\n=========================\nMatch Ended!\n\nEnter quit or choose Close Game option from the Menu\n=========================");
            playAgainAlert();
            return true;
        }
        return false;
    }

    /**
     * this method updates current score on the board based on given @param
     *
     * @param i whites player score
     * @param j blacks player score
     */
    private void setCurrentPoints(int i, int j) {
        pointsBlack.setText(j + " / " + maxPoints);
        pointsWhite.setText(i + " / " + maxPoints);
    }

    /**
     * this method displays an alert after a game has finished and asks
     * players to confirm to start the next game in the match
     *
     * @param text string to display on alert
     */
    private void nextGameAlert(String text) {
        ButtonType agree = new ButtonType("Next Game", ButtonBar.ButtonData.YES);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, text, agree);
        alert.setHeaderText("WINNER IS:");
        alert.setTitle("edarmoc: Game Result");
        alert.show();
        PauseTransition delay = new PauseTransition(Duration.millis(2500));
        delay.setOnFinished(e -> {
            alert.close();
            restartGame();
        });
        delay.play();
    }

    /**
     * this method displays an alert after the match has finished asking if
     * players would like to re-match
     */
    private void playAgainAlert() {
        boardUpdate();
        removeHighlight();
        ButtonType agree = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType disagree = new ButtonType("No", ButtonBar.ButtonData.NO);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Thank you for playing!\n" +
                "Would you like to start another match?", agree, disagree);
        alert.setTitle("edarmoc: Match Result");
        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent()) {
            playAgainAlert();
        } else if (result.get() == agree) {
            checkers.getChildren().clear();
            mainPane.getChildren().remove(checkers);
            logic.emptyLayout();
            boardUpdate();
            Stage newstage = new Stage();
            try {
                start(newstage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            closeGame();
        }
    }
}