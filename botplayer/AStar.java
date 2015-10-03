import com.orbischallenge.engine.gameboard.*;
import com.orbischallenge.engine.gameboard.Gameboard;
import com.orbischallenge.game.enums.*;
import com.orbischallenge.game.enums.Move;
import sun.reflect.generics.tree.Tree;

import java.awt.*;
import java.util.*;
import java.util.List;

public class AStar {
    private Queue<Move> moveQueue;
    private Heuristic heuristic;

    public AStar(Heuristic heuristic){
        this.heuristic = heuristic;
        moveQueue = new ArrayDeque<>();
    }

    public AStar(Heuristic heuristic, Queue<Move> moveQueue){
        this.heuristic = heuristic;
        this.moveQueue = moveQueue;
    }

    /**
     * Uses A* to find a path from A to B. Ignores bullets, players and lasers.
     *
     * @param start
     * @param end
     * @param gameboard
     */
    public void findPath(Point start, Point end, Gameboard gameboard){
        PriorityQueue<>
    }

    private List<Move> getMoves(){
        return null;
    }

    private static class Node implements Comparable<Node>{
        private Node parent;
        private List<Node> neighbours;
        private boolean visited;
        private float distanceFromStart;
        private float heuristicDistanceFromGoal;
        private Point coords;
        private boolean isObstacle;
        private boolean isStart;
        private boolean isGoal;

        public int compareTo(Node other){
            float thisTotalDistanceFromGoal = heuristicDistanceFromGoal + distanceFromStart;
            float otherTotalDistanceFromGoal = other.getHeuristicDistanceFromGoal() + other.getDistanceFromStart();

            if (thisTotalDistanceFromGoal < otherTotalDistanceFromGoal) {
                return -1;
            } else if (thisTotalDistanceFromGoal > otherTotalDistanceFromGoal) {
                return 1;
            } else {
                return 0;
            }
        }

        /**
         *  Getters and Setters
         */

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public List<Node> getNeighbours() {
            return neighbours;
        }

        public void setNeighbours(List<Node> neighbours) {
            this.neighbours = neighbours;
        }

        public boolean isVisited() {
            return visited;
        }

        public void setVisited(boolean visited) {
            this.visited = visited;
        }

        public float getDistanceFromStart() {
            return distanceFromStart;
        }

        public void setDistanceFromStart(float distanceFromStart) {
            this.distanceFromStart = distanceFromStart;
        }

        public float getHeuristicDistanceFromGoal() {
            return heuristicDistanceFromGoal;
        }

        public void setHeuristicDistanceFromGoal(float heuristicDistanceFromGoal) {
            this.heuristicDistanceFromGoal = heuristicDistanceFromGoal;
        }

        public Point getCoords() {
            return coords;
        }

        public void setCoords(Point coords) {
            this.coords = coords;
        }

        public boolean isObstacle() {
            return isObstacle;
        }

        public void setIsObstacle(boolean isObstacle) {
            this.isObstacle = isObstacle;
        }

        public boolean isStart() {
            return isStart;
        }

        public void setIsStart(boolean isStart) {
            this.isStart = isStart;
        }

        public boolean isGoal() {
            return isGoal;
        }

        public void setIsGoal(boolean isGoal) {
            this.isGoal = isGoal;
        }


    }

    public interface Heuristic{
        int getDistance(Point start, Point end, int width, int height);
    }

	public static class ManhattenDistance implements Heuristic{
        /**
         * Returns the shortest manhatten distance from start to end on the game board.
         *
         * @param start Start position
         * @param end Target position
         * @param board The game board (torus)
         * @return shortest manhatten distance between points
         */
		public int getDistance(Point start, Point end, int width, int height){
            // calculate normal manhatten distance
            int dx_normal = Math.abs(start.x - end.x);
            int dy_normal = Math.abs(start.y - end.y);

            // calculate wrap around manhatten distances in x and y
            int dx_wrap = width - 1 - dx_normal;
            int dy_wrap = height - 1 - dy_normal;

            return Math.min(dx_normal, dx_wrap) + min(dy_normal, dy_wrap);
		}
	}
}