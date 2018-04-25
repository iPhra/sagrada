package it.polimi.se2018.Model.ToolCards;

import it.polimi.se2018.Model.Board;
import it.polimi.se2018.Model.Moves.Move;

abstract public class ToolCard {
    private boolean alreadyUsed; //true if this tool card has already been used once
    private boolean usableAfterDraft; //true if this tool card is usable after placing a die
    private String imagePath;
    private String title;
    private Board board;

    protected ToolCard(String imagePath, String title, Board board) {
        this.imagePath=imagePath;
        this.title=title;
        alreadyUsed = false;
        this.board=board;
    }

    //REMEMBER to set hasPlacedDie and hasUsedCard attributes in Round
    //also, if you call denyNextTurn() from Round, you need to throw an exception if player is in his second turn
    public void useCard(Move move) { //every specific tool card will implement this method differently
    }

    public boolean isAlreadyUsed() {return alreadyUsed;}

    public void setAlreadyUsed() {alreadyUsed=true;}
}