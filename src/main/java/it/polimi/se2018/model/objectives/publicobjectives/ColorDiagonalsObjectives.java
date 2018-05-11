package it.polimi.se2018.model.objectives.publicobjectives;

import it.polimi.se2018.model.Die;
import it.polimi.se2018.model.Map;
import it.polimi.se2018.model.Player;
import it.polimi.se2018.model.Square;
import it.polimi.se2018.network.messages.Coordinate;

import java.util.ArrayList;
import java.util.List;

public class ColorDiagonalsObjectives extends PublicObjective {
    private static ColorDiagonalsObjectives instance = null;
    private boolean[][] alreadyCounted;  //needed in order not to count the same die twice while evaluating points

    private ColorDiagonalsObjectives(String imagePath, String title){
        super(imagePath,title);
        alreadyCounted = new boolean[4][5];
        for (int row=0; row<4; row++) {
            for (int col=0; col<5; col++) {
                alreadyCounted[row][col]=false;
            }
        }
    }

    private static synchronized ColorDiagonalsObjectives createInstance(String imagePath, String title){
        if (instance==null) instance = new ColorDiagonalsObjectives(imagePath, title);
        return instance;
    }

    public ColorDiagonalsObjectives instance(String imagePath, String title){
        if (instance==null) createInstance(imagePath, title);
        return instance;
    }

    //returns dice placed in the 2 diagonals below given position
    private List<Die> belowDiagonalsDice(Player player, int row, int col){
        ArrayList<Die> belowDiagonals = new ArrayList<>();
        if (row < player.getMap().getRows()-1){
            if (col > 0 && !player.getMap().getSquare(new Coordinate(row+1,col-1)).isEmpty() && !alreadyCounted[row+1][col-1]){
                belowDiagonals.add(player.getMap().getSquare(new Coordinate(row+1,col-1)).getDie());
                alreadyCounted[row+1][col-1] = true;
            }
            if (col < player.getMap().getCols()-1 && !player.getMap().getSquare(new Coordinate(row+1,col+1)).isEmpty() && !alreadyCounted[row+1][col+1]){
                belowDiagonals.add(player.getMap().getSquare(new Coordinate(row+1,col+1)).getDie());
                alreadyCounted[row+1][col+1] = true;
            }
        }
        return belowDiagonals;
    }

    @Override
    public int evalPoints(Player player){
        Map map = player.getMap();
        int points = 0;
        for (Square square : map) {
            if (square.getDie() != null) {
                ArrayList<Die> belowDiagonalsDice = (ArrayList<Die>)belowDiagonalsDice(player,square.getRow(),square.getCol());
                for (Die die : belowDiagonalsDice) {
                    if (die.getColor() == square.getDie().getColor()) points++;
                }
            }
        }
        return points;
    }

}