import com.orbischallenge.engine.gameboard.*;

import java.awt.*;
import java.lang.Math.*;
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

            // calculate normal manhatten distance
            int dx_normal = Math.abs(start.getX()- end.getX());

			return 0;
		}
	}
}