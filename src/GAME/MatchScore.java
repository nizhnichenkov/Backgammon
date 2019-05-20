package GAME;

/* Ilya Lyubomirov -- 16205331 */
/* Mikhail Yankelevich -- 16205326 */
/* Svetoslav Nizhnichenkov -- 17712081 */

/**
 * this class implements necessary match score logic elements
 */
public class MatchScore extends DoublingCube {
    GameLogic game;

    private boolean Crawford = false;
    private int[] crawfordScore = {-1, -1};
    private int prevCauseId = -1;

    public int[] getScore() {
        return score;
    }

    public void setScore(int[] score) {
        this.score = score;
    }

    private int[] score = {0, 0};

    MatchScore(GameLogic game) {
        this.game = game;
    }

    private boolean inOpponentsHomeBoard(Colour colour) {

        if (colour.equals(Colour.WHITE)) {
            for (int i = 0; i < 5; i++) {
                if (!game.getPoint(i).isEmpty())
                    if (game.getPoint(i).top().getColour().equals(colour))
                        return true;
            }
            return false;
        }
        if (colour.equals(Colour.BLACK)) {
            for (int i = 18; i < 23; i++) {
                if (!game.getPoint(i).isEmpty())
                    if (game.getPoint(i).top().getColour().equals(colour))
                        return true;
            }
            return false;
        }
        return false;
    }


    private int[] calculateScoreCurr() {
        int[] i = {0, 0};

        if (game.getWhiteOut().isEmpty() && agreementToDouble) {
            if (!game.getMiddleBlack().isEmpty() && !inOpponentsHomeBoard(Colour.BLACK))
                i[0] = 2 * doublingCubeValue;
            else
                i[0] = 3 * doublingCubeValue;
        } else
            i[0] = 1 * doublingCubeValue;

        if (game.getBlackOut().isEmpty() && agreementToDouble) {
            if (!game.getMiddleWhite().isEmpty() && !inOpponentsHomeBoard(Colour.WHITE))
                i[1] = 2 * doublingCubeValue;
            else
                i[1] = 3 * doublingCubeValue;
        } else
            i[1] = doublingCubeValue;

        return i;
    }

    public int[] calculateScoreFull() {
        int[] i = {doublingCubeValue, doublingCubeValue};
        i[0] += this.score[0];
        i[1] += this.score[1];
        System.out.println();

        return i;
    }

    public int[] calculateScoreFullWin() {
        int[] i = calculateScoreCurr();
        if (game.getColour().equals(Colour.WHITE)) {
            i[0] += this.score[0];
            i[1] = this.score[1];
        } else {
            i[0] = this.score[0];
            i[1] += this.score[1];
        }
        return i;
    }


    public boolean doubleCube() {

        return (doublingCubeValue + this.score[1] <= maxScore && doublingCubeValue + this.score[0] <= maxScore) && agreementToDouble;
    }

    public boolean deadCube() {
        if (Crawford)
            return true;
        doubleCubeValue();
        if (doubleCube()) {
            doublingCubeValue /= 2;
            return false;
        }
        doublingCubeValue /= 2;
        return true;
    }


    private boolean agreementToDouble = true;

    public boolean isAgreementToDouble() {
        return agreementToDouble;
    }

    public void setAgreementToDouble(boolean agreementToDouble) {
        this.agreementToDouble = agreementToDouble;
    }

    public boolean isMatchWon() {
        if (game.getColour().equals(Colour.WHITE))
            return this.score[0] >= this.maxScore;
        else
            return this.score[1] >= this.maxScore;
    }

    /**
     * Rolls the doubling cube
     */
    public boolean roll() {
        if (game.getColour().equals(ownerColour) && !Crawford) {
            if (doubleCubeValue()) {
                if (doubleCube()) {
                    super.changeOwnerColour();
                    return true;
                } else
                    doublingCubeValue /= 2;
            }
        }
        return false;
    }

    /**
     * checks whether game has been won by either player
     *
     * @return true if 15 checkers belonging to either player are off board, false otherwise
     */
    public boolean isWon() {
        return this.score[0] >= maxScore || this.score[1] >= maxScore;
    }

    public void setCrawford(boolean crawford) {
        Crawford = crawford;
    }

    public void checkCrawford() {
        if ((this.score[0] + 1 == maxScore || this.score[1] + 1 == maxScore) && (this.crawfordScore[0] == -1 && this.crawfordScore[1] == -1)) {
            if (this.score[0] + 1 == maxScore)
                this.prevCauseId = 0;
            else this.prevCauseId = 1;
            setCrawford(true);
            this.crawfordScore[1] = this.score[1];
            this.crawfordScore[0] = this.score[0];
            return;
        }

        if ((this.score[0] + 1 == maxScore || this.score[1] + 1 == maxScore) && !(this.crawfordScore[0] == this.score[0] && this.crawfordScore[1] == this.score[1])) {
            setCrawford(false);
        }
    }
}
