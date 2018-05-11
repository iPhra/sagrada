package it.polimi.se2018.model.placementlogic;

import it.polimi.se2018.model.Die;
import it.polimi.se2018.model.Map;
import it.polimi.se2018.network.messages.Coordinate;

public class DiePlacerNormal extends DiePlacer {

    public DiePlacerNormal(Die die, Coordinate coordinate, Map map) {
        super(die,coordinate,map);
    }

    @Override
    public boolean checkCondition() {
        return square.isEmpty() && square.sameColor(die) && square.sameValue(die) && isValueOk() && isColorOk()  && hasSurroundingDice();
    }
}