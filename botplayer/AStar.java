import com.orbischallenge.engine.gameboard.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

public class AStar {


	public static class heuristics{
        /**
         * Returns the shortest manhatten distance from start to end on the game board.
         *
         * @param start Start position
         * @param end Target position
         * @param board The game board (torus)
         * @return shortest manhatten distance between points
         */
		public static int manhattenDistance(Point start, Point end, Gameboard board){
            int width = board.getWidth();
            int height = board.getHeight();

            

			return 0;
		}
	}
}