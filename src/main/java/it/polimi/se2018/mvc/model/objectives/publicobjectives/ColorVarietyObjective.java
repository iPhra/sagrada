package it.polimi.se2018.mvc.model.objectives.publicobjectives;

import it.polimi.se2018.mvc.model.Color;
import it.polimi.se2018.mvc.model.Player;
import it.polimi.se2018.mvc.model.Square;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This is the color variety objective card. It extends {@link PublicObjective}
 * Design pattern Singleton is used in this class, because only one instance is used in every running game
 */
public class ColorVarietyObjective extends PublicObjective {
    private static ColorVarietyObjective instance = null;

    private ColorVarietyObjective(String imagePath) {
        super("Color Variety","4 points for every set of one of each color anywhere",imagePath);
    }

    /**
     * @return a new instance of this card if does not exist, the existing instance otherwise (as expected in the
     * Singleton pattern)
     */
    public static ColorVarietyObjective instance() {
        if (instance == null) instance = new ColorVarietyObjective("/objectives/public_objectives/color_variety.png");
        return instance;
    }

    /**
     * This method returns a predicate, it contains a lambda function that checks if given color is the color of the die
     * of a square
     * @param color it's the color that method must check if it's present in the die of the square
     * @return the result of the lambda function
     */
    private Predicate<Square> checkIfContainsColor(final Color color) {
        return (square -> square.getDie() != null && square.getDie().getColor() == color);
    }

    /**
     * The points are evaluated as it follows: this objective gives 4 points to the player for each set of one of each
     * color anywhere on his window
     * @param player the player whose points must be evaluated
     * @return the points given by this card to the player
     */
    @Override
    public int evalPoints(Player player) {
        Optional<Integer> min = (Stream.of(Color.BLUE,Color.GREEN,Color.RED,Color.PURPLE,Color.YELLOW)
                .map(color -> ( (int)StreamSupport.stream(player.getWindow().spliterator(), false)
                        .filter(checkIfContainsColor(color))
                        .count()))
                .reduce(Integer::min));
        return (min.map(integer -> integer * 4).orElse(0));

    }
}

