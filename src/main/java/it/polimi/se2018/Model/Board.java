package it.polimi.se2018.Model;

import it.polimi.se2018.Model.Moves.Coordinate;
import it.polimi.se2018.Model.Moves.MoveMessage;
import it.polimi.se2018.Model.Objectives.PublicObjectives.PublicObjective;
import it.polimi.se2018.Model.ToolCards.ToolCard;
import it.polimi.se2018.Utils.Observable;

import java.util.ArrayList;
import java.util.List;

public class Board extends Observable<Board> {
    public static final int COLORSNUMBER = 5; //number of colors in the game, 5 in our instance
    public static final int DICENUMBER = 90; //number of dice in the game, 90 in our instance
    public static final int ROUNDSNUMBER = 10; //number of rounds in one game
    private final int id;
    private Round round;
    private final String imagePath;
    private final int playersNumber;
    private final ArrayList<Player> players; //contiene la mappa di ciascun giocatore
    private final DraftPool draftPool; //draft pool
    private final ToolCard[] toolCards; // sono le toolCards sulla board (il gioco prevede da regolamento che siano 3, ma noi lo facciamo parametrico e quindi estendibile)
    private final PublicObjective[] publicObjectives; //array che contiene le carte degli obbiettivi pubblici
    private final Bag bag; //ha il riferimento al sacchetto dei dadi
    private final RoundTracker roundTracker; //ha il riferimento al roundTracker

    public Board(int id, List<Player> players, String imagePath, ToolCard[] toolCards, PublicObjective[] publicObjectives) {
        this.players = (ArrayList<Player>)players;
        this.imagePath = imagePath;
        this.id=id;
        this.playersNumber=players.size();
        ArrayList<Integer> playersId = new ArrayList<>(playersNumber);
        for (int i=0; i<players.size(); i++) {
            playersId.add(players.get(i).getId());
        }
        round = new Round(playersId, 1);
        draftPool = new DraftPool();
        this.initDraftPool();
        this.toolCards = toolCards;
        this.publicObjectives = publicObjectives;
        bag = new Bag(COLORSNUMBER, DICENUMBER);
        roundTracker = new RoundTracker(ROUNDSNUMBER);
    }

    public DraftPool getDraftPool() {
        return draftPool;
    }

    public Round getRound() {
        return round;
    }

    public Bag getBag() {
        return bag;
    }

    public RoundTracker getRoundTracker() {
        return roundTracker;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public String getImagePath() {
        return imagePath;
    }

    public ToolCard[] getToolCards() {
        return toolCards;
    }

    public PublicObjective[] getPublicObjectives() {
        return publicObjectives;
    }

    public int getId() {
        return id;
    }

    private void initDraftPool() {
        draftPool.fillDraftPool(bag.drawDice(playersNumber));
    }

    public void startRound() {
        initDraftPool();
    }

    private void endRound() {
        if (round.getRoundNumber() == ROUNDSNUMBER) {
            this.endMatch();
        }
        else {
            round = round.changeRound();
            roundTracker.updateRoundTracker((ArrayList<Die>)draftPool.getDraftPool());
            draftPool.emptyDraftPool();
        }
    }

    public void draftDie (Player player, Die die)  { //removes a die from the draft pool and places into one player's map
        draftPool.removeFromDraftPool(die);
        player.setDieInHand(die);
        round.setHasDraftedDie(true);
    }

    public void draftedDieToMap(Player player, int row, int col){
        player.getMap().placeDie(player.getDieInHand(),row,col);
        player.setDieInHand(null);
    }

    public void placeDraftedDie (Player player, Die die, Coordinate finalPosition)  { //standard move of a player, die goes from draftpool to one's map
        //LANCIA ECCEZIONI
        draftedDieToMap(player,finalPosition.getRow(),finalPosition.getCol());
    }

    public void endTurn() {
        if (round.isLastTurn()) {
            this.endRound();
        }
        else {
            round.changeTurn();
        }
    }

    //Player::getFavorPoints() deve essere coerente con ToolCard::isAlreadyUsed()   (o fai tre if o sdoppi il codice)
    //ToolCard::useCard()
    //Player::setFavorPoints()
    //ToolCard::setAlreadyUsed()
    //Round::setHasUsedCard()
    public void useToolCard(MoveMessage move) {
    }

    public void endMatch() {}

    //IMPLEMENTARE TUTTI I METODI COPY PER OGNI CLASSE DA COPIARE
    public ModelView modelViewCopy() {
        ArrayList<Boolean> usedToolCards = new ArrayList<>();
        ArrayList<Square[][]> maps = new ArrayList<>();
        ArrayList<Integer> favorPoints = new ArrayList<>();
        ArrayList<Integer> scores = new ArrayList<>();
        ArrayList<Die> diceInHand = new ArrayList<>();
        for(ToolCard toolCard : toolCards) {
            usedToolCards.add(toolCard.isAlreadyUsed());
        }
        for(Player player : players) {
            maps.add(player.getMap().modelViewCopy());
            favorPoints.add(player.getFavorPoints());
            scores.add(player.getScore());
            diceInHand.add(player.getDieInHand());
        }
        return new ModelView(this.draftPool.modelViewCopy(),this.roundTracker.modelViewCopy(),usedToolCards,maps,favorPoints,scores,diceInHand,this.roundTracker.getTurn());
    }

}
